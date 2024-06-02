import { Component } from '@angular/core';
import {
  RouterOutlet,
  Router,
  RouterModule,
  RouterLink,
} from '@angular/router';
import { ColumnTableComponent } from '../column-table/column-table.component';
import { SchemaService } from '../../services/schema/schema.service';
import { TableService } from '../../services/table/table.service';
import { Table } from '../../models/table.model';
import { TableComponent } from '../table/table.component';

import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-database',
  standalone: true,
  imports: [
    RouterOutlet,
    ColumnTableComponent,
    TableComponent,
    RouterModule,
    RouterLink,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './database.component.html',
  styleUrl: './database.component.scss',
})
export class DatabaseComponent {
  schemas: { id: number; name: string }[] = [];
  tables: Table[] = [];

  chosenTable: Table = new Table();

  isLoading: boolean = false;
  isDone: boolean = false;
  status: string = '';
  message: string = '';

  turnOffNotify() {
    this.isDone = false;
  }

  constructor(
    private router: Router,
    private schemaService: SchemaService,
    private tableService: TableService
  ) {
    // Chuyển hướng đến trang connection khi khởi động ứng dụng
  }

  ngOnInit(): void {
    this.schemaService.getList().subscribe({
      next: (data) => {
        // Lấy danh sách schema từ API
        for (let i = 0; i < data.length; i++) {
          this.schemas.push({ id: i, name: data[i] });
        }
        console.log(data);
      },
      error: (error) => {
        console.error(error);
      },
    });
  }
  onSchemaChange(event: any) {
    const schemaName: string = event.target.value;
    // Chuyển hướng đến trang column khi chọn schema
    this.tableService.getList(schemaName).subscribe({
      next: (data) => {
        this.tables = data;
        for (let i = 0; i < this.tables.length; i++) {
          this.tables[i].id = i + 1;
        }
      },
      error: (error) => {
        console.error(error);
      },
    });
  }
  onTableSelect(table: Table) {
    this.chosenTable = table;
  }
}
