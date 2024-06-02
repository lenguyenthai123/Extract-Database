import { Routes } from '@angular/router';
import { ColumnTableComponent } from './components/column-table/column-table.component';
import { ConnectionComponent } from './components/connection/connection.component';
import { DatabaseComponent } from './components/database/database.component';
import { TableComponent } from './components/table/table.component';
export const routes: Routes = [
  {
    path: 'connection',
    title: 'Connection',
    component: ConnectionComponent,
  },
  {
    path: 'database',
    title: 'Database management',
    component: DatabaseComponent,
    children: [
      {
        path: 'column',
        title: 'Column',
        component: ColumnTableComponent,
      },
    ],
  },
];
