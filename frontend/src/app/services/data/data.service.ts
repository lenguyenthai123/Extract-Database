import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';
import { Subject } from 'rxjs';
import { Table } from '../../models/table.model';
@Injectable({
  providedIn: 'root',
})
export class DataService {
  mysqlDataTypes = [
    'TINYINT',
    'SMALLINT',
    'MEDIUMINT',
    'INT',
    'BIGINT',
    'FLOAT',
    'DOUBLE',
    'DECIMAL',
    'DATE',
    'DATETIME',
    'TIMESTAMP',
    'TIME',
    'YEAR',
    'CHAR',
    'VARCHAR',
    'BINARY',
    'VARBINARY',
    'TINYBLOB',
    'BLOB',
    'MEDIUMBLOB',
    'LONGBLOB',
    'TINYTEXT',
    'TEXT',
    'MEDIUMTEXT',
    'LONGTEXT',
    'ENUM',
    'SET',
    'JSON',
    'GEOMETRY',
    'POINT',
    'LINESTRING',
    'POLYGON',
    'MULTIPOINT',
    'MULTILINESTRING',
    'MULTIPOLYGON',
    'GEOMETRYCOLLECTION',
  ];
  mysqlNumericDataTypes = [
    'TINYINT',
    'SMALLINT',
    'MEDIUMINT',
    'INT',
    'BIGINT',
    'FLOAT',
    'DOUBLE',
    'DECIMAL',
  ];
  mysqlDataTypesWithoutAutoIncrement: string[] = [
    // Numeric Types
    'FLOAT',
    'DOUBLE',
    'DECIMAL',

    // Date and Time Types
    'DATE',
    'DATETIME',
    'TIMESTAMP',
    'TIME',
    'YEAR',

    // String Types
    'CHAR',
    'VARCHAR',
    'BINARY',
    'VARBINARY',
    'TINYBLOB',
    'BLOB',
    'MEDIUMBLOB',
    'LONGBLOB',
    'TINYTEXT',
    'TEXT',
    'MEDIUMTEXT',
    'LONGTEXT',

    // Special Types
    'ENUM',
    'SET',
    'JSON',

    // Spatial Types
    'GEOMETRY',
    'POINT',
    'LINESTRING',
    'POLYGON',
    'MULTIPOINT',
    'MULTILINESTRING',
    'MULTIPOLYGON',
    'GEOMETRYCOLLECTION',
  ];
  mysqlDataTypesNeedSize: string[] = [
    'CHAR',
    'VARCHAR',
    'BINARY',
    'VARBINARY',
    'DECIMAL',
    'NUMERIC',
    'BIT',
    'FLOAT', // Khi dùng với độ chính xác
    'DOUBLE', // Khi dùng với độ chính xác
    'ENUM', // Cần liệt kê các giá trị
    'SET', // Cần liệt kê các giá trị]
  ];
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

  parseStringToJson(input: string): any {
    try {
      // Giả sử input là một chuỗi JSON hợp lệ
      const result = JSON.parse(input);

      // Kiểm tra nếu kết quả là một đối tượng và có các thuộc tính để đệ quy
      if (result && typeof result === 'object') {
        for (const key in result) {
          if (result.hasOwnProperty(key) && typeof result[key] === 'string') {
            result[key] = this.parseStringToJson(result[key]);
          }
        }
      }

      return result;
    } catch (error) {
      // Xử lý lỗi hoặc trả về chuỗi ban đầu nếu không phải là JSON
      console.error('Error parsing JSON: ', error);
      return input;
    }
  }
}
