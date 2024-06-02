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
import { Subject } from 'rxjs';
import { DataService } from '../../services/data/data.service';
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

  eventsSubject: Subject<void> = new Subject<void>();

  emitEventToChild() {
    this.eventsSubject.next();
  }

  turnOffNotify() {
    this.isDone = false;
  }

  constructor(
    private router: Router,
    private schemaService: SchemaService,
    private tableService: TableService,
    private dataService: DataService
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
        this.onLoadAllTable(this.schemas[0].name);
        this.dataService.saveData('schema', this.schemas[0].name);

        console.log(data);
      },
      error: (error) => {
        console.error(error);
      },
    });
    this.router.navigate(['/database/column']);
  }
  onSchemaChange(event: any) {
    const schemaName: string = event.target.value;
    this.dataService.saveData('schema', schemaName);
    this.onLoadAllTable(schemaName);
  }

  onLoadAllTable(schemaName: string) {
    this.tableService.getList(schemaName).subscribe({
      next: (data) => {
        this.tables = data;
        for (let i = 0; i < this.tables.length; i++) {
          this.tables[i].id = i + 1;
        }
        this.chosenTable = this.tables[0];

        this.dataService.pulishString(this.chosenTable);
      },

      error: (error) => {
        console.error(error);
      },
    });
  }

  onTableSelect(table: Table) {
    this.chosenTable = table;
    this.dataService.pulishString(this.chosenTable);
  }
}
