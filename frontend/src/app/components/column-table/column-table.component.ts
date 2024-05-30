import { booleanAttribute, Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { Column } from '../../models/column.model';
import { ColumnService } from '../../services/column.service';

@Component({
  selector: 'app-column-table',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './column-table.component.html',
  styleUrl: './column-table.component.scss',
})
export class ColumnTableComponent {
  preRows: Column[] = [];
  rows: Column[] = [];

  numberChanged: number = 0;
  changedList: [number, number][] = [];

  btnAddStatus: boolean = false;
  btnSaveStatus: boolean = true;

  alertPlaceholder: HTMLElement | null = null;
  alertTrigger: HTMLElement | null = null;

  actionInsert: boolean = false;
  actionUpdate: boolean = false;
  actionDelete: boolean = false;

  constructor(private columnService: ColumnService) {
    // Thêm một hàng mẫu ban đầu
    this.rows.push({
      id: 1,
      fieldName: 'field_name',
      dataType: 'VARCHAR(255)',
      nullable: false, // Giá trị mặc định là false
      autoIncrement: false, // Giá trị mặc định là false
      primaryKey: false, // Giá trị mặc định là false
      defaultValue: 'NULL',
      description: 'Description',
      disabled: false,
    });
  }

  ngAfterViewInit() {
    this.alertPlaceholder = document.getElementById('liveAlertPlaceholder');
    this.alertTrigger = document.getElementById('liveAlertBtn');
  }

  appendAlert = (message: String, type: String) => {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = [
      `<div class="alert alert-${type} alert-dismissible" role="alert">`,
      `   <div>${message}</div>`,
      '   <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>',
      '</div>',
    ].join('');

    if (this.alertPlaceholder !== null) {
      const alertElement = this.alertPlaceholder.appendChild(wrapper);

      // Tự động ẩn thông báo sau 3 giây
      setTimeout(() => {
        if (alertElement && alertElement.parentNode) {
          alertElement.parentNode.removeChild(alertElement);
        }
      }, 3000);
    }
  };

  save() {
    // Xử lý validation
    for (let i = 0; i < this.rows.length; i++) {
      const { status, message }: { status: boolean; message: string } =
        this.columnService.isValid(this.rows[i]);

      if (!status) {
        this.appendAlert(message, 'danger');
        return;
      }
    }

    // Xử lý trường hợp Insert
    if (this.actionInsert) {
      // Call API insert

      const { status, message }: { status: boolean; message: string } =
        this.columnService.add(this.rows[0]);

      if (!status) {
        this.appendAlert(message, 'danger');
        return;
      }
      // Call API.

      // Xóa hàng mẫu và thêm vào cuối.
      this.rows.push(this.rows[0]);
      this.rows.splice(0, 1);

      // Enable toàn bộ row.
      this.enableAllRows();

      this.appendAlert('Thêm cột thành công!', 'success');
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
          this.appendAlert(msg, 'danger');
        } else {
          // Call API.
          this.appendAlert('Update successfully!', 'success');
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
      fieldName: '',
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
