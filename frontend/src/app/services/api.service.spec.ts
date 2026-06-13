import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';
import { Search, SearchQuery } from '../models/search.model';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ApiService
      ]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('saveSearch', () => {
    it('should POST to /api/search and return void', () => {
      const query: SearchQuery = { query: 'test query' };

      service.saveSearch(query).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne('/api/search');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(query);
      req.flush(null, { status: 204, statusText: 'No Content' });
    });
  });

  describe('getSearchHistory', () => {
    it('should GET from /api/search/history and return searches', () => {
      const mockHistory: Search[] = [
        { id: 1, query: 'query 1', updatedDate: '2023-01-01', lastSeenAt: '2023-01-01T00:00:00Z', isExpired: false },
        { id: 2, query: 'query 2', updatedDate: '2023-01-02', lastSeenAt: '2023-01-02T00:00:00Z', isExpired: false }
      ];

      service.getSearchHistory().subscribe(history => {
        expect(history).toEqual(mockHistory);
      });

      const req = httpMock.expectOne('/api/search/history');
      expect(req.request.method).toBe('GET');
      req.flush(mockHistory);
    });
  });
});
