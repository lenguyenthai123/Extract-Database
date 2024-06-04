// src/app/utils/string-utils.ts

/**
 * Viết hoa chữ cái đầu của mỗi từ trong chuỗi.
 * @param str Chuỗi đầu vào
 * @returns Chuỗi đã được viết hoa chữ cái đầu
 */
export function capitalizeWords(str: string): string {
  return str.replace(/\b\w/g, (char) => char.toUpperCase());
}

/**
 * Kiểm tra xem chuỗi có phải là chuỗi rỗng hay không.
 * @param str Chuỗi đầu vào
 * @returns `true` nếu chuỗi rỗng, ngược lại `false`
 */
export function isEmpty(str: string): boolean {
  return !str || str.trim().length === 0;
}

/**
 * Biến đổi về thành chuỗi unique
 * @param array Mảng đầu vào
 * @returns Mảng đã được biến đổi unique.
 */

export function getUniqueElements<T>(array: T[]): T[] {
  return array.reduce((accumulator, currentValue) => {
    if (!accumulator.includes(currentValue)) {
      accumulator.push(currentValue);
    }
    return accumulator;
  }, [] as T[]);
}
