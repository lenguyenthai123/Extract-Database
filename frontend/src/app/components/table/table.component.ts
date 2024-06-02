import { booleanAttribute, Component, OnInit, Input } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, RouterLink } from '@angular/router';
import { ColumnTableComponent } from '../column-table/column-table.component';
import { Table } from '../../models/table.model';
@Component({
  selector: 'app-table',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterOutlet,
    ColumnTableComponent,
    RouterLink,
  ],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
})
export class TableComponent {
  @Input() table: Table = new Table();

  isLoading: boolean = false;
  isDone: boolean = false;
  status: string = '';
  message: string = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.isLoading = true;
  }

  turnOffNotify() {
    this.isDone = false;
  }
}
