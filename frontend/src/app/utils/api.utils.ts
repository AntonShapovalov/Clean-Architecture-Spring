import {HttpErrorResponse} from '@angular/common/http';

/**
 * Extracts a human-readable error message from an unknown error value.
 *
 * Supports Angular `HttpErrorResponse` objects with RFC 7807 / RFC 9457 `ProblemDetail`
 * bodies (validation error lists, detail, title), standard JavaScript `Error` instances,
 * plain string messages, and returns a default fallback message when no specific error
 * information is available.
 *
 * @param error The error value, object, or exception to extract a message from.
 * @returns A descriptive error message string.
 */
export function extractErrorMessage(error: unknown): string {
  const httpMessage = getHttpErrorMessage(error);
  if (httpMessage !== null) {
    return httpMessage;
  }

  const genericMessage = getGenericErrorMessage(error);
  return genericMessage ?? 'An unexpected error occurred';
}

function getHttpErrorMessage(error: unknown): string | null {
  if (!isHttpError(error)) {
    return null;
  }

  return getProblemMessage(error.error) ?? (error.message ? error.message : null);
}

function getProblemMessage(problem: unknown): string | null {
  const directMessage = getNonEmptyString(problem);
  if (directMessage) {
    return directMessage;
  }

  if (!isRecord(problem)) {
    return null;
  }

  const errorMessage = getErrorMessage(getProblemErrors(problem));
  return errorMessage ?? getFirstMessage(problem, ['detail', 'title']);
}

function getProblemErrors(problem: Record<string, unknown>): unknown {
  const properties = isRecord(problem['properties']) ? problem['properties'] : undefined;
  const errorKeys = ['errors', 'invalidParams', 'invalid-params'];

  for (const key of errorKeys) {
    const errors = problem[key] ?? properties?.[key];
    if (errors !== undefined && errors !== null) {
      return errors;
    }
  }

  return null;
}

function getGenericErrorMessage(error: unknown): string | null {
  if (error instanceof Error) {
    return getNonEmptyString(error.message);
  }
  if (typeof error === 'string') {
    return getNonEmptyString(error);
  }
  return null;
}

function isHttpError(error: unknown): error is HttpErrorResponse | { error: unknown; message?: string } {
  return error instanceof HttpErrorResponse || (isRecord(error) && 'error' in error);
}

function getErrorMessage(errors: unknown): string | null {
  if (Array.isArray(errors)) {
    return joinMessages(errors.map(getArrayItemMessage).filter(isString));
  }

  if (isRecord(errors)) {
    const messages = Object.values(errors).flatMap(getObjectValueMessages);
    return joinMessages(messages);
  }

  return null;
}

function getArrayItemMessage(item: unknown): string | null {
  const directMessage = getNonEmptyString(item);
  if (directMessage) {
    return directMessage;
  }

  if (!isRecord(item)) {
    return null;
  }

  return getFirstMessage(item, ['message', 'defaultMessage', 'reason', 'detail']);
}

function getObjectValueMessages(value: unknown): string[] {
  if (Array.isArray(value)) {
    return value.map(getNonEmptyString).filter(isString);
  }

  const message = getNonEmptyString(value);
  return message ? [message] : [];
}

function getFirstMessage(object: Record<string, unknown>, keys: string[]): string | null {
  for (const key of keys) {
    const message = getNonEmptyString(object[key]);
    if (message) {
      return message;
    }
  }

  return null;
}

function getNonEmptyString(value: unknown): string | null {
  return typeof value === 'string' && value.trim().length > 0 ? value.trim() : null;
}

function joinMessages(messages: string[]): string | null {
  return messages.length > 0 ? messages.join(', ') : null;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

function isString(value: string | null): value is string {
  return value !== null;
}
