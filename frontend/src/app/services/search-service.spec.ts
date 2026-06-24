import { TestBed } from '@angular/core/testing';
import { SearchService } from './search-service';
import { ApiService } from './api.service';
import { of } from 'rxjs';
import { Search, SearchQuery } from '../models/search.model';
import { describe, it, expect, beforeEach, vi, Mock } from 'vitest';

describe('SearchService', () => {
  let service: SearchService;
  let apiServiceMock: {
    getSearchHistory: Mock;
    saveSearch: Mock;
  };

  const mockHistory: Search[] = [
    { id: 1, query: 'query 1', updatedDate: '2023-01-01', lastSeenAt: '2023-01-01T00:00:00Z', isExpired: false },
    { id: 2, query: 'query 2', updatedDate: '2023-01-02', lastSeenAt: '2023-01-02T00:00:00Z', isExpired: false }
  ];

  beforeEach(() => {
    apiServiceMock = {
      getSearchHistory: vi.fn().mockReturnValue(of(mockHistory)),
      saveSearch: vi.fn().mockReturnValue(of(undefined))
    };

    TestBed.configureTestingModule({
      providers: [
        SearchService,
        { provide: ApiService, useValue: apiServiceMock }
      ]
    });
    service = TestBed.inject(SearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have empty initial history', () => {
    expect(service.history()).toEqual([]);
  });

  it('should have null initial recentSearch', () => {
    expect(service.recentSearch()).toBeNull();
  });

  describe('loadHistory', () => {
    it('should update history signal on success', () => {
      service.loadHistory().subscribe();
      expect(apiServiceMock.getSearchHistory).toHaveBeenCalled();
      expect(service.history()).toEqual(mockHistory);
    });

    it('should update recentSearch signal on success', () => {
      service.loadHistory().subscribe();
      expect(service.recentSearch()).toEqual(mockHistory[0]);
    });

    it('should return history observable', () => {
      let result: Search[] = [];
      service.loadHistory().subscribe(h => result = h);
      expect(result).toEqual(mockHistory);
    });
  });

  describe('saveSearch', () => {
    it('should save search and then reload history', () => {
      const query: SearchQuery = { query: 'new query' };
      const newHistory: Search[] = [
        { id: 3, query: 'new query', updatedDate: '2023-01-03', lastSeenAt: '2023-01-03T00:00:00Z', isExpired: false },
        ...mockHistory
      ];
      apiServiceMock.getSearchHistory.mockReturnValue(of(newHistory));

      service.saveSearch(query).subscribe();

      expect(apiServiceMock.saveSearch).toHaveBeenCalledWith(query);
      expect(apiServiceMock.getSearchHistory).toHaveBeenCalled();
      expect(service.history()).toEqual(newHistory);
      expect(service.recentSearch()).toEqual(newHistory[0]);
    });

    it('should return new history observable', () => {
      const query: SearchQuery = { query: 'new query' };
      const newHistory: Search[] = [
        { id: 3, query: 'new query', updatedDate: '2023-01-03', lastSeenAt: '2023-01-03T00:00:00Z', isExpired: false },
        ...mockHistory
      ];
      apiServiceMock.getSearchHistory.mockReturnValue(of(newHistory));

      let result: Search[] = [];
      service.saveSearch(query).subscribe(h => result = h);
      expect(result).toEqual(newHistory);
    });
  });
});
