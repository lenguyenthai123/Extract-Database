import { Component } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { ColumnTableComponent } from './components/column-table/column-table.component';
import { ConnectionComponent } from './components/connection/connection.component';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ColumnTableComponent, ConnectionComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'frontend';
  constructor(private router: Router) {
    // Chuyển hướng đến trang connection khi khởi động ứng dụng
    this.router.navigate(['/connection']);
  }
}
