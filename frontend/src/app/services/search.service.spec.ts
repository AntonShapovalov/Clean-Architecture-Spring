import { TestBed } from '@angular/core/testing';
import { SearchService } from './search.service';
import { ApiService } from './api.service';
import { of, throwError } from 'rxjs';
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

  it('should have null initial error', () => {
    expect(service.error()).toBeNull();
  });

  describe('getFilteredHistory', () => {
    beforeEach(() => service.history.set(mockHistory));

    it('should match query substrings case-insensitively and ignore surrounding whitespace', () => {
      expect(service.getFilteredHistory('  QUERY 1  ')).toEqual([mockHistory[0]]);
    });

    it('should return all history for an empty or whitespace-only filter', () => {
      expect(service.getFilteredHistory('')).toEqual(mockHistory);
      expect(service.getFilteredHistory('   ')).toEqual(mockHistory);
    });

    it('should use the latest history signal value', () => {
      const updatedHistory = [mockHistory[1]];
      service.history.set(updatedHistory);

      expect(service.getFilteredHistory('query')).toEqual(updatedHistory);
    });
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

    it('should return a success flag', () => {
      let result = false;
      service.loadHistory().subscribe(loaded => result = loaded);
      expect(result).toBe(true);
    });

    it('should set empty history and clear recentSearch when no history is returned', () => {
      apiServiceMock.getSearchHistory.mockReturnValue(of([]));

      let result = false;
      service.loadHistory().subscribe(loaded => result = loaded);

      expect(result).toBe(true);
      expect(service.history()).toEqual([]);
      expect(service.recentSearch()).toBeNull();
    });

    it('should log load errors, preserve the error, and leave existing state unchanged', () => {
      const error = new Error('Load error');
      const errorHandler = vi.fn();
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
      service.history.set(mockHistory);
      service.recentSearch.set(mockHistory[0]);
      apiServiceMock.getSearchHistory.mockReturnValue(throwError(() => error));

      service.loadHistory().subscribe({ error: errorHandler });

      expect(errorHandler).toHaveBeenCalledWith(error);
      expect(consoleSpy).toHaveBeenCalledWith('Error loading history:', error);
      expect(service.history()).toEqual(mockHistory);
      expect(service.recentSearch()).toEqual(mockHistory[0]);
      consoleSpy.mockRestore();
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

    it('should return a success flag after reloading history', () => {
      const query: SearchQuery = { query: 'new query' };
      const newHistory: Search[] = [
        { id: 3, query: 'new query', updatedDate: '2023-01-03', lastSeenAt: '2023-01-03T00:00:00Z', isExpired: false },
        ...mockHistory
      ];
      apiServiceMock.getSearchHistory.mockReturnValue(of(newHistory));

      let result = false;
      service.saveSearch(query).subscribe(loaded => result = loaded);
      expect(result).toBe(true);
    });

    it('should propagate request errors from apiService and update error signal', () => {
      const error = new Error('Save error');
      const errorHandler = vi.fn();
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
      apiServiceMock.saveSearch.mockReturnValue(throwError(() => error));

      service.saveSearch({ query: 'failed query' }).subscribe({ error: errorHandler });

      expect(errorHandler).toHaveBeenCalledWith(expect.objectContaining({ message: 'Save error' }));
      expect(service.error()).toBe('Save error');
      expect(consoleSpy).toHaveBeenCalledWith('Error saving search:', error);
      consoleSpy.mockRestore();
    });

    it('should clear error signal on successful saveSearch', () => {
      service.error.set('Previous error');
      apiServiceMock.saveSearch.mockReturnValue(of(undefined));
      apiServiceMock.getSearchHistory.mockReturnValue(of(mockHistory));

      service.saveSearch({ query: 'new query' }).subscribe();

      expect(service.error()).toBeNull();
    });

    it('should propagate and log a history reload error after saving without replacing current history', () => {
      const query: SearchQuery = {query: 'new query'};
      const error = new Error('Reload error');
      const errorHandler = vi.fn();
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
      service.loadHistory().subscribe();
      apiServiceMock.saveSearch.mockReturnValue(of(undefined));
      apiServiceMock.getSearchHistory.mockReturnValue(throwError(() => error));

      service.saveSearch(query).subscribe({error: errorHandler});

      expect(errorHandler).toHaveBeenCalledWith(error);
      expect(consoleSpy).toHaveBeenCalledWith('Error loading history:', error);
      expect(service.history()).toEqual(mockHistory);
      expect(service.recentSearch()).toEqual(mockHistory[0]);
      consoleSpy.mockRestore();
    });
  });

  describe('updateSearch', () => {
    it('should save search to API, update lastSeenAt in history without reordering, and set recentSearch to selected search', () => {
      service.loadHistory().subscribe();
      expect(service.history()).toEqual(mockHistory);
      expect(service.recentSearch()).toEqual(mockHistory[0]);

      const selectedSearch = mockHistory[1];
      const fakeDate = '2026-08-24T12:00:00.000Z';
      vi.useFakeTimers();
      vi.setSystemTime(new Date(fakeDate));

      service.updateSearch(selectedSearch).subscribe();

      expect(apiServiceMock.saveSearch).toHaveBeenCalledWith({ query: selectedSearch.query });
      expect(apiServiceMock.getSearchHistory).toHaveBeenCalledTimes(1);

      const updatedHistory = service.history();
      expect(updatedHistory.length).toBe(2);
      expect(updatedHistory[0]).toEqual(mockHistory[0]);
      expect(updatedHistory[1]).toEqual({
        ...selectedSearch,
        lastSeenAt: fakeDate
      });
      expect(service.recentSearch()).toEqual({
        ...selectedSearch,
        lastSeenAt: fakeDate
      });

      vi.useRealTimers();
    });

    it('should return a success flag', () => {
      service.loadHistory().subscribe();

      const selectedSearch = mockHistory[1];
      let result = false;
      service.updateSearch(selectedSearch).subscribe(updated => result = updated);

      expect(result).toBe(true);
    });

    it('should propagate request errors from apiService and update error signal on updateSearch', () => {
      const error = new Error('Update error');
      const errorHandler = vi.fn();
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
      apiServiceMock.saveSearch.mockReturnValue(throwError(() => error));

      service.updateSearch(mockHistory[0]).subscribe({ error: errorHandler });

      expect(errorHandler).toHaveBeenCalledWith(expect.objectContaining({ message: 'Update error' }));
      expect(service.error()).toBe('Update error');
      expect(consoleSpy).toHaveBeenCalledWith('Error updating search:', error);
      consoleSpy.mockRestore();
    });
  });
});
