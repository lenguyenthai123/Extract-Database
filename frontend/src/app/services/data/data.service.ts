import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';
import { Subject } from 'rxjs';
import { Table } from '../../models/table.model';
@Injectable({
  providedIn: 'root',
})
export class DataService {
  constructor() {}

  key = '123456';

  // Key for encrypt and decrypt
  private encrypt(txt: string): string {
    return CryptoJS.AES.encrypt(txt, this.key).toString();
  }

  private decrypt(txtToDecrypt: string) {
    return CryptoJS.AES.decrypt(txtToDecrypt, this.key).toString(
      CryptoJS.enc.Utf8
    );
  }
  //-------------------------------

  public saveData(key: string, value: string) {
    localStorage.setItem(key, this.encrypt(value));
  }

  public getData(key: string) {
    let data = localStorage.getItem(key) || '';
    return this.decrypt(data);
  }

  public removeData(key: string) {
    localStorage.removeItem(key);
  }

  public clearData() {
    localStorage.clear();
  }

  //--------------------------------------------------------------
  private _subject = new Subject<Table>();

  pulishString(event: Table) {
    this._subject.next(event);
  }

  get events$() {
    return this._subject.asObservable();
  }
}
