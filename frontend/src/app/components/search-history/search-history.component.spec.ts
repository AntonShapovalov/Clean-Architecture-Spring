import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SearchHistoryComponent } from './search-history.component';
import { SearchService } from '../../services/search-service';
import { Search } from '../../models/search.model';
import { describe, it, expect, beforeEach, vi, Mock } from 'vitest';
import { of, throwError } from 'rxjs';
import { signal, Signal } from '@angular/core';

describe('SearchHistoryComponent', () => {
  let component: SearchHistoryComponent;
  let fixture: ComponentFixture<SearchHistoryComponent>;
  let searchServiceMock: {
    loadHistory: Mock;
    saveSearch: Mock;
    history: Signal<Search[]>;
  };

  const mockHistory: Search[] = [
    { id: 1, query: 'test 1', updatedDate: '2023-01-01', lastSeenAt: '', isExpired: false },
    { id: 2, query: 'test 2', updatedDate: '2023-01-02', lastSeenAt: '', isExpired: false }
  ];

  beforeEach(async () => {
    searchServiceMock = {
      loadHistory: vi.fn().mockReturnValue(of(mockHistory)),
      saveSearch: vi.fn().mockReturnValue(of(mockHistory)),
      history: signal(mockHistory)
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SearchHistoryComponent],
      providers: [
        { provide: SearchService, useValue: searchServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadHistory on init', () => {
    expect(searchServiceMock.loadHistory).toHaveBeenCalled();
  });

  it('should display history items in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.history-list li');
    expect(items.length).toBe(2);
    expect(items[0].textContent).toContain('test 1');
    expect(items[1].textContent).toContain('test 2');
  });

  it('should not call saveSearch if query is invalid (too short)', () => {
    component['queryControl'].setValue('ab');
    component['onSearch']();
    expect(searchServiceMock.saveSearch).not.toHaveBeenCalled();
  });

  it('should call saveSearch on valid search', () => {
    const newQuery = 'new search';
    component['queryControl'].setValue(newQuery);

    component['onSearch']();

    expect(searchServiceMock.saveSearch).toHaveBeenCalledWith({ query: newQuery });
    expect(component['queryControl'].value).toBe(''); // Should be reset
  });

  it('should handle error when loading history', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
    searchServiceMock.loadHistory.mockReturnValue(throwError(() => new Error('Load error')));

    component.ngOnInit();

    expect(consoleSpy).toHaveBeenCalledWith('Error loading history:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('should handle error when saving search', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
    searchServiceMock.saveSearch.mockReturnValue(throwError(() => new Error('Save error')));
    component['queryControl'].setValue('valid query');

    component['onSearch']();

    expect(consoleSpy).toHaveBeenCalledWith('Error saving search:', expect.any(Error));
    consoleSpy.mockRestore();
  });
});
