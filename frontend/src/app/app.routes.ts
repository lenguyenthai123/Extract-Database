import { Routes } from '@angular/router';
import { ColumnTableComponent } from './components/column-table/column-table.component';
import { ConnectionComponent } from './components/connection/connection.component';
import { DatabaseComponent } from './components/database/database.component';
import { TableComponent } from './components/table/table.component';
import { ConstraintTableComponent } from './components/constraint-table/constraint-table.component';
import { TriggerTableComponent } from './components/trigger-table/trigger-table.component';
import { IndexTableComponent } from './components/index-table/index-table.component';
import { ReportComponent } from './components/report/report.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
export const routes: Routes = [
  {
    path: 'connection',
    title: 'Connection',
    component: ConnectionComponent,
  },
  {
    path: 'login',
    title: 'Login',
    component: LoginComponent,
  },

  {
    path: 'register',
    title: 'Register',
    component: RegisterComponent,
  },

  {
    path: 'home',
    title: 'Home',
    component: HomeComponent,
    children: [
      {
        path: 'report',
        title: 'Report',
        component: ReportComponent,
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
          {
            path: 'constraint',
            title: 'Constraint',
            component: ConstraintTableComponent,
          },
          {
            path: 'trigger',
            title: 'Trigger',
            component: TriggerTableComponent,
          },
          {
            path: 'index',
            title: 'Index',
            component: IndexTableComponent,
          },
        ],
      },
    ],
  },
];
