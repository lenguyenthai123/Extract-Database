export class Column {
  public id: number;
  name: string;
  dataType: string;
  nullable: boolean;
  autoIncrement: boolean;
  primaryKey: boolean;
  defaultValue: string;
  description: string;
  size: string;
  isDataTypeNeedSize: boolean;
  disabled: boolean;
  disabledAutoIncrement: boolean;

  defaultValueType: string;
  constructor() {
    this.id = 0;
    this.name = '';
    this.dataType = '';
    this.nullable = false;
    this.autoIncrement = false;
    this.primaryKey = false;
    this.defaultValue = '0';
    this.description = '';
    this.size = '0';
    this.isDataTypeNeedSize = false;
    this.disabled = false;
    this.disabledAutoIncrement = false;
    this.defaultValueType = 'text';
  }
  set(column: Column): void {
    this.id = column.id;
    this.name = column.name;
    this.dataType = column.dataType;
    this.nullable = column.nullable;
    this.autoIncrement = column.autoIncrement;
    this.primaryKey = column.primaryKey;
    this.size = column.size;
    this.isDataTypeNeedSize = column.isDataTypeNeedSize;
    this.defaultValue = column.defaultValue;
    this.description = column.description;
    this.disabled = column.disabled;
  }
}
