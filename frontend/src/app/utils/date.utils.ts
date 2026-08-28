/**
 * Determines whether a given date string or `Date` instance matches the current calendar day (today).
 *
 * Comparisons are performed using the local time zone's year, month, and day.
 *
 * @param value The date to evaluate, provided as a date string (e.g. ISO format) or a `Date` object.
 * @returns `true` if the value represents a valid date that falls on today's local date; otherwise `false`.
 */
export function isDateToday(value: string | Date): boolean {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return false;
  }

  const today = new Date();

  return date.getFullYear() === today.getFullYear()
    && date.getMonth() === today.getMonth()
    && date.getDate() === today.getDate();
}
