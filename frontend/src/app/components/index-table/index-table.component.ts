import {
  booleanAttribute,
  Component,
  OnInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule, Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { IndexService } from '../../services/index/index.service';
import { DataService } from '../../services/data/data.service';
import { Table } from '../../models/table.model';
import { getUniqueElements } from '../../utils/Utils';
import { Toast } from 'bootstrap';
import { Notification } from '../../models/notification.model';
import {
  MdbModalRef,
  MdbModalService,
  MdbModalModule,
} from 'mdb-angular-ui-kit/modal';
import { ModalComponent } from '../modal-delete/modal.component';

import { Index } from '../../models/index.model';
@Component({
  selector: 'app-index-table',
  standalone: true,
  imports: [FormsModule, CommonModule, MdbModalModule],
  templateUrl: './index-table.component.html',
  styleUrl: './index-table.component.scss',
})
export class IndexTableComponent {
  preRows: Index[] = [];
  rows: Index[] = [];

  typeOfDatabase: string = '';

  numberChanged: number = 0;
  changedList: [number, number][] = [];

  btnAddStatus: boolean = false;
  btnSaveStatus: boolean = true;

  alertPlaceholder: HTMLElement | null = null;
  alertTrigger: HTMLElement | null = null;
  liveToast: ElementRef | null = null;

  actionInsert: boolean = false;
  actionUpdate: boolean = false;
  actionDelete: boolean = false;

  isLoading: boolean = false;
  isDone: boolean = false;
  status: string = '';
  message: string = '';

  contentText: string = '';

  table: Table = new Table();

  modifyingField: string = '';
  preFieldChange: { rowId: number; fieldId: number; fieldName: string } = {
    rowId: -1,
    fieldName: '0',
    fieldId: 0,
  };
  fieldChange: { rowId: number; fieldId: number; fieldName: string } = {
    rowId: 0,
    fieldName: '0',
    fieldId: 0,
  };

  // Danh sách các kiểu dữ liệu trong MySQL
  listDataTypes: string[] = [];
  listNumericDataTypes: string[] = [];
  listDataTypesWithoutAutoIncrement: string[] = [];
  listDataTypesNeedSize: string[] = [];

  // Notification

  @ViewChild('toastFail', { static: true }) toastFailEl: any;
  @ViewChild('toastSuccess', { static: true }) toastSuccessEl: any;

  toastFail: any;
  toastSuccess: any;

  failInformation: Notification = new Notification();
  successInformation: Notification = new Notification();

  // Modal to confirm delete hoặc làm các thứ khác bla bla

  modalRef: MdbModalRef<ModalComponent> | null = null;

  constructor(
    private indexService: IndexService,
    private dataService: DataService,
    private modalService: MdbModalService
  ) {
    if (
      this.dataService.getData('type') === 'mysql' ||
      this.dataService.getData('type') === 'mariadb'
    ) {
      this.listDataTypes = this.dataService.mysqlDataTypes;
      this.listNumericDataTypes = this.dataService.mysqlNumericDataTypes;
      this.listDataTypesWithoutAutoIncrement =
        this.dataService.mysqlDataTypesWithoutAutoIncrement;
      this.listDataTypesNeedSize = this.dataService.mysqlDataTypesNeedSize;
    }
  }

  ngAfterViewInit() {
    this.alertPlaceholder = document.getElementById('liveAlertPlaceholder');
    this.alertTrigger = document.getElementById('liveAlertBtn');
  }

  ngOnInit(): void {
    this.successInformation.title = 'Thành công';
    this.failInformation.title = 'Thất bại';

    this.toastFail = new Toast(this.toastFailEl.nativeElement, {});
    this.toastSuccess = new Toast(this.toastSuccessEl.nativeElement, {});

    this.isLoading = true;

    this.table = JSON.parse(this.dataService.getData('table'));
    console.log('Table in column component: ', this.table);
    this.dataService.events$.subscribe({
      next: (data) => {
        this.table = data;
        this.loadAllIndex();
        console.log('Data in column component: ', data);
        //Call api
      },
      error: (error) => {
        console.error(error);
      },
    });
    this.loadAllIndex();
  }

  loadAllIndex() {
    this.actionDelete = false;
    this.actionUpdate = false;
    this.actionInsert = false;
    this.indexService.getList(this.table.name).subscribe({
      next: (data) => {
        this.rows.splice(0, this.rows.length);
        this.rows.splice(0, this.preRows.length);
        this.rows = [];
        this.preRows = [];

        for (let i = 0; i < data.length; i++) {
          let index1: Index = new Index();
          let index2: Index = new Index();

          index1.set(data[i]);
          index2.set(data[i]);

          index1.disabled = index2.disabled = false;

          index1.referencedColumnName = index1.columns[0];
          index2.referencedColumnName = index2.columns[0];

          this.rows.push(index1);
          this.preRows.push(index2);
        }

        // Cập nhật lại id cho từng hàng
        for (let i = 0; i < this.rows.length; i++) {
          this.rows[i].id = i + 1;

          this.rows[i].disabled =
            this.rows[i].name === 'PRIMARY' ? true : false;
          console.log(this.rows[i].disabled);

          console.log(
            'XOAAAA: ' +
              (this.rows[i].disabled || this.actionInsert || this.actionUpdate)
          );
        }

        console.log('Data: ', this.rows);
      },
      error: (error) => {
        this.raiseAlert('Lỗi kết nối đến server', 'danger');

        console.error('There was an error!', error);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }

  // Hàm để mở modal
  openModal(indexRow: number) {
    this.modalRef = this.modalService.open(ModalComponent, {
      data: {
        title: 'Xác nhận',
        content: `Bạn có chắc chắn muốn xóa index <b>${this.rows[indexRow].name}</b> này không?`,
      },
    });

    this.modalRef.onClose.subscribe((message: any) => {
      console.log(message);

      if (message === 'yes') {
        this.deleteRow(indexRow);
      }
    });
  }

  // Đang ở đây

  // For toast
  isClosed() {
    return !this.toastFailEl.nativeElement.classList.contains('show');
  }

  turnOffNotify() {
    this.isDone = false;
  }

  raiseAlert(message: string, type: string): void {
    console.log('Goi alert');
    if (type === 'danger') {
      console.log('Goi fail');
      this.failInformation.message = message;
      this.toastFail.show();
    }
    if (type === 'success') {
      this.successInformation.message = message;
      this.toastSuccess.show();
    }
  }

  handleColumns() {
    for (let i = 0; i < this.rows.length; i++) {
      let list: string[] = this.rows[i].referencedColumnName.split(',');
      for (let j = 0; j < list.length; j++) {
        list[j] = list[j].trim();
      }
      this.rows[i].columns = list;
    }
  }

  save() {
    // Xử lý validation
    for (let i = 0; i < this.rows.length; i++) {
      const { status, message }: { status: boolean; message: string } =
        this.indexService.isValid(this.rows[i]);

      if (!status) {
        this.raiseAlert(message, 'danger');
        return;
      }
    }

    // Xử lý index gồm nhiều cột
    this.handleColumns();

    // Variable to control
    let checkInsert: boolean = true;
    let checkUpdate: boolean = true;

    // Chứa thông báo
    let successMessage: string = '';
    let failedMessage: string = '';

    // Xử lý trường hợp Insert
    if (this.actionInsert) {
      checkInsert = true;
      // Call API.
      console.log(this.rows[0]);

      this.isLoading = true;
      this.indexService.add(this.rows[0]).subscribe({
        next: (data) => {
          console.log(data);

          successMessage += `- Thêm index <b> ${this.rows[0].name} </b> thành công!<br>`;

          // Xóa hàng mẫu và thêm vào cuối.
          let newIndex = new Index();
          newIndex.set(this.rows[0]);

          this.preRows.push(newIndex);

          this.rows.push(this.rows[0]);
          this.rows.splice(0, 1);

          this.actionInsert = false;

          // Bật chức năng thêm và tắt chức năng save
          checkInsert = true;

          // Enable toàn bộ row.
          this.enableAllRows();

          // Thêm thông báo thành công
          this.updateIdAllRow();

          // Cập nhật UI
          this.enableSaveAndDiscardBtn(false);
          this.numberChanged = 0;
          this.changedList = [];
          console.log('toi đây ');

          // Show thông báo
          this.successInformation.message = successMessage;
          this.toastSuccess.show();
          this.isLoading = false;

          this.loadAllIndex();
        },
        error: (error) => {
          console.log(error.error);

          failedMessage += `- Thêm index thất bại!<br>  + Nguyên nhân: ${error.error.cause} <br>`;
          this.failInformation.message = failedMessage;
          this.toastFail.show();
          this.isLoading = false;
        },
      });
      this.actionUpdate = false;
      return;
    }

    //---------------------------------------------------------

    //Xử lý trường hợp Update
    let successList: number[] = [];
    let failedList: number[] = [];
    if (this.actionUpdate) {
      let changedRow: number[] = [];
      for (let i = 0; i < this.changedList.length; i++) {
        // Lấy ra từng hàng đã bị thay đổi.
        const rowId = this.changedList[i][0];
        changedRow.push(rowId);
      }
      // Rút gọn lại unique những hàng bị thay đổi.
      changedRow = getUniqueElements(changedRow);

      const updateRow: Index[] = this.aggregateRow(changedRow);
      console.log('Update row');
      console.log(updateRow);

      // Call api cho từng hàng

      // Loading
      this.isLoading = true;

      // Số lượng hoàn thành
      let count = 0;

      for (let i = 0; i < updateRow.length; i++) {
        console.log('Old name: ' + updateRow[i].oldName);

        this.indexService.update(updateRow[i], updateRow[i].oldName).subscribe({
          next: (data) => {
            console.log(data);
            console.log('UPDATE');
            console.log(data.status);
            console.log(data);
            if (data.ok === true) {
              successMessage += `- Cập nhật index ${updateRow[i].name} thành công!<br>`;

              // Update old row

              this.updateChanged(changedRow[i]);
            } else {
              failedMessage += `- Cập nhật index ${updateRow[i].name} thất bại!<br>`;
              failedList.push(changedRow[i]);
              console.log();
            }

            this.loadAllIndex();
            this.onComplete(successMessage, failedMessage);
          },
          error: (error) => {
            console.log(error);
            console.log(error.error);
            console.log('Cause:  ' + error.error.cause);
            failedMessage += `- Cập nhật index ${updateRow[i].name} thất bại!<br> + Nguyên nhân: ${error.error.cause} <br>`;

            //this.raiseAlert('Cập nhật cột thất bại', 'danger');
            failedList.push(changedRow[i]);

            this.onComplete(successMessage, failedMessage);
          },
        });
      }
    }

    //reset numberChanged
  }

  aggregateRow(changedRow: number[]): Index[] {
    let updateRow: Index[] = [];

    let map = new Map<String, number>();

    for (let i = 0; i < changedRow.length; i++) {
      let idx = changedRow[i] - 1;

      if (map.has(this.rows[idx].name)) {
        continue;
      }

      map.set(this.rows[idx].name, idx);

      let row: Index = new Index();

      row.set(this.rows[idx]);
      row.oldName = this.preRows[idx].name;

      for (let j = 0; j < this.rows.length; j++) {
        if (idx == j) {
          continue;
        }

        if (this.rows[idx].name == this.rows[j].name) {
          for (let k = 0; k < this.rows[j].columns.length; k++) {
            row.columns.push(this.rows[j].columns[k]);
          }
        }
      }
      updateRow.push(row);
    }
    return updateRow;
  }

  onComplete(successMessage: string, failedMessage: string) {
    this.isLoading = false;

    this.actionUpdate = false;

    // Thông báo
    if (successMessage.length > 0) {
      this.successInformation.message = successMessage;
      this.toastSuccess.show();
    }
    if (failedMessage.length > 0) {
      this.failInformation.message = failedMessage;
      this.toastFail.show();

      this.actionUpdate = true;
    }
  }

  addRow() {
    // Assign flag insert true
    this.actionInsert = true;

    // Create new column
    let index: Index = new Index();
    this.rows.unshift(index);
    this.updateIdAllRow();

    // Disable all rows except the first row
    this.disableAllRowsExcept();
    this.enableSaveAndDiscardBtn(true);
    this.rows[0].disabled = false;
  }

  deleteRow(index: number) {
    this.isLoading = true;
    let check: boolean = false;
    let name: string = this.rows[index].name;

    console.log(this.rows[index]);

    this.indexService.delete(this.rows[index]).subscribe({
      next: (data) => {
        if (data.ok === true) {
          check = true;
          this.rows.splice(index, 1);
          this.updateIdAllRow();
          this.updateForPreRows();

          this.successInformation.message = `Xóa index <b>${name}</b> thành công!`;
          this.toastSuccess.show();
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.log('Detele - Error');

        this.failInformation.message = `Xóa index <b>${name}</b> thất bại!<br> + Nguyên nhân: ${error.error.cause} <br>`;
        this.toastFail.show();
        this.isLoading = false;
      },
    });
  }
  disableAllRowsExcept() {
    for (let i = 1; i < this.rows.length; i++) {
      this.rows[i]['disabled'] = true;
    }
  }
  enableAllRows() {
    this.rows.forEach((row) => {
      row['disabled'] = false;
    });
  }

  onFieldChange(
    nameField: string,
    event: string,
    rowId: number,
    fieldId: number
  ) {
    let check = false;

    if (fieldId === 2) {
      this.handleColumns();
    }
    if (fieldId === 1 && !this.actionInsert) {
      for (let i = 0; i < this.preRows.length; i++) {
        if (this.preRows[i].name == this.preRows[rowId - 1].name) {
          this.rows[i].name = event;
        }
      }
    }

    for (let i = 0; i < this.rows.length; i++) {
      if (this.rows[i].name != this.rows[rowId - 1].name) {
        this.rows[i].disabled = true;
        console.log(this.preRows[i].oldName);
      }
    }

    for (let i = 0; i < this.changedList.length; i++) {
      {
        if (
          this.changedList[i][0] == rowId &&
          this.changedList[i][1] == fieldId
        ) {
          check = true;
          break;
        }
      }
    }

    if (!check) {
      this.actionUpdate = true;
      this.enableSaveAndDiscardBtn(true);
      this.changedList.push([rowId, fieldId]);
      this.numberChanged++;
    }
  }
  updateChanged(rowId: number) {
    let check: boolean = false;
    for (let i = this.changedList.length - 1; i >= 0; i--) {
      {
        if (this.changedList[i][0] == rowId) {
          check = true;
          this.changedList.splice(i, 1);
          this.numberChanged--;
        }
      }
    }
    if (check && this.numberChanged == 0) {
      this.enableSaveAndDiscardBtn(false);
    }
  }

  // Hàm xử lý khi có muốn hủy bỏ các thay đổi TẠM THỜI

  discardChanged() {
    this.isLoading = false;

    console.log('vo day');
    this.rows.splice(
      this.preRows.length,
      this.rows.length - this.preRows.length
    );

    for (let i = 0; i < this.preRows.length; i++) {
      this.rows[i].set(this.preRows[i]);
      this.rows[i].id = i + 1;
      this.rows[i].disabled = this.rows[i].name === 'PRIMARY' ? true : false;
    }
    this.enableSaveAndDiscardBtn(false);
    this.numberChanged = 0;
    this.changedList = [];
    this.actionInsert = this.actionUpdate = false;
  }

  enableSaveAndDiscardBtn(flag: boolean) {
    this.btnSaveStatus = !flag;
    this.btnAddStatus = flag;
  }

  // Hanlde textarea

  onTextAreaInput(event: Event): void {
    this.onFieldChange(
      '',
      '',
      this.fieldChange.rowId,
      this.fieldChange.fieldId
    );

    const newContent = event.target as HTMLTextAreaElement;
    this.contentText = newContent.value;

    if (this.fieldChange.fieldId === 1) {
      this.rows[this.fieldChange.rowId - 1].name = this.contentText;

      for (let i = 0; i < this.preRows.length; i++) {
        if (
          this.preRows[i].name == this.preRows[this.fieldChange.rowId - 1].name
        ) {
          this.rows[i].name = this.rows[this.fieldChange.rowId - 1].name;
        }
      }
    }
    if (this.fieldChange.fieldId === 2) {
      this.handleColumns();
      this.rows[this.fieldChange.rowId - 1].referencedColumnName =
        this.contentText;
    }
  }

  onFocus(fieldName: string, rowId: number, fieldId: number, content: string) {
    // Check xem thử lượt focus này khác không
    console.log(content);

    this.modifyingField = `${fieldName} của index <b>${
      this.rows[rowId - 1].name
    }</b> `;
    this.fieldChange.rowId = rowId;
    this.fieldChange.fieldName = fieldName;
    this.fieldChange.fieldId = fieldId;

    this.contentText = '';
    this.contentText = content;
  }

  updateIdAllRow() {
    for (let i = 0; i < this.rows.length; i++) {
      this.rows[i].id = i + 1;
    }
  }
  updateForPreRows() {
    this.preRows = [];
    for (let i = 0; i < this.rows.length; i++) {
      let index: Index = new Index();
      index.set(this.rows[i]);
      this.preRows.push(index);
    }
  }
}
