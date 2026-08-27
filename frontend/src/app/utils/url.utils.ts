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
