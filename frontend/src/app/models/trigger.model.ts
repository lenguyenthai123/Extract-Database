export class Trigger {
  public id: number;
  name: string;
  event: string;
  timing: string;
  doAction: string;
  actionCondition: string;
  disabled: boolean;
  disabledAutoIncrement: boolean;
  defaultValueType: string;

  constructor() {
    this.id = 0;
    this.name = '';
    this.event = 'INSERT';
    this.timing = 'BEFORE';
    this.doAction = '';
    this.actionCondition = '';
    this.disabled = false;
    this.disabledAutoIncrement = false;
    this.defaultValueType = 'text';
  }
  set(trigger: Trigger): void {
    this.id = trigger.id;
    this.name = trigger.name;
    this.event = trigger.event;
    this.timing = trigger.timing;
    this.doAction = trigger.doAction;
    this.actionCondition = trigger.actionCondition;
    this.disabled = trigger.disabled;
  }
}
