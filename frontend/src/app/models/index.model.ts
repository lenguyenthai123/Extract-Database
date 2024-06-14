export class Index {
  public id: number;
  name: string;
  columns: string[];
  referencedColumnName: string;
  disabled: boolean;
  oldName: string;

  constructor() {
    this.id = 0;
    this.name = '';
    this.referencedColumnName = '';
    this.columns = [];
    this.disabled = false;
    this.oldName = '';
  }
  set(index: Index): void {
    this.id = index.id;
    this.name = index.name;
    this.referencedColumnName = index.referencedColumnName;
    this.disabled = index.disabled;
    this.columns = [];
    index.columns.forEach((column) => {
      this.columns.push(column);
    });
    this.oldName = index.oldName;
  }
}
export interface IdList {
  ids: string[];
}
