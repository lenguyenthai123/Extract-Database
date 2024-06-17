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
export class ReportService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

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

  addTemplate(formData: FormData) {
    console.log('formData', formData);

    return this.http.post(`${this.apiUrl}/report/upload`, formData, {
      observe: 'response',
    });
  }

  downloadDoc(fileName: string): Observable<Blob> {
    let params = this.createParams();
    params = params.set('type', this.dataService.getData('type'));
    params = params.set('usernameId', '12');
    params = params.set('fileName', fileName);
    console.log(fileName);

    return this.http.get(`${this.apiUrl}/report/export-docx/12`, {
      params,
      responseType: 'blob',
    });
  }

  downloadPdf(fileName: string): Observable<Blob> {
    let params = this.createParams();
    params = params.set('type', this.dataService.getData('type'));
    params = params.set('usernameId', '12');

    return this.http.get(`${this.apiUrl}/report/export-docx/12/${fileName}`, {
      params,
      responseType: 'blob',
    });
  }
}
