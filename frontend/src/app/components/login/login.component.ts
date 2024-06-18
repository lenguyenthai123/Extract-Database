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
  selector: 'app-login',
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
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  user: User = new User();
  failInformation: Notification = new Notification();
  successInformation: Notification = new Notification();

  @ViewChild('toastFail', { static: true }) toastFailEl: any;
  @ViewChild('toastSuccess', { static: true }) toastSuccessEl: any;

  toastFail: any;
  toastSuccess: any;
  loginForm: FormGroup = new FormGroup({});
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private dataService: DataService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required]),
      confirmPassword: new FormControl('', [Validators.required]),
    });
    this.toastFail = new Toast(this.toastFailEl.nativeElement, {});
    this.toastSuccess = new Toast(this.toastSuccessEl.nativeElement, {});

    this.failInformation.title = 'Login failed';
  }

  login() {
    console.log(this.user);
    this.authenticationService.login(this.user).subscribe(
      (data) => {
        const json = JSON.parse(JSON.stringify(data));
        console.log(json);
        console.log(json.body);
        console.log(json.body.id);
        this.dataService.saveData('usernameId', JSON.stringify(json.body.id));
        this.router.navigate(['/connection']);
      },
      (error) => {
        console.log(error);
        this.failInformation.message = error.error.message;
        this.toastFail.show();
      }
    );
  }

  navigateToConnection(): void {
    this.router.navigate(['/connection']);
  }
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
