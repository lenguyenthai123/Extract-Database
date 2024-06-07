export class Index {
  public id: number;
  name: string;
  referencedColumnName: string;
  disabled: boolean;

  constructor() {
    this.id = 0;
    this.name = '';
    this.referencedColumnName = '';
    this.disabled = false;
  }
  set(index: Index): void {
    this.id = index.id;
    this.name = index.name;
    this.referencedColumnName = index.referencedColumnName;
    this.disabled = index.disabled;
  }
}
