import { Component, ViewChild } from '@angular/core';
import {
  RouterOutlet,
  Router,
  RouterModule,
  RouterLink,
} from '@angular/router';
import {
  FormsModule,
  ReactiveFormsModule,
  FormGroup,
  FormControl,
  Validators,
  FormBuilder,
} from '@angular/forms';
import { ColumnTableComponent } from '../column-table/column-table.component';
import { SchemaService } from '../../services/schema/schema.service';
import { TableService } from '../../services/table/table.service';
import { Table } from '../../models/table.model';
import { TableComponent } from '../table/table.component';
import { Toast } from 'bootstrap';

import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { DataService } from '../../services/data/data.service';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { User } from '../../models/user.model';
import { Notification } from '../../models/notification.model';
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    RouterOutlet,
    ColumnTableComponent,
    ReactiveFormsModule,

    TableComponent,
    RouterModule,
    RouterLink,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: '../login/login.component.scss',
})
export class RegisterComponent {
  user: User = new User();
  failInformation: Notification = new Notification();
  successInformation: Notification = new Notification();

  @ViewChild('toastFail', { static: true }) toastFailEl: any;
  @ViewChild('toastSuccess', { static: true }) toastSuccessEl: any;

  toastFail: any;
  toastSuccess: any;
  registerForm: FormGroup = new FormGroup({});
  constructor(
    private router: Router,
    private AuthenticationService: AuthenticationService,
    private dataService: DataService,
    private fb: FormBuilder
  ) {}

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required]),
      confirmPassword: new FormControl('', [Validators.required]),
    });
    this.toastFail = new Toast(this.toastFailEl.nativeElement, {});
    this.toastSuccess = new Toast(this.toastSuccessEl.nativeElement, {});

    this.failInformation.title = 'Register failed';
  }

  register() {
    // Xử lý dữ liệu khi form được submit
    this.AuthenticationService.register(this.user).subscribe({
      next: (res) => {
        console.log('Response from register');
        console.log(res);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.failInformation.message = error.error.message;
        console.log(error.error);
        this.toastFail.show();
      },
    });
  }
}
