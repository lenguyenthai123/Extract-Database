import { Component, OnInit } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule,
  FormGroup,
  FormControl,
  Validators,
  FormBuilder,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { requireServiceNameIfOracle } from '../../validators/serviceNameOfOracle';
import { Connection } from '../../models/connection.model';
import { ConnectionService } from '../../services/connection/connection.service';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { ColumnTableComponent } from '../column-table/column-table.component';
import { DataService } from '../../services/data/data.service';
import { Table } from '../../models/table.model';

@Component({
  selector: 'app-connection',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterOutlet,
    RouterLink,
    ColumnTableComponent,
  ],
  templateUrl: './connection.component.html',
  styleUrl: './connection.component.scss',
})
export class ConnectionComponent implements OnInit {
  connectionForm: FormGroup = new FormGroup({});

  alertPlaceholder: HTMLElement | null = null;
  alertTrigger: HTMLElement | null = null;

  //Variabe to hanle loading state of connection
  isLoading: boolean = false;
  isDone: boolean = false;
  message: string = '';
  status: string = '';

  constructor(
    private fb: FormBuilder,
    private connectionService: ConnectionService,
    private router: Router,
    private dataService: DataService
  ) {}
  ngAfterViewInit() {
    this.alertPlaceholder = document.getElementById('liveAlertPlaceholder');
    this.alertTrigger = document.getElementById('liveAlertBtn');
  }

  handleSubmit() {
    // Xử lý dữ liệu khi form được submit
    const connection: Connection = this.connectionForm.value;

    console.log(connection);

    this.isLoading = true;
    this.isDone = false;
    // Gọi service để kết nối đến database
    this.connectionService.connect(connection).subscribe({
      next: (data) => {
        // Saving type of database
        this.dataService.saveData('type', connection.type);

        this.message = 'Kết nối thành công';
        this.status = 'success';

        this.isDone = true;
        this.isLoading = false;
        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.message = 'Kết nối thất bại';
        this.status = 'danger';

        this.isDone = true;
        this.isLoading = false;
      },
      complete: () => {
        console.log('Complete');
        this.isLoading = false;
      },
    });
  }
  ngOnInit(): void {
    this.connectionForm = this.fb.group({
      // Define the form controls
      host: new FormControl('localhost', [Validators.required]),
      port: new FormControl('3308', [Validators.required]),
      type: new FormControl('mysql', [Validators.required]),
      serviceName: new FormControl(''),
      username: new FormControl('root', [Validators.required]),
      password: new FormControl('password'),
    });
  }

  // Phương thức để kiểm tra trạng thái của form control
  isFieldInvalid(fieldName: string): boolean {
    const control = this.connectionForm.get(fieldName);
    return control
      ? control.invalid && (control.dirty || control.touched)
      : false;
  }

  // Phương thức để service name có được nhập không type = oracle
  checkConnectionForm(): boolean {
    if (this.connectionForm.value['type'] === 'oracle') {
      if (this.connectionForm.value['serviceName'].length === 0) {
        return true;
      }
    }
    return false;
  }
  appendAlert = (message: String, type: String) => {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = [].join('');

    if (this.alertPlaceholder !== null) {
      const alertElement = this.alertPlaceholder.appendChild(wrapper);

      // Tự động ẩn thông báo sau 3 giây
      setTimeout(() => {
        if (alertElement && alertElement.parentNode) {
          alertElement.parentNode.removeChild(alertElement);
        }
      }, 3000);
    }
  };
}
