import { Component } from '@angular/core';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss',
})
export class ModalComponent {
  title: string | null = null;
  content: string | null = null;
  constructor(public modalRef: MdbModalRef<ModalComponent>) {}

  close(): void {
    const closeMessage = 'no';
    this.modalRef.close(closeMessage);
  }
  confirm(): void {
    const confirmMessage = 'yes';
    this.modalRef.close(confirmMessage);
  }
}
