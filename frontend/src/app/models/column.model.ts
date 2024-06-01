export interface Column {
  id: number;
  name: string;
  dataType: string;
  nullable: boolean;
  autoIncrement: boolean;
  primaryKey: boolean;
  defaultValue: string;
  description: string;
  disabled: boolean;
}
