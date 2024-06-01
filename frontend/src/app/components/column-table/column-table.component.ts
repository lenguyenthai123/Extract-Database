import { booleanAttribute, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { Column } from '../../models/column.model';
import { ColumnService } from '../../services/column/column.service';
import { DataService } from '../../services/data/data.service';
@Component({
  selector: 'app-column-table',
  standalone: true,
  imports: [FormsModule, CommonModule],
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

  actionInsert: boolean = false;
  actionUpdate: boolean = false;
  actionDelete: boolean = false;

  isLoading: boolean = false;
  isDone: boolean = false;
  status: string = '';
  message: string = '';

  constructor(
    private columnService: ColumnService,
    private dataService: DataService
  ) {}

  ngAfterViewInit() {
    this.alertPlaceholder = document.getElementById('liveAlertPlaceholder');
    this.alertTrigger = document.getElementById('liveAlertBtn');
  }

  ngOnInit(): void {
    this.isLoading = true;

    this.columnService.getList().subscribe({
      next: (data) => {
        this.preRows = data;
        this.rows = data;
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
    this.message = message;
    this.status = type;
    this.isDone = true;
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

    // Xử lý trường hợp Insert
    if (this.actionInsert) {
      // Call API insert

      //      const { status, message }: { status: boolean; message: string } =
      //        this.columnService.add(this.rows[0]);

      const message: string = 'Thêm cột thành công!';

      if (!status) {
        this.raiseAlert(message, 'danger');
        return;
      }
      // Call API.

      // Xóa hàng mẫu và thêm vào cuối.
      this.rows.push(this.rows[0]);
      this.rows.splice(0, 1);

      // Enable toàn bộ row.
      this.enableAllRows();

      this.raiseAlert('Thêm cột thành công!', 'success');
      this.actionInsert = false;
    }

    //Xử lý trường hợp Update
    if (this.actionUpdate) {
      let message: String = '';
      for (let i = 0; i < this.changedList.length; i++) {
        const rowId = this.changedList[i][0];
        const fieldId = this.changedList[i][1];
        const { status, message: msg }: { status: boolean; message: string } =
          this.columnService.isValid(this.rows[rowId - 1]);
        if (!status) {
          this.raiseAlert(msg, 'danger');
        } else {
          // Call API.
          this.raiseAlert('Update successfully!', 'success');
          this.actionUpdate = false;
        }
      }
    }

    // Bật chức năng thêm và tắt chức năng save
    this.enableSaveAndDiscardBtn(false);

    //reset numberChanged
    this.numberChanged = 0;
    this.changedList = [];
  }

  addRow() {
    this.actionInsert = true;
    this.rows.unshift({
      id: this.rows.length + 1,
      name: '',
      dataType: '',
      nullable: false, // Giá trị mặc định là false
      autoIncrement: false, // Giá trị mặc định là false
      primaryKey: false, // Giá trị mặc định là false
      defaultValue: '',
      description: '',
      disabled: false,
    });
    this.disableAllRowsExcept(this.rows.length - 1);
    this.enableSaveAndDiscardBtn(true);
  }

  deleteRow(index: number) {
    this.rows.splice(index, 1);
    for (let i = 0; i < this.rows.length; i++) {
      this.rows[i].id = i + 1;
    }
  }
  disableAllRowsExcept(rowId: number) {
    this.rows.forEach((row) => {
      row['disabled'] = row.id === rowId;
    });
  }
  enableAllRows() {
    this.rows.forEach((row) => {
      row['disabled'] = false;
    });
  }
  onFieldChange(event: string, rowId: number, fieldId: number) {
    let check: boolean = false;
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
      this.enableSaveAndDiscardBtn(true);
      this.changedList.push([rowId, fieldId]);
      this.numberChanged++;
    }
  }

  discard() {
    return 1;
  }

  enableSaveAndDiscardBtn(flag: boolean) {
    this.btnSaveStatus = !flag;
    this.btnAddStatus = flag;
  }
}
