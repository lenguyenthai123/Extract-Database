import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
  HttpHeaders,
} from '@angular/common/http';
import { catchError, count } from 'rxjs/operators';
import { throwError, Observable } from 'rxjs';
import { environment } from '../../env/environment';
import { DataService } from '../data/data.service';
import { Constraint } from '../../models/constraint.model';
import { Column } from '../../models/column.model';
@Injectable({
  providedIn: 'root',
})
export class ConstraintService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  isValid(constraint: Constraint): { status: boolean; message: string } {
    if (constraint.name == '') {
      const message = 'Field name must not be empty!';
      return { status: false, message: message };
    }
    if (constraint.name.length > 255) {
      const message = 'Field name must not exceed 255 characters!';
      return { status: false, message: message };
    }
    if (constraint.name.includes(' ')) {
      const message = 'Field name must not contain spaces!';
      return { status: false, message: message };
    }

    return { status: true, message: '' };
  }

  httpOptionsWithJson = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };
  createParams(): HttpParams {
    return new HttpParams()
      .set('type', this.dataService.getData('type'))
      .set('schemaName', this.dataService.getData('schemaName'))
      .set('tableName', this.dataService.getData('tableName'))
      .set('usernameId', this.dataService.getData('usernameId'));
  }

  getList(tableName: string): Observable<Constraint[]> {
    let params = this.createParams();
    return this.http.get<Constraint[]>(
      `${this.apiUrl}/constraint/list-from-table`,
      {
        params: params,
      }
    );
  }

  add(constraint: Constraint) {
    console.log(constraint);
    return this.http.post(
      `${this.apiUrl}/constraint`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: this.dataService.getData('usernameId'),

        name: constraint.name,
        columnList: constraint.columnList,
        constraintType: constraint.constraintType,
        refTableName: constraint.refTableName,
        refColumnName: constraint.refColumnName,
      },
      {
        observe: 'response',
      }
    );
  }
  update(constraint: Constraint, identifyName: string) {
    return this.http.put(
      `${this.apiUrl}/constraint`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: this.dataService.getData('usernameId'),

        oldName: identifyName,
        name: constraint.name,
        columnList: constraint.columnList,
        constraintType: constraint.constraintType,
        refTableName: constraint.refTableName,
        refColumnName: constraint.refColumnName,
      },
      {
        observe: 'response',
      }
    );
  }

  delete(constraint: Constraint) {
    let params = this.createParams();
    console.log(params);

    return this.http.delete(`${this.apiUrl}/constraint`, {
      params: {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: this.dataService.getData('usernameId'),

        name: constraint.name,
        columnList: constraint.columnList,
        constraintType: constraint.constraintType,
        refTableName: constraint.refTableName,
        refColumnName: constraint.refColumnName,
      },
      observe: 'response',
    });
  }
}
