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

import { Index } from '../../models/index.model';
import { IdList } from '../../models/index.model';
@Injectable({
  providedIn: 'root',
})
export class IndexService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  isValid(index: Index): { status: boolean; message: string } {
    if (index.name == '') {
      const message = 'Field name must not be empty!';
      return { status: false, message: message };
    }
    if (index.name.length > 255) {
      const message = 'Field name must not exceed 255 characters!';
      return { status: false, message: message };
    }
    if (index.name.includes(' ')) {
      const message = 'Field name must not contain spaces!';
      return { status: false, message: message };
    }

    return { status: true, message: '' };
  }

  httpOptionsWithJson = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      observe: 'response',
    }),
  };
  createParams(): HttpParams {
    return new HttpParams()
      .set('type', this.dataService.getData('type'))
      .set('schemaName', this.dataService.getData('schemaName'))
      .set('tableName', this.dataService.getData('tableName'))
      .set('usernameId', this.dataService.getData('usernameId'));
  }

  getList(tableName: string): Observable<Index[]> {
    let params = this.createParams();
    return this.http.get<Index[]>(`${this.apiUrl}/index/list-from-table`, {
      params: params,
    });
  }

  add(index: Index) {
    let params = this.createParams();

    params = params.set('name', index.name);
    index.columns.forEach((column) => {
      params = params.append('columns', column);
    });

    console.log(index);
    return this.http.post(
      `${this.apiUrl}/index`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: this.dataService.getData('usernameId'),

        name: index.name,
        columns: index.columns,
      },
      {
        observe: 'response',
      }
    );
  }
  update(index: Index, identifyName: string) {
    return this.http.put(
      `${this.apiUrl}/index`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: this.dataService.getData('usernameId'),

        oldName: identifyName,
        name: index.name,
        columns: index.columns,
      },
      {
        observe: 'response',
      }
    );
  }

  delete(index: Index) {
    let params = this.createParams();
    console.log(params);

    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      params: params,
      observe: 'response',
    };

    return this.http.delete(`${this.apiUrl}/index`, {
      params: {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: this.dataService.getData('usernameId'),

        name: index.name,
        columns: index.columns,
      },
      observe: 'response',
    });
  }
}
