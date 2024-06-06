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
import { Trigger } from '../../models/trigger.model';
@Injectable({
  providedIn: 'root',
})
export class TriggerService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  isValid(trigger: Trigger): { status: boolean; message: string } {
    if (trigger.name == '') {
      const message = 'Field name must not be empty!';
      return { status: false, message: message };
    }
    if (trigger.name.length > 255) {
      const message = 'Field name must not exceed 255 characters!';
      return { status: false, message: message };
    }
    if (trigger.name.includes(' ')) {
      const message = 'Field name must not contain spaces!';
      return { status: false, message: message };
    }
    if (trigger.doAction == '') {
      const message = 'Do action must not be empty!';
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
      .set('usernameId', '12');
  }

  getList(tableName: string): Observable<Trigger[]> {
    let params = this.createParams();
    return this.http.get<Trigger[]>(`${this.apiUrl}/trigger/list-from-table`, {
      params: params,
    });
  }

  add(trigger: Trigger): Observable<unknown> {
    return this.http.post<Trigger>(
      `${this.apiUrl}/trigger/add`,
      trigger,
      this.httpOptionsWithJson
    );
  }
  update(trigger: Trigger, identifyName: string): Observable<unknown> {
    return this.http.put<Trigger>(
      `${this.apiUrl}/trigger/update`,
      trigger,
      this.httpOptionsWithJson
    );
  }

  delete(trigger: Trigger): Observable<unknown> {
    return this.http.delete<Trigger>(
      `${this.apiUrl}/trigger/delete/${trigger.id}`
    );
  }
}
