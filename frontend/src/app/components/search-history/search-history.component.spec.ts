import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SearchHistoryComponent } from './search-history.component';
import { ApiService } from '../../services/api.service';
import { of, throwError } from 'rxjs';
import { Search } from '../../models/search.model';
import { describe, it, expect, beforeEach, vi, Mock } from 'vitest';

describe('SearchHistoryComponent', () => {
  let component: SearchHistoryComponent;
  let fixture: ComponentFixture<SearchHistoryComponent>;
  let apiServiceMock: {
    getSearchHistory: Mock;
    saveSearch: Mock;
  };

  const mockHistory: Search[] = [
    { id: 1, query: 'test 1', updatedDate: '2023-01-01', lastSeenAt: '', isExpired: false },
    { id: 2, query: 'test 2', updatedDate: '2023-01-02', lastSeenAt: '', isExpired: false }
  ];

  beforeEach(async () => {
    apiServiceMock = {
      getSearchHistory: vi.fn().mockReturnValue(of(mockHistory)),
      saveSearch: vi.fn().mockReturnValue(of(undefined))
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SearchHistoryComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load history on init', () => {
    expect(apiServiceMock.getSearchHistory).toHaveBeenCalled();
    expect(component['history']()).toEqual(mockHistory);
  });

  it('should display history items in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.list-group-item');
    // The empty state item is also a .list-group-item, but it's only shown when history is empty.
    // In this test, history is mocked with 2 items.
    expect(items.length).toBe(2);
    expect(items[0].textContent).toContain('test 1');
    expect(items[1].textContent).toContain('test 2');
  });

  it('should not call saveSearch if query is invalid (too short)', () => {
    component['queryControl'].setValue('ab');
    component['onSearch']();
    expect(apiServiceMock.saveSearch).not.toHaveBeenCalled();
  });

  it('should call saveSearch and reload history on valid search', () => {
    const newQuery = 'new search';
    component['queryControl'].setValue(newQuery);

    // Reset call count from init
    apiServiceMock.getSearchHistory.mockClear();

    component['onSearch']();

    expect(apiServiceMock.saveSearch).toHaveBeenCalledWith({ query: newQuery });
    expect(apiServiceMock.getSearchHistory).toHaveBeenCalled();
    expect(component['queryControl'].value).toBe(''); // Should be reset
  });

  it('should handle error when loading history', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {
      // Intentionally empty
    });
    apiServiceMock.getSearchHistory.mockReturnValue(throwError(() => new Error('API Error')));

    // Trigger another load
    component['loadHistory']();

    expect(consoleSpy).toHaveBeenCalledWith('Error loading history:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('should handle error when saving search', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {
      // Intentionally empty
    });
    apiServiceMock.saveSearch.mockReturnValue(throwError(() => new Error('Save Error')));

    component['queryControl'].setValue('valid query');
    component['onSearch']();

    expect(consoleSpy).toHaveBeenCalledWith('Error saving search:', expect.any(Error));
    consoleSpy.mockRestore();
  });
});
