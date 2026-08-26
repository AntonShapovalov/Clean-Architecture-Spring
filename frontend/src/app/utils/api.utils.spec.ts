import {HttpErrorResponse} from '@angular/common/http';
import {describe, expect, it} from 'vitest';
import {ProblemDetail} from '../models/problem-detail.model';
import {extractErrorMessage} from './api.utils';

describe('extractErrorMessage', () => {
  it('extracts field error message from ProblemDetail errors array instead of generic detail', () => {
    const problemDetail: ProblemDetail = {
      type: '/api/problems/validation-error',
      title: 'Invalid Request Content',
      status: 400,
      detail: 'Validation failed',
      errors: [
        {
          field: 'query',
          message: 'Query must contain only letters, digits, and spaces'
        }
      ]
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 400,
      statusText: 'Bad Request'
    });

    expect(extractErrorMessage(httpError)).toBe('Query must contain only letters, digits, and spaces');
  });

  it('extracts field error message from ProblemDetail properties.errors', () => {
    const problemDetail: ProblemDetail = {
      type: '/api/problems/validation-error',
      title: 'Invalid Request Content',
      status: 400,
      detail: 'Validation failed',
      properties: {
        errors: [
          {
            field: 'query',
            message: 'Query must contain only letters, digits, and spaces'
          }
        ]
      }
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 400,
      statusText: 'Bad Request'
    });

    expect(extractErrorMessage(httpError)).toBe('Query must contain only letters, digits, and spaces');
  });

  it('extracts and joins multiple error messages from errors array', () => {
    const problemDetail: ProblemDetail = {
      type: '/api/problems/validation-error',
      title: 'Invalid Request Content',
      status: 400,
      detail: 'Validation failed',
      errors: [
        {
          field: 'query',
          message: 'Query is required'
        },
        {
          field: 'query',
          message: 'Query must contain only letters, digits, and spaces'
        }
      ]
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 400,
      statusText: 'Bad Request'
    });

    expect(extractErrorMessage(httpError)).toBe('Query is required, Query must contain only letters, digits, and spaces');
  });

  it('extracts string errors from errors array', () => {
    const problemDetail: ProblemDetail = {
      type: '/api/problems/validation-error',
      title: 'Invalid Request Content',
      status: 400,
      detail: 'Validation failed',
      errors: ['First error', 'Second error']
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 400,
      statusText: 'Bad Request'
    });

    expect(extractErrorMessage(httpError)).toBe('First error, Second error');
  });

  it('extracts detail message from ProblemDetail in HttpErrorResponse', () => {
    const problemDetail: ProblemDetail = {
      type: 'about:blank',
      title: 'Not Found',
      status: 404,
      detail: 'Search with the given ID was not found'
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 404,
      statusText: 'Not Found'
    });

    expect(extractErrorMessage(httpError)).toBe('Search with the given ID was not found');
  });

  it('extracts title when detail is not present in ProblemDetail', () => {
    const problemDetail: ProblemDetail = {
      type: 'about:blank',
      title: 'Internal server error',
      status: 500
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 500,
      statusText: 'Internal Server Error'
    });

    expect(extractErrorMessage(httpError)).toBe('Internal server error');
  });

  it('extracts plain string error body from HttpErrorResponse', () => {
    const httpError = new HttpErrorResponse({
      error: 'Backend failure',
      status: 500,
      statusText: 'Internal Server Error'
    });

    expect(extractErrorMessage(httpError)).toBe('Backend failure');
  });

  it('falls back to HttpErrorResponse message when error payload is empty or not informative', () => {
    const httpError = new HttpErrorResponse({
      error: null,
      status: 500,
      statusText: 'Internal Server Error',
      url: '/api/movies'
    });

    expect(extractErrorMessage(httpError)).toBe(httpError.message);
  });

  it('extracts message from standard Error instance', () => {
    const error = new Error('Custom exception message');
    expect(extractErrorMessage(error)).toBe('Custom exception message');
  });

  it('returns string error directly when error is a string', () => {
    expect(extractErrorMessage('Direct error string')).toBe('Direct error string');
  });

  it('returns default fallback message when error is unknown or unexpected', () => {
    expect(extractErrorMessage(null)).toBe('An unexpected error occurred');
    expect(extractErrorMessage(undefined)).toBe('An unexpected error occurred');
    expect(extractErrorMessage(12345)).toBe('An unexpected error occurred');
    expect(extractErrorMessage({})).toBe('An unexpected error occurred');
    expect(extractErrorMessage(new Error(''))).toBe('An unexpected error occurred');
    expect(extractErrorMessage(new Error('   '))).toBe('An unexpected error occurred');
    expect(extractErrorMessage('')).toBe('An unexpected error occurred');
    expect(extractErrorMessage('   ')).toBe('An unexpected error occurred');
  });

  it('handles array values gracefully without treating arrays as records', () => {
    const problemDetail = {
      type: '/api/problems/error',
      title: 'Error',
      status: 400,
      properties: []
    };
    const httpError = new HttpErrorResponse({
      error: problemDetail,
      status: 400,
      statusText: 'Bad Request'
    });

    expect(extractErrorMessage(httpError)).toBe('Error');
  });
});
