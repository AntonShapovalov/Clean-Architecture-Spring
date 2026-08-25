import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import {isDateToday} from './date.utils';

describe('isDateToday', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => vi.useRealTimers());

  it('returns true when the date has the same local calendar day as today', () => {
    const today = new Date(2026, 7, 25, 12, 30);
    vi.setSystemTime(today);

    expect(isDateToday(new Date(2026, 7, 25, 0, 1))).toBe(true);
    expect(isDateToday(today.toISOString())).toBe(true);
  });

  it('returns false for another day or an invalid date', () => {
    vi.setSystemTime(new Date(2026, 7, 25, 12, 30));

    expect(isDateToday(new Date(2026, 7, 24, 23, 59))).toBe(false);
    expect(isDateToday('not a date')).toBe(false);
  });
});
