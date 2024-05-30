import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
interface TableRow {
  id: number;
  fieldName: string;
  dataType: string;
  nullable: boolean;
  autoIncrement: boolean;
  primaryKey: boolean;
  defaultValue: string;
  description: string;
  disabled: boolean;
}

@Component({
  selector: 'app-column-table',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './column-table.component.html',
  styleUrl: './column-table.component.scss',
})
export class ColumnTableComponent {
  rows: TableRow[] = [];

  btnAddStatus: boolean = false;
  btnSaveStatus: boolean = true;

  alertPlaceholder: HTMLElement | null = null;
  alertTrigger: HTMLElement | null = null;

  constructor() {
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
    // Tắt chức năng cột
    this.btnAddStatus = true;
    this.btnSaveStatus = false;

    const { status, message }: { status: boolean; message: string } =
      this.checkInformation(this.rows[0]);
    if (!status) return;
    else {
      // Call API.
    }
  }

  checkInformation(row: TableRow): { status: boolean; message: string } {
    if (row.fieldName == '' || row.dataType == '') {
      const message = 'Field name and data type must not be empty!';
      this.appendAlert(message, 'danger');
      return { status: false, message: message };
    }
    if (row.fieldName.includes(' ')) {
      const message = 'Field name must not contain spaces!';
      this.appendAlert(message, 'danger');
      return { status: false, message: message };
    }
    return { status: true, message: '' };
  }

  addRow() {
    this.rows.unshift({
      id: 0,
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
    this.btnAddStatus = true;
    this.btnSaveStatus = false;
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
}
