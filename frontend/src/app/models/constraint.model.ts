export class Constraint {
  public id: number;
  name: string;
  fieldName: string;
  typeName: string;
  referencedTableName: string;
  referencedColumnName: string;
  disabled: boolean;
  disabledAutoIncrement: boolean;
  defaultValueType: string;

  constructor() {
    this.id = 0;
    this.name = '';
    this.fieldName = '';
    this.typeName = '';
    this.referencedTableName = '';
    this.referencedColumnName = '';
    this.disabled = false;
    this.disabledAutoIncrement = false;
    this.defaultValueType = 'text';
  }
  set(constraint: Constraint): void {
    this.id = constraint.id;
    this.name = constraint.name;
    this.fieldName = constraint.fieldName;
    this.typeName = constraint.typeName;
    this.referencedTableName = constraint.referencedTableName;
    this.referencedColumnName = constraint.referencedColumnName;
    this.disabled = constraint.disabled;
  }
}
