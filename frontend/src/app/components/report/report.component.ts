import { Component } from '@angular/core';
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
  selectedTemplate: string | null = null;
  docHtml: string = '';

  dataJson: string = '';
  type: string = 'doc';

  constructor(
    private reportService: ReportService,
    private dataService: DataService
  ) {}

  ngOnInit() {
    this.reportService.getListTemplate().subscribe({
      next: (data) => {
        this.templates = data;
        this.templates.push('default');
        console.log(data);
      },
      error: (error) => {
        console.log(error);
      },
    });
  }

  selectTemplate(template: string) {
    this.selectedTemplate = template;

    if (template === 'default') {
      this.docHtml = '';
      return;
    }
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
            })
            .catch((error) => {
              console.error('Error converting file:', error);
            });
        };
        reader.readAsArrayBuffer(blob);
      },
      error: (err) => {
        console.error('Error downloading file:', err);
      },
    });
  }

  uploadTemplate(event: any) {
    const file = event.target.files[0];
    if (file) {
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
        },
        error: (error) => {
          console.log(error);
        },
      });
    }
  }

  downloadReport() {
    console.log(this.dataJson);
    let json = this.dataService.parseStringToJson(this.dataJson);

    console.log(json);

    let fileNameTemplate = this.selectedTemplate;
    console.log('fileNameTemplate: ', fileNameTemplate);
    if (!fileNameTemplate) {
      fileNameTemplate = 'default';
    }
    if (this.type === 'doc') {
      this.reportService
        .downloadDoc(fileNameTemplate, json, 'pdf')
        .subscribe((blob) => {
          saveAs(blob, `Báo cáo.docx`);
        });
    } else {
    }
  }
}
