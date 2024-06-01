import { Routes } from '@angular/router';
import { ColumnTableComponent } from './components/column-table/column-table.component';
import { ConnectionComponent } from './components/connection/connection.component';
export const routes: Routes = [
  {
    path: 'column-table',
    title: 'Column Table',
    component: ColumnTableComponent,
  },
  {
    path: 'connection',
    title: 'Connection',
    component: ConnectionComponent,
  },
];
