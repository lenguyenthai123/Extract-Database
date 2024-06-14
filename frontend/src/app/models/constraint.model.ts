export class Constraint {
  public id: number;
  name: string;
  fieldName: string;
  constraintType: string;
  refTableName: string;
  refColumnName: string;
  oldName: string;
  disabled: boolean;
  disabledAutoIncrement: boolean;
  defaultValueType: string;
  columnList: string[];
  columnName: string;

  constructor() {
    this.id = 0;
    this.name = '';
    this.fieldName = '';
    this.refTableName = '';
    this.refColumnName = '';
    this.disabled = false;
    this.disabledAutoIncrement = false;
    this.defaultValueType = 'text';
    this.constraintType = 'UNIQUE';
    this.oldName = '';
    this.columnList = [];
    this.columnName = '';
  }
  set(constraint: Constraint): void {
    this.id = constraint.id;
    this.name = constraint.name;
    this.fieldName = constraint.fieldName;
    this.refTableName = constraint.refTableName;
    this.refColumnName = constraint.refColumnName;
    this.columnList = [];
    constraint.columnList.forEach((column) => {
      this.columnList.push(column);
    });
    this.oldName = constraint.oldName;
    this.disabled = constraint.disabled;
    this.disabledAutoIncrement = constraint.disabledAutoIncrement;
    this.defaultValueType = constraint.defaultValueType;
    this.constraintType = constraint.constraintType;
    this.columnName = constraint.columnName;
  }
}
