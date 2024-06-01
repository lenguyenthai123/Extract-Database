import { Injectable } from '@angular/core';
import { Column } from '../../models/column.model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError, Observable } from 'rxjs';
import { environment } from '../../env/environment';
@Injectable({
  providedIn: 'root',
})
export class ColumnService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient) {}

  isValid(column: Column): { status: boolean; message: string } {
    if (column.name == '') {
      const message = 'Field name must not be empty!';
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

    return { status: true, message: '' };
  }

  // Phương thức POST
  add(column: Column): Observable<Column> {
    return this.http.get<Column>(`${this.apiUrl}/table`, {
      params: {
        type: 'mysql',
        schemaName: 'football',
        table: 'players',
        usernameId: '121212',
      },
    });
  }

  getList(): Observable<Column[]> {
    return this.http.get<Column[]>(`${this.apiUrl}/column/list`, {
      params: {
        type: 'mysql',
        schemaName: 'company',
        table: 'orders',
        usernameId: '121212',
      },
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
