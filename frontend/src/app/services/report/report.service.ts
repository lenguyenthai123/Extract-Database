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
      .set('usernameId', this.dataService.getData('usernameId'));
  }

  getListTemplate(): Observable<string[]> {
    let params = this.createParams();
    const usernameId = this.dataService.getData('usernameId');
    return this.http.get<string[]>(
      `${this.apiUrl}/report/list-template/${usernameId}`
    );
  }

  addTemplate(formData: FormData) {
    console.log('formData', formData);

    return this.http.post(`${this.apiUrl}/report/upload`, formData, {
      observe: 'response',
    });
  }

  downloadTemplate(fileName: string): Observable<Blob> {
    let params = this.createParams();
    params = params.set('type', this.dataService.getData('type'));
    params = params.set('usernameId', this.dataService.getData('usernameId'));
    const usernameId = this.dataService.getData('usernameId');

    return this.http.get(
      `${this.apiUrl}/report/download-template/${usernameId}/${fileName}`,
      {
        responseType: 'blob',
      }
    );
  }

  downloadDoc(
    fileName: string,
    dataJson: string,
    extension: string
  ): Observable<Blob> {
    let params = this.createParams();
    params = params.set('type', this.dataService.getData('type'));
    params = params.set('usernameId', this.dataService.getData('usernameId'));
    params = params.set('fileName', fileName);
    params = params.set('dataJson', JSON.stringify(dataJson));
    params = params.set('extension', extension);
    console.log(fileName);
    const usernameId = this.dataService.getData('usernameId');

    return this.http.get(`${this.apiUrl}/report/export/${usernameId}`, {
      params,
      responseType: 'blob',
    });
  }

  downloadPdf(fileName: string): Observable<Blob> {
    let params = this.createParams();
    params = params.set('type', this.dataService.getData('type'));
    params = params.set('usernameId', this.dataService.getData('usernameId'));
    const usernameId = this.dataService.getData('usernameId');

    return this.http.get(
      `${this.apiUrl}/report/export-docx/${usernameId}/${fileName}`,
      {
        params,
        responseType: 'blob',
      }
    );
  }
}
