import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';  // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
interface TableRow{
  id: number;
  fieldName: string;
  dataType: string;
  nullable: boolean;
  autoIncrement: boolean;
  primaryKey: boolean;
  defaultValue: string;
  description: string;
}

@Component({
  selector: 'app-column-table',
  standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './column-table.component.html',
  styleUrl: './column-table.component.scss'
})

export class ColumnTableComponent {

  rows: TableRow[] = [  ]

  constructor() {
    // Thêm một hàng mẫu ban đầu
    this.rows.push({
      id:1,
      fieldName: 'field_name',
      dataType: 'VARCHAR(255)',
      nullable: true,
      autoIncrement: true,
      primaryKey: true,
      defaultValue: 'NULL',
      description: 'Description'
    });
  }

  addRow() {
    this.rows.push({
      id: this.rows.length + 1,
      fieldName: '',
      dataType: '',
      nullable: true,
      autoIncrement: true,
      primaryKey: true,
      defaultValue: '',
      description: ''
    });
  }

  deleteRow(index: number) {
    this.rows.splice(index, 1);
    for(let i = index+1; i < this.rows.length; i++) {
      this.rows[i].id = i - 1;
    }
  }

}
