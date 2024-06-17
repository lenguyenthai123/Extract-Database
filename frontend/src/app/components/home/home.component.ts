import {
  booleanAttribute,
  Component,
  OnInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import {
  RouterOutlet,
  Router,
  RouterModule,
  RouterLink,
} from '@angular/router';
import { DatabaseComponent } from '../database/database.component';
import { ReportComponent } from '../report/report.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, RouterModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {
    this.router.navigate(['home', 'database']);
  }

  navigateToReport() {
    console.log('Navigate to report');
    this.router.navigate(['home', 'report']);
  }

  navigateToDatabase() {
    console.log('Navigate to database');
    this.router.navigate(['home', 'database']);
  }
}
