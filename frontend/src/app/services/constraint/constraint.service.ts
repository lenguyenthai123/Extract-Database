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
      .set('usernameId', '12');
  }

  getList(tableName: string): Observable<Constraint[]> {
    let params = this.createParams();
    return this.http.get<Constraint[]>(`${this.apiUrl}/constraint/list`, {
      params: params,
    });
  }
}
