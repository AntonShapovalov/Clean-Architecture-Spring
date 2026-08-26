import {HttpErrorResponse} from '@angular/common/http';
import {ProblemDetail} from '../models/problem-detail.model';

export function extractErrorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse || (typeof error === 'object' && error !== null && 'error' in error)) {
    const httpError = error as HttpErrorResponse | { error: unknown; message?: string };
    const problem = httpError.error as ProblemDetail | string | null | undefined;
    if (typeof problem === 'string' && problem.trim().length > 0) {
      return problem;
    }
    if (problem && typeof problem === 'object') {
      if ('detail' in problem && typeof problem.detail === 'string' && problem.detail.trim().length > 0) {
        return problem.detail;
      }
      if ('title' in problem && typeof problem.title === 'string' && problem.title.trim().length > 0) {
        return problem.title;
      }
    }
    if (httpError.message) {
      return httpError.message;
    }
  }
  if (error instanceof Error) {
    return error.message;
  }
  if (typeof error === 'string') {
    return error;
  }
  return 'An unexpected error occurred';
}
