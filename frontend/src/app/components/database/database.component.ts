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

  searchText: string = '';

  onSearchTextChange(event: Event): void {
    this.tables = [];

    this.tableService.search(this.searchText).subscribe({
      next: (data) => {
        this.tables = data;
        for (let i = 0; i < this.tables.length; i++) {
          this.tables[i].id = i + 1;
        }
        console.log(data);
      },

      error: (error) => {
        console.error(error);
      },
    });
  }

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
    // Mock
    this.tables.push(new Table(1, 'table1', 'description1'));
    this.tables.push(new Table(2, 'table2', 'description2'));
    this.tables.push(new Table(3, 'table3', 'description3'));
    this.tables.push(new Table(4, 'table4', 'description4'));

    this.schemaService.getList().subscribe({
      next: (data) => {
        // Lấy danh sách schema từ API
        for (let i = 0; i < data.length; i++) {
          this.schemas.push({ id: i, name: data[i] });
        }
        this.onLoadAllTable(this.schemas[0].name);
        this.dataService.saveData('schemaName', this.schemas[0].name);

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
    this.dataService.saveData('schemaName', schemaName);
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
        this.dataService.saveData('tableName', this.tables[0].name);
        this.dataService.saveData(
          'tableDescription',
          this.tables[0].description
        );

        this.dataService.saveData('table', JSON.stringify(this.chosenTable));

        this.dataService.pulishString(this.chosenTable);
      },

      error: (error) => {
        console.error(error);
      },
    });
  }

  onTableSelect(table: Table) {
    console.log('vo select');
    console.log(table);
    if (
      table.schemaName !== '' &&
      table.schemaName !== null &&
      table.schemaName !== undefined
    ) {
      this.dataService.saveData('schemaName', table.schemaName);
      console.log('schemaName: ' + table.schemaName);
    }

    this.chosenTable = table;
    this.dataService.saveData('tableName', this.chosenTable.name);
    this.dataService.saveData('tableDescription', this.tables[0].description);

    this.dataService.saveData('table', JSON.stringify(this.chosenTable));
    this.dataService.pulishString(this.chosenTable);
  }
}
