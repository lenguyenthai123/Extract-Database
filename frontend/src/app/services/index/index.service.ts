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
      .set('usernameId', '12');
  }

  getList(tableName: string): Observable<Index[]> {
    let params = this.createParams();
    return this.http.get<Index[]>(`${this.apiUrl}/index/list-from-table`, {
      params: params,
    });
  }

  add(index: Index) {
    console.log(index);
    return this.http.post(
      `${this.apiUrl}/index`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: '12',

        name: index.name,
        referencedColumnName: index.referencedColumnName,
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
        usernameId: '12',

        oldName: identifyName,
        name: index.name,
        referencedColumnName: index.referencedColumnName,
      },
      {
        observe: 'response',
      }
    );
  }

  delete(index: Index) {
    let params = this.createParams();
    console.log(params);
    params = params.set('indexName', index.name);

    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      params: params,
      observe: 'response',
    };

    return this.http.delete(`${this.apiUrl}/index`, {
      headers: httpOptions.headers,
      params: httpOptions.params,
      observe: 'response',
    });
  }
}
