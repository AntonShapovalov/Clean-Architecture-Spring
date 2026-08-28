/**
 * Validates whether a provided value is a valid HTTP/HTTPS or relative URL.
 *
 * Rejects `null`, `undefined`, empty or whitespace-only strings, the string `'N/A'` (case-insensitive),
 * and URLs with unsupported protocols or malformed syntax.
 *
 * @param value The URL string to validate.
 * @returns `true` if the string is a valid HTTP, HTTPS, or relative URL; otherwise `false`.
 */
export function isValidUrl(value: string | null | undefined): boolean {
  const trimmed = value?.trim();
  if (!trimmed || trimmed.toUpperCase() === 'N/A') {
    return false;
  }

  try {
    const url = new URL(trimmed, 'http://localhost');
    return url.protocol === 'http:' || url.protocol === 'https:';
  } catch {
    return false;
  }
}
