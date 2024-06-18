import { Component, ViewChild } from '@angular/core';
import { saveAs } from 'file-saver';
import {
  RouterOutlet,
  Router,
  RouterModule,
  RouterLink,
} from '@angular/router';
import { ColumnTableComponent } from '../column-table/column-table.component';
import { SchemaService } from '../../services/schema/schema.service';
import { TableService } from '../../services/table/table.service';
import { Table } from '../../models/table.model';
import { TableComponent } from '../table/table.component';

import { FormsModule } from '@angular/forms'; // Import FormsModule
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { DataService } from '../../services/data/data.service';
import { ReportService } from '../../services/report/report.service';
import * as mammoth from 'mammoth';
import { Notification } from '../../models/notification.model';
import { Toast } from 'bootstrap';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [
    RouterOutlet,
    ColumnTableComponent,
    TableComponent,
    RouterModule,
    RouterLink,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './report.component.html',
  styleUrl: './report.component.scss',
})
export class ReportComponent {
  templates: string[] = [];
  selectedTemplate: string = 'default.docx';
  docHtml: string = '';

  isLoading: boolean = true;
  dataJson: string = '';
  extension: string = 'docx';

  failInformation: Notification = new Notification();
  successInformation: Notification = new Notification();

  @ViewChild('toastFail', { static: true }) toastFailEl: any;
  @ViewChild('toastSuccess', { static: true }) toastSuccessEl: any;

  toastFail: any;
  toastSuccess: any;
  constructor(
    private reportService: ReportService,
    private dataService: DataService
  ) {}

  ngOnInit() {
    this.toastFail = new Toast(this.toastFailEl.nativeElement, {});
    this.toastSuccess = new Toast(this.toastSuccessEl.nativeElement, {});

    this.isLoading = true;
    this.reportService.getListTemplate().subscribe({
      next: (data) => {
        this.templates = data;
        this.templates.push('default.docx');
        console.log(data);
        this.isLoading = false;
      },
      error: (error) => {
        console.log(error);
        this.isLoading = false;

        this.failInformation.message = error.error.message;
        this.toastFail.show();
      },
    });
  }
  onChangeExtension(event: any) {
    this.extension = event.target.value;
  }

  selectTemplate(template: string) {
    this.selectedTemplate = template;

    if (template === 'default.docx') {
      this.docHtml = '';
      return;
    }
    this.isLoading = true;
    console.log('Template selected: ', template);

    this.reportService.downloadTemplate(template).subscribe({
      next: (blob) => {
        let reader = new FileReader();
        reader.onload = (event: any) => {
          let arrayBuffer = event.target.result;
          mammoth
            .convertToHtml({ arrayBuffer: arrayBuffer })
            .then((result) => {
              this.docHtml = result.value;
              this.isLoading = false;
            })
            .catch((error) => {
              console.error('Error converting file:', error);
              this.isLoading = false;
            });
        };
        reader.readAsArrayBuffer(blob);
      },
      error: (error) => {
        console.error('Error downloading file:', error);
        this.isLoading = false;
        this.failInformation.message = error.error.message;
        this.toastFail.show();
      },
    });
  }

  uploadTemplate(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.isLoading = true;
      console.log(`File uploaded: ${file.name}`);
      // Handle file upload logic here
      // Add template to the list
      // Split the file name to get the template name without extension
      const fileName = file.name.split('.')[0];

      const formData = new FormData();
      formData.append('file', file, file.name);
      formData.append('usernameId', this.dataService.getData('usernameId'));

      this.reportService.addTemplate(formData).subscribe({
        next: (data) => {
          console.log(data);
          this.templates.push(file.name);
          this.isLoading = false;
        },
        error: (error) => {
          console.log(error);
          this.isLoading = false;
          this.failInformation.message = error.error.message;
          this.toastFail.show();
        },
      });
    }
  }

  downloadReport() {
    console.log(this.dataJson);
    let json = this.dataService.parseStringToJson(this.dataJson);

    console.log(json);
    console.log(this.extension);

    this.isLoading = true;
    this.reportService
      .downloadDoc(this.selectedTemplate, json, this.extension)
      .subscribe((blob) => {
        saveAs(blob, `Báo cáo.${this.extension}`);
        this.isLoading = false;
      });
  }
}
