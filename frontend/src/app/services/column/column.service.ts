import { Injectable } from '@angular/core';
import { Column } from '../../models/column.model';
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
@Injectable({
  providedIn: 'root',
})
export class ColumnService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  isValid(column: Column): { status: boolean; message: string } {
    if (column.name == '') {
      const message = 'Field name must not be empty!';
      return { status: false, message: message };
    }
    // Khoo
    if (/^[0-9]$/.test(column.name.charAt(0))) {
      const message = 'Field name must not start with a number!';
      return { status: false, message: message };
    }
    if (column.name.length > 255) {
      const message = 'Field name must not exceed 255 characters!';
      return { status: false, message: message };
    }
    if (column.name.includes(' ')) {
      const message = 'Field name must not contain spaces!';
      return { status: false, message: message };
    }

    if (column.dataType == '') {
      const message = 'Data type must not be empty!';
      return { status: false, message: message };
    }
    if (column.dataType.length > 255) {
      const message = 'Data type must not exceed 255 characters!';
      return { status: false, message: message };
    }
    if (column.isDataTypeNeedSize && Number(column.size) <= 0) {
      const message = 'Size must be greater than 0!';
      return { status: false, message: message };
    }

    return { status: true, message: '' };
  }

  createParams(): HttpParams {
    return new HttpParams()
      .set('type', this.dataService.getData('type'))
      .set('schemaName', this.dataService.getData('schemaName'))
      .set('tableName', this.dataService.getData('tableName'))
      .set('usernameId', '12');
  }

  httpOptionsWithJson = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };
  // Phương thức POST
  add(column: Column): Observable<unknown> {
    let dataType = column.dataType;
    if (column.isDataTypeNeedSize === true) {
      dataType = column.dataType.toUpperCase() + '(' + column.size + ')';
    }

    return this.http.post<unknown>(
      `${this.apiUrl}/column`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),

        usernameId: '12',
        name: column.name,
        dataType: dataType,
        size: '',
        primaryKey: column.primaryKey,
        nullable: column.nullable,
        autoIncrement: column.autoIncrement,
        defaultValue: column.defaultValue,
        description: column.description,
      },
      this.httpOptionsWithJson
    );
  }

  // Phương thức PUT
  update(column: Column, identifyName: string): Observable<unknown> {
    let dataType = column.dataType;
    if (column.isDataTypeNeedSize === true) {
      dataType = column.dataType.toUpperCase() + '(' + column.size + ')';
    }
    console.log('Service: ' + column.dataType);
    return this.http.put<unknown>(
      `${this.apiUrl}/column`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),

        oldName: identifyName,
        usernameId: '12',
        name: column.name,
        dataType: dataType,
        size: '',
        primaryKey: column.primaryKey,
        nullable: column.nullable,
        autoIncrement: column.autoIncrement,
        defaultValue: column.defaultValue,
        description: column.description,
      },
      this.httpOptionsWithJson
    );
  }

  getList(tableName: string): Observable<Column[]> {
    let params = this.createParams();
    return this.http.get<Column[]>(`${this.apiUrl}/column/list`, {
      params: params,
    });
  }

  delete(column: Column): Observable<unknown> {
    let params = this.createParams();
    console.log(params);
    params = params.set('name', column.name);
    return this.http.delete<unknown>(`${this.apiUrl}/column`, {
      params: params,
    });
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Lỗi mạng hoặc lỗi khác phía client
      errorMessage = `An error occurred: ${error.error.message}`;
    } else {
      // Lỗi từ phía server
      errorMessage = `Server returned code: ${error.status}, error message is: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(errorMessage);
  }
}
