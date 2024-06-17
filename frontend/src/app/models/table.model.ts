export class Table {
  id: number;
  schemaName: string;
  name: string;
  description: string;
  constructor(
    id: number = 0,
    name: string = '',
    description: string = '',
    schemaName: string = ''
  ) {
    this.id = id;
    this.name = name;
    this.schemaName = schemaName;
    this.description = description;
  }
}
