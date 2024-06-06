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
import { Column } from '../../models/column.model';
import { ColumnService } from '../../services/column/column.service';
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

@Component({
  selector: 'app-column-table',
  standalone: true,
  imports: [FormsModule, CommonModule, MdbModalModule],
  templateUrl: './column-table.component.html',
  styleUrl: './column-table.component.scss',
})
export class ColumnTableComponent implements OnInit {
  preRows: Column[] = [];
  rows: Column[] = [];

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
    private columnService: ColumnService,
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
        this.loadAllColumns();
        console.log('Data in column component: ', data);
        //Call api
      },
      error: (error) => {
        console.error(error);
      },
    });
    this.loadAllColumns();
  }

  // Hàm để mở modal
  openModal(indexRow: number) {
    this.modalRef = this.modalService.open(ModalComponent, {
      data: {
        title: 'Xác nhận',
        content: `Bạn có chắc chắn muốn xóa cột <b>${this.rows[indexRow].name}</b> này không?`,
      },
    });

    this.modalRef.onClose.subscribe((message: any) => {
      console.log(message);

      if (message === 'yes') {
        this.deleteRow(indexRow);
      }
    });
  }

  // For toast
  isClosed() {
    return !this.toastFailEl.nativeElement.classList.contains('show');
  }

  loadAllColumns() {
    this.columnService.getList(this.table.name).subscribe({
      next: (data) => {
        this.rows = [];
        this.preRows = [];

        for (let i = 0; i < data.length; i++) {
          let column1: Column = new Column();
          let column2: Column = new Column();

          column1.set(data[i]);
          column2.set(data[i]);

          // Chuyển đổi giá trị mặc định thành số => phù hợp với kiểu dữ liệu
          this.listNumericDataTypes.forEach((type) => {
            if (column1.dataType === type) {
              column1.defaultValueType = 'number';
              column2.defaultValueType = 'number';
            }
          });

          //
          this.listDataTypesWithoutAutoIncrement.forEach((type) => {
            if (column1.dataType === type) {
              column1.disabledAutoIncrement = true;
              column2.disabledAutoIncrement = true;
            }
          });

          this.rows.push(column1);
          this.preRows.push(column2);

          this.activateCondition('', column1.primaryKey.toString(), i + 1, 5);
          this.activateCondition('', column1.dataType, i + 1, 2);
          this.rows[i].size = column2.size;
        }

        // Cập nhật lại id cho từng hàng
        for (let i = 0; i < this.rows.length; i++) {
          this.rows[i].id = i + 1;
        }

        this.isLoading = false;

        console.log('Data: ', data);
      },
      error: (error) => {
        this.isLoading = false;

        this.raiseAlert('Lỗi kết nối đến server', 'danger');

        console.error('There was an error!', error);
      },
    });
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

  save() {
    // Xử lý validation
    for (let i = 0; i < this.rows.length; i++) {
      const { status, message }: { status: boolean; message: string } =
        this.columnService.isValid(this.rows[i]);

      if (!status) {
        this.raiseAlert(message, 'danger');
        return;
      }
    }

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
      this.columnService.add(this.rows[0]).subscribe({
        next: (data) => {
          if (data === true) {
            console.log(data);
            successMessage += `- Thêm cột <b> ${this.rows[0].name} </b> thành công!<br>`;

            // Xóa hàng mẫu và thêm vào cuối.
            let newColumn = new Column();
            newColumn.set(this.rows[0]);

            this.preRows.push(newColumn);

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
          } else {
            failedMessage += '- Thêm cột thất bại!<br>';
          }
        },
        error: (error) => {
          console.log(error.error);
          failedMessage += '- Thêm cột thất bại!<br>';
        },
        complete: () => {
          this.isLoading = false;

          // Thông báo
          if (successMessage.length > 0) {
            this.successInformation.message = successMessage;
            this.toastSuccess.show();
          }
          if (failedMessage.length > 0) {
            this.failInformation.message = failedMessage;
            this.toastFail.show();
          }
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

      // Call api cho từng hàng
      // Loading
      this.isLoading = true;

      // Số lượng hoàn thành
      let count = 0;

      for (let i = 0; i < changedRow.length; i++) {
        console.log('Old name: ' + this.preRows[changedRow[i] - 1].name);
        console.log(this.rows[changedRow[i] - 1]);
        this.columnService
          .update(
            this.rows[changedRow[i] - 1],
            this.preRows[changedRow[i] - 1].name
          )
          .subscribe({
            next: (data) => {
              if (data === true) {
                successMessage += `- Cập nhật cột ${changedRow[i]} thành công!<br>`;

                // Update old row
                this.preRows[changedRow[i] - 1].set(
                  this.rows[changedRow[i] - 1]
                );

                this.updateChanged(changedRow[i]);
              } else {
                failedMessage += `- Cập nhật cột ${changedRow[i]} thất bại!<br>`;
                failedList.push(changedRow[i]);
                console.log();
              }
            },
            error: (error) => {
              console.log(error.error);
              failedMessage += `- Cập nhật cột ${changedRow[i]} thất bại!<br>`;

              //this.raiseAlert('Cập nhật cột thất bại', 'danger');
              failedList.push(changedRow[i]);
            },
            complete: () => {
              count++;
              if (count === changedRow.length) {
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
            },
          });
      }
    }

    //reset numberChanged
  }

  addRow() {
    // Assign flag insert true
    this.actionInsert = true;

    // Create new column
    let column: Column = new Column();
    this.rows.unshift(column);
    this.updateIdAllRow();

    // Disable all rows except the first row
    this.disableAllRowsExcept();
    this.enableSaveAndDiscardBtn(true);
    this.rows[0].disabled = false;
  }

  deleteRow(index: number) {
    this.isLoading = true;
    let check: boolean = false;
    let name: string = '';
    this.columnService.delete(this.rows[index]).subscribe({
      next: (data) => {
        if (data === true) {
          check = true;
          name = this.rows[index].name;
          this.rows.splice(index, 1);
          this.updateIdAllRow();
          this.updateForPreRows();
        }
      },
      error: (error) => {
        console.log(error.error);
      },
      complete: () => {
        this.isLoading = false;
        if (check) {
          this.successInformation.message = `Xóa cột <b>${name}</b> thành công!`;
          this.toastSuccess.show();
        } else {
          this.failInformation.message = `Xóa cột <b>${name}</b> thất bại!`;
          this.toastFail.show();
        }
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

  activateCondition(
    nameField: string,
    event: string,
    rowId: number,
    fieldId: number
  ) {
    let check: boolean = false;

    if (fieldId === 5) {
      if (event === 'true') {
        let check: boolean = false;
        for (let i = 0; i < this.rows.length; i++) {
          if (this.rows[i].primaryKey && i != rowId - 1) {
            check = true;
            break;
          }
        }

        if (check) {
          this.raiseAlert(
            'Hãy bỏ primary key ban đầu để tiến hàng chọn primary key mới',
            'danger'
          );
          this.rows[rowId - 1].primaryKey = false;

          return;
        }
      }
    }

    if (fieldId === 2) {
      // Chuyển đổi giá trị mặc định thành số => phù hợp với kiểu dữ liệu
      let check: boolean = false;

      this.listNumericDataTypes.forEach((type) => {
        if (event === type) {
          this.rows[rowId - 1].defaultValueType = 'number';
          this.rows[rowId - 1].defaultValue = '0';
          check = true;
        }
      });
      if (!check) {
        this.rows[rowId - 1].defaultValueType = 'text';
      }

      // Disable auto increment
      check = false;
      this.listDataTypesWithoutAutoIncrement.forEach((type) => {
        if (event === type) {
          this.rows[rowId - 1].disabledAutoIncrement = true;
          check = true;
        }
      });
      if (!check) {
        this.rows[rowId - 1].disabledAutoIncrement = false;
      }
    }
    // Check field need size
    if (fieldId === 2) {
      // Chuyển đổi giá trị mặc định thành số => phù hợp với kiểu dữ liệu
      let check: boolean = false;
      console.log('Datatype: ' + event);
      console.log('Disable: ' + this.rows[rowId - 1].disabled);
      console.log('Row id: ' + rowId);
      this.listDataTypesNeedSize.forEach((type) => {
        if (event === type) {
          console.log(type);
          this.rows[rowId - 1].isDataTypeNeedSize = true;
          this.rows[rowId - 1].size = '10';
          check = true;
        }
      });
      if (!check) {
        this.rows[rowId - 1].isDataTypeNeedSize = false;
        this.rows[rowId - 1].size = '';
      }
    }
  }

  onFieldChange(
    nameField: string,
    event: string,
    rowId: number,
    fieldId: number
  ) {
    let check = false;
    this.activateCondition(nameField, event, rowId, fieldId);

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
    console.log('vo day');
    this.rows.splice(
      this.preRows.length,
      this.rows.length - this.preRows.length
    );

    for (let i = 0; i < this.preRows.length; i++) {
      this.rows[i].set(this.preRows[i]);
      this.rows[i].id = i + 1;
    }
    this.enableSaveAndDiscardBtn(false);
    this.numberChanged = 0;
    this.changedList = [];
    this.actionInsert = this.actionUpdate = false;

    for (let i = 0; i < this.rows.length; i++) {
      this.activateCondition('', this.rows[i].primaryKey.toString(), i + 1, 5);
      this.activateCondition('', this.rows[i].dataType, i + 1, 2);
    }
  }

  enableSaveAndDiscardBtn(flag: boolean) {
    this.btnSaveStatus = !flag;
    this.btnAddStatus = flag;
  }

  primaryKeyCondition(rowId: number): boolean {
    let check: boolean = false;
    for (let i = 0; i < this.rows.length; i++) {
      if (this.rows[i].primaryKey) {
        check = true;
        break;
      }
    }
    if (check) {
      if (this.rows[rowId].primaryKey) {
        return false;
      }
      return true;
    }
    return false;
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
    }
    if (this.fieldChange.fieldId === 6) {
      this.rows[this.fieldChange.rowId - 1].defaultValue = this.contentText;
    }
    if (this.fieldChange.fieldId === 7) {
      this.rows[this.fieldChange.rowId - 1].description = this.contentText;
    }
    if (this.fieldChange.fieldId === 8) {
      this.rows[this.fieldChange.rowId - 1].size = this.contentText;
    }
  }

  onFocus(fieldName: string, rowId: number, fieldId: number, content: string) {
    // Check xem thử lượt focus này khác không
    console.log(content);

    this.modifyingField = `${fieldName} của cột <b>${
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
      let column: Column = new Column();
      column.set(this.rows[i]);
      this.preRows.push(column);
    }
  }
}
