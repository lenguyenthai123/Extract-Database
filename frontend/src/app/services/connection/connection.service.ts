import { Injectable } from '@angular/core';
import { Column } from '../../models/column.model';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError, Observable } from 'rxjs';
import { environment } from '../../env/environment';
import { Connection } from '../../models/connection.model';

@Injectable({
  providedIn: 'root',
})
export class ConnectionService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient) {}

  connect(con: Connection): Observable<unknown> {
    const requestOptions: Object = {
      /* other options here */
      headers: 'Content-Type: application/json',
      oberve: 'response',
      responseType: 'text',
    };

    return this.http.post<unknown>(
      `${this.apiUrl}/connect`,
      {
        type: con.type,
        host: con.host,
        port: con.port.toString(),
        serviceName: con.serviceName,
        username: con.username,
        password: con.password,
        databaseName: '',
        usernameId: '12',
      },
      requestOptions
    );
  }
}
