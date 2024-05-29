import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ColumnTableComponent } from './column-table/column-table.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,ColumnTableComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';
}
