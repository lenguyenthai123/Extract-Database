export interface Column {
  id: number;
  fieldName: string;
  dataType: string;
  nullable: boolean;
  autoIncrement: boolean;
  primaryKey: boolean;
  defaultValue: string;
  description: string;
  disabled: boolean;
}
