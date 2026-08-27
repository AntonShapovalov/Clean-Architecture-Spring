import {describe, expect, it} from 'vitest';
import {isValidUrl} from './url.utils';

describe('isValidUrl', () => {
  it('returns true for absolute HTTP(S) and relative URLs', () => {
    expect(isValidUrl('https://example.com/poster.jpg')).toBe(true);
    expect(isValidUrl('http://example.com/poster.jpg')).toBe(true);
    expect(isValidUrl('/images/poster.jpg')).toBe(true);
    expect(isValidUrl('poster.jpg')).toBe(true);
  });

  it('returns false for empty and unavailable poster values', () => {
    expect(isValidUrl('')).toBe(false);
    expect(isValidUrl('   ')).toBe(false);
    expect(isValidUrl('N/A')).toBe(false);
    expect(isValidUrl('n/a')).toBe(false);
    expect(isValidUrl('  N/A  ')).toBe(false);
    expect(isValidUrl(' n/a ')).toBe(false);
    expect(isValidUrl(null)).toBe(false);
    expect(isValidUrl(undefined)).toBe(false);
  });

  it('returns false for malformed URLs and unsupported schemes', () => {
    expect(isValidUrl('https://')).toBe(false);
    expect(isValidUrl('https://example .com/poster.jpg')).toBe(false);
    expect(isValidUrl('javascript:alert(1)')).toBe(false);
  });
});
