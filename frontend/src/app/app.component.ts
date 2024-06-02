import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterModule } from '@angular/router';
import { ColumnTableComponent } from './components/column-table/column-table.component';
import { ConnectionComponent } from './components/connection/connection.component';
import { DatabaseComponent } from './components/database/database.component';
import { ApplicationConfig } from '@angular/core';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    ColumnTableComponent,
    ConnectionComponent,
    DatabaseComponent,
    RouterModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'frontend';
  constructor(private router: Router) {
    // Chuyển hướng đến trang connection khi khởi động ứng dụng
  }

  ngOnInit(): void {
    this.router.navigate(['/connection']);
  }
}
