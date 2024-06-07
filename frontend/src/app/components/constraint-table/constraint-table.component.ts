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
import { getUniqueElements } from '../../utils/Utils';
import { Constraint } from '../../models/constraint.model';

import { ConstraintService } from '../../services/constraint/constraint.service';

@Component({
  selector: 'app-constraint-table',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './constraint-table.component.html',
  styleUrl: './constraint-table.component.scss',
})
export class ConstraintTableComponent implements OnInit {
  preRows: Constraint[] = [];
  rows: Constraint[] = [];

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

  // Danh sách các kiểu dữ liệu trong MySQL
  listDataTypes: string[] = [];
  listNumericDataTypes: string[] = [];
  listDataTypesWithoutAutoIncrement: string[] = [];

  constructor(
    private constraintService: ConstraintService,
    private dataService: DataService
  ) {
    if (
      this.dataService.getData('type') === 'mysql' ||
      this.dataService.getData('type') === 'mariadb'
    ) {
      this.listDataTypes = this.dataService.mysqlDataTypes;
      this.listNumericDataTypes = this.dataService.mysqlNumericDataTypes;
      this.listDataTypesWithoutAutoIncrement =
        this.dataService.mysqlDataTypesWithoutAutoIncrement;
    }
  }

  ngAfterViewInit() {
    this.alertPlaceholder = document.getElementById('liveAlertPlaceholder');
    this.alertTrigger = document.getElementById('liveAlertBtn');
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.table = JSON.parse(this.dataService.getData('table'));
    this.dataService.events$.subscribe({
      next: (data) => {
        this.table = data;
        this.loadAllConstraint();
        console.log('Data in column component: ', data);
        //Call api
      },
      error: (error) => {
        console.error(error);
      },
    });
    this.loadAllConstraint();
  }

  loadAllConstraint() {
    this.constraintService.getList(this.table.name).subscribe({
      next: (data) => {
        this.rows = [];
        this.preRows = [];

        for (let i = 0; i < data.length; i++) {
          let constraint1: Constraint = new Constraint();
          let constraint2: Constraint = new Constraint();

          constraint1.set(data[i]);
          constraint2.set(data[i]);

          this.rows.push(constraint1);
          this.preRows.push(constraint2);
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
    this.message = message;
    this.status = type;
    this.isDone = true;
  }

  save() {
    // Xử lý validation
    for (let i = 0; i < this.rows.length; i++) {
      const { status, message }: { status: boolean; message: string } =
        this.constraintService.isValid(this.rows[i]);

      if (!status) {
        this.raiseAlert(message, 'danger');
        return;
      }
    }

    // Variable to control
    let checkInsert: boolean = true;
    let checkUpdate: boolean = true;

    // Xử lý trường hợp Insert

    /*  if (this.actionInsert) {
      checkInsert = true;
      // Call API.
      console.log(this.rows[0]);

      this.columnService.add(this.rows[0]).subscribe({
        next: (data) => {
          if (data === true) {
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
    */

    //---------------------------------------------------------

    //Xử lý trường hợp Update

    /*    
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
      let successList: number[] = [];
      let failedList: number[] = [];

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
                console.log('Update ngon');
                //this.raiseAlert('Cập nhật cột thành công!', 'success');

                successList.push(changedRow[i]);
              } else {
                //this.raiseAlert('Cập nhật cột thất bại', 'danger');
                failedList.push(changedRow[i]);
                console.log('ngu ngu');
              }
            },
            error: (error) => {
              console.log(error.error);
              //this.raiseAlert('Cập nhật cột thất bại', 'danger');
              failedList.push(changedRow[i]);
            },
          });
      }
      console.log('ra day');
      let message: string = '';
      // Xử lý thông báo
      if (successList.length > 0) {
        message += 'Cập nhật cột thành công: ';
        for (let i = 0; i < successList.length; i++) {
          message += successList[i] + ', ';
        }
        message = message.slice(0, -2);
        this.raiseAlert(message, 'success');
      }

      message = '';
      if (failedList.length > 0) {
        message += 'Cập nhật cột thất bại: ';
        for (let i = 0; i < failedList.length; i++) {
          message += failedList[i] + ', ';
        }
        message = message.slice(0, -2);
        this.raiseAlert(message, 'danger');
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
    let constraint: Constraint = new Constraint();
    constraint.id = this.rows.length + 1;
    this.rows.unshift(constraint);

    // Disable all rows except the first row
    this.disableAllRowsExcept();
    this.enableSaveAndDiscardBtn(true);
  }

  deleteRow(index: number) {
    alert('Delete row ' + index);
    /*this.constraintService.detele(this.rows[index]).subscribe({
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
    */
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