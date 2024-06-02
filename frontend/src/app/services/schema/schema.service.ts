import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../env/environment';
import { Observable } from 'rxjs';
import { DataService } from '../data/data.service';

@Injectable({
  providedIn: 'root',
})
export class SchemaService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  getList(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/schema/list`, {
      params: {
        type: this.dataService.getData('type'),
        usernameId: '12',
      },
    });
  }
}
