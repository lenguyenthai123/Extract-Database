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

  getList(tableName: string): Observable<Trigger[]> {
    let params = this.createParams();
    return this.http.get<Trigger[]>(`${this.apiUrl}/trigger/list-from-table`, {
      params: params,
    });
  }

  add(trigger: Trigger) {
    console.log(trigger);
    return this.http.post(
      `${this.apiUrl}/trigger`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: '12',

        name: trigger.name,
        event: trigger.event,
        timing: trigger.timing,
        doAction: trigger.doAction,
        actionCondition: trigger.actionCondition,
      },
      {
        observe: 'response',
      }
    );
  }
  update(trigger: Trigger, identifyName: string) {
    return this.http.put(
      `${this.apiUrl}/trigger`,
      {
        type: this.dataService.getData('type'),
        schemaName: this.dataService.getData('schemaName'),
        tableName: this.dataService.getData('tableName'),
        usernameId: '12',

        oldName: identifyName,
        name: trigger.name,
        event: trigger.event,
        timing: trigger.timing,
        doAction: trigger.doAction,
        actionCondition: trigger.actionCondition,
      },
      {
        observe: 'response',
      }
    );
  }

  delete(trigger: Trigger) {
    let params = this.createParams();
    console.log(params);
    params = params.set('triggerName', trigger.name);

    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      params: params,
      observe: 'response',
    };

    return this.http.delete(`${this.apiUrl}/trigger`, {
      headers: httpOptions.headers,
      params: httpOptions.params,
      observe: 'response',
    });
  }
}
