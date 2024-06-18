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
import { User } from '../../models/user.model';
@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  // Phương thức POST
  register(user: User) {
    return this.http.post(
      `${this.apiUrl}/auth/register`,
      {
        username: user.username,
        password: user.password,
        confirmPassword: user.confirmPassword,
      },
      {
        observe: 'response',
      }
    );
  }
  login(user: User) {
    return this.http.post(
      `${this.apiUrl}/auth/login`,
      {
        username: user.username,
        password: user.password,
      },
      {
        observe: 'response',
      }
    );
  }
}
