export class User {
  public id: number;
  username: string;
  password: string;
  confirmPassword: string;

  constructor() {
    this.id = 0;
    this.username = '';
    this.password = '';
    this.confirmPassword = '';
  }
  set(user: User): void {
    this.id = user.id;
    this.username = user.username;
    this.password = user.password;
    this.confirmPassword = user.confirmPassword;
  }
}
