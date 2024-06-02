export class Column {
  public id: number;
  name: string;
  dataType: string;
  nullable: boolean;
  autoIncrement: boolean;
  primaryKey: boolean;
  defaultValue: string;
  description: string;
  disabled: boolean;

  constructor() {
    this.id = 0;
    this.name = '';
    this.dataType = '';
    this.nullable = false;
    this.autoIncrement = false;
    this.primaryKey = false;
    this.defaultValue = '';
    this.description = '';
    this.disabled = false;
  }
  set(column: Column): void {
    this.id = column.id;
    this.name = column.name;
    this.dataType = column.dataType;
    this.nullable = column.nullable;
    this.autoIncrement = column.autoIncrement;
    this.primaryKey = column.primaryKey;
    this.defaultValue = column.defaultValue;
    this.description = column.description;
    this.disabled = column.disabled;
  }
}
