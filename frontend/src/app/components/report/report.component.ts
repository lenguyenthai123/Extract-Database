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
  templates: string[] = [
    'Template 1',
    'Template 2',
    'Template 3',
    'Template 4',
    'Template 5',
  ];
  selectedTemplate: string | null = null;

  contentText: string = '';
  type: string = 'doc';

  constructor(private reportService: ReportService) {}

  selectTemplate(template: string) {
    this.selectedTemplate = template;
  }

  uploadTemplate(event: any) {
    const file = event.target.files[0];
    if (file) {
      console.log(`File uploaded: ${file.name}`);
      // Handle file upload logic here
      // Add template to the list
      // Split the file name to get the template name without extension
      const fileName = file.name.split('.')[0];
      this.templates.push(fileName);

      const formData = new FormData();
      formData.append('file', file, file.name);
      formData.append('usernameId', '12');

      this.reportService.addTemplate(formData).subscribe({
        next: (data) => {
          console.log(data);
        },
        error: (error) => {
          console.log(error);
        },
      });
    }
  }

  downloadReport() {
    let fileNameTemplate = this.selectedTemplate;
    if (!fileNameTemplate) {
      fileNameTemplate = 'default';
    }
    if (this.type === 'doc') {
      this.reportService.downloadDoc(fileNameTemplate).subscribe((blob) => {
        saveAs(blob, `Báo cáo.doc`);
      });
    } else {
    }
  }
}
