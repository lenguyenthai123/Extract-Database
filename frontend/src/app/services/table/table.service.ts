import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../env/environment';
import { Observable } from 'rxjs';
import { DataService } from '../data/data.service';
import { Table } from '../../models/table.model';
@Injectable({
  providedIn: 'root',
})
export class TableService {
  private apiUrl = environment.apiUrl; // Đặt URL API của bạn ở đây

  constructor(private http: HttpClient, private dataService: DataService) {}

  getList(schemaName: string): Observable<Table[]> {
    return this.http.get<Table[]>(`${this.apiUrl}/table/list`, {
      params: {
        type: this.dataService.getData('type'),
        schemaName: schemaName,
        usernameId: '12',
      },
    });
  }

  search(keyword: string): Observable<Table[]> {
    return this.http.get<Table[]>(`${this.apiUrl}/table/search`, {
      params: {
        type: this.dataService.getData('type'),
        keyword: keyword,
        usernameId: '12',
      },
    });
  }
}
