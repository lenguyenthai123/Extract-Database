import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function requireServiceNameIfOracle(
  typeControl: AbstractControl
): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const isOracle = typeControl.value === 'oracle';
    const isServiceNameEmpty = !control.value;
    return isOracle && isServiceNameEmpty
      ? { requiredServiceName: true }
      : null;
  };
}
