import {
  booleanAttribute,
  Component,
  OnInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { Column } from '../../models/column.model';
import { ColumnService } from '../../services/column/column.service';
import { DataService } from '../../services/data/data.service';
import { Table } from '../../models/table.model';

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
  liveToast: ElementRef | null = null;

  actionInsert: boolean = false;
  actionUpdate: boolean = false;
  actionDelete: boolean = false;

  isLoading: boolean = false;
  isDone: boolean = false;
  status: string = '';
  message: string = '';

  table: Table = new Table();

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
  }

  loadAllColumns() {
    this.columnService.getList(this.table.name).subscribe({
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

    // Variable to control
    let checkInsert: boolean = true;
    let checkUpdate: boolean = true;

    // Xử lý trường hợp Insert
    if (this.actionInsert) {
      checkInsert = true;
      // Call API.
      console.log(this.rows[0]);

      this.columnService.add(this.rows[0]).subscribe({
        next: (data) => {
          if (data === true) {
            alert(data);
            console.log(data);

            // Xóa hàng mẫu và thêm vào cuối.
            this.rows.push(this.rows[0]);
            this.rows.splice(0, 1);

            // Enable toàn bộ row.
            this.enableAllRows();

            this.raiseAlert('Thêm cột thành công!', 'success');
            this.actionInsert = false;

            // Bật chức năng thêm và tắt chức năng save
            checkInsert = true;

            // Update old row
            this.preRows.unshift(this.rows[0]);
          } else {
            this.raiseAlert('Thêm cột thất bại', 'danger');
          }
        },
        error: (error) => {
          console.log(error.error);
          this.raiseAlert('Thêm cột thất bại', 'danger');
        },
      });
    }

    /*
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
    */

    //reset numberChanged

    if (checkInsert && checkUpdate) {
      this.enableSaveAndDiscardBtn(false);
      this.numberChanged = 0;
      this.changedList = [];
    }
  }

  addRow() {
    // Assign flag insert true
    this.actionInsert = true;

    // Create new column
    let column: Column = new Column();
    column.id = this.rows.length + 1;
    this.rows.unshift(column);

    // Disable all rows except the first row
    this.disableAllRowsExcept();
    this.enableSaveAndDiscardBtn(true);
  }

  deleteRow(index: number) {
    this.columnService.detele(this.rows[index]).subscribe({
      next: (data) => {
        alert(data);
        console.log(data);

        this.rows.splice(index, 1);
        for (let i = 0; i < this.rows.length; i++) {
          this.rows[i].id = i + 1;
        }
      },
      error: (error) => {
        console.log(error);
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

  discardChanged() {
    for (let i = 0; i < this.preRows.length; i++) {
      this.rows[i].set(this.preRows[i]);
    }
    this.enableSaveAndDiscardBtn(false);
    this.numberChanged = 0;
    this.changedList = [];
  }

  enableSaveAndDiscardBtn(flag: boolean) {
    this.btnSaveStatus = !flag;
    this.btnAddStatus = flag;
  }
}
