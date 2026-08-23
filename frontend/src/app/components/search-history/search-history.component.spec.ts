import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {SearchHistoryComponent} from './search-history.component';
import {SearchService} from '../../services/search.service';
import {Search} from '../../models/search.model';
import {beforeEach, describe, expect, it, Mock, vi} from 'vitest';
import {of, Subject, throwError} from 'rxjs';
import {signal, Signal} from '@angular/core';

describe('SearchHistoryComponent', () => {
  let component: SearchHistoryComponent;
  let fixture: ComponentFixture<SearchHistoryComponent>;
  let searchServiceMock: {
    loadHistory: Mock;
    saveSearch: Mock;
    history: Signal<Search[]>;
  };

  const mockHistory: Search[] = [
    {id: 1, query: 'test 1', updatedDate: '2023-01-01', lastSeenAt: '', isExpired: false},
    {id: 2, query: 'test 2', updatedDate: '2023-01-02', lastSeenAt: '', isExpired: false}
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
        {provide: SearchService, useValue: searchServiceMock}
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

  it('should call saveSearch on valid search and reset search field on success', () => {
    const newQuery = 'new search';
    component['queryControl'].setValue(newQuery);

    component['onSearch']();

    expect(searchServiceMock.saveSearch).toHaveBeenCalledWith({query: newQuery});
    expect(component['queryControl'].value).toBe('');
  });

  it('should reset search field on receiving data inside subscription', () => {
    const saveSubject = new Subject<Search[]>();
    searchServiceMock.saveSearch.mockReturnValue(saveSubject.asObservable());

    component['queryControl'].setValue('pending query');
    component['onSearch']();

    expect(searchServiceMock.saveSearch).toHaveBeenCalledWith({query: 'pending query'});
    expect(component['queryControl'].value).toBe('pending query');

    saveSubject.next(mockHistory);
    expect(component['queryControl'].value).toBe('');
  });

  it('should handle error when loading history', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
    searchServiceMock.loadHistory.mockReturnValue(throwError(() => new Error('Load error')));

    component.ngOnInit();

    expect(consoleSpy).toHaveBeenCalledWith('Error loading history:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('should handle error when saving search without resetting search field', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(vi.fn());
    searchServiceMock.saveSearch.mockReturnValue(throwError(() => new Error('Save error')));
    component['queryControl'].setValue('valid query');

    component['onSearch']();

    expect(consoleSpy).toHaveBeenCalledWith('Error saving search:', expect.any(Error));
    expect(component['queryControl'].value).toBe('valid query');
    consoleSpy.mockRestore();
  });

  it('should filter history list when typing in search field', () => {
    component['queryControl'].setValue('test 1');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.history-list li');
    expect(items.length).toBe(1);
    expect(items[0].textContent).toContain('test 1');
  });

  it('should filter history list case-insensitively', () => {
    component['queryControl'].setValue('TEST 2');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.history-list li');
    expect(items.length).toBe(1);
    expect(items[0].textContent).toContain('test 2');
  });

  it('should display all items when search field is cleared', () => {
    component['queryControl'].setValue('test 1');
    fixture.detectChanges();

    component['queryControl'].setValue('');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.history-list li');
    expect(items.length).toBe(2);
  });

  it('should not display clear button when search field is empty', () => {
    component['queryControl'].setValue('');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const clearBtn = compiled.querySelector('button[aria-label="Clear search"]');
    expect(clearBtn).toBeNull();
  });

  it('should display clear button when search field has text', () => {
    component['queryControl'].setValue('test');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const clearBtn = compiled.querySelector('button[aria-label="Clear search"]');
    expect(clearBtn).not.toBeNull();
  });

  it('should reset search field and filter when clicking clear button', () => {
    component['queryControl'].setValue('test 1');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelectorAll('.history-list li').length).toBe(1);

    const clearBtn = compiled.querySelector('button[aria-label="Clear search"]') as HTMLButtonElement;
    clearBtn.click();
    fixture.detectChanges();

    expect(component['queryControl'].value).toBe('');
    expect(compiled.querySelectorAll('.history-list li').length).toBe(2);
  });

  it('should set search field text and call search with selected query on clicking history item', () => {
    const saveSubject = new Subject<Search[]>();
    searchServiceMock.saveSearch.mockReturnValue(saveSubject.asObservable());

    const compiled = fixture.nativeElement as HTMLElement;
    const firstItem = compiled.querySelector('.history-list li') as HTMLElement;
    firstItem.click();
    fixture.detectChanges();

    expect(component['queryControl'].value).toBe('test 1');
    expect(searchServiceMock.saveSearch).toHaveBeenCalledWith({query: 'test 1'});

    saveSubject.next(mockHistory);
    expect(component['queryControl'].value).toBe('');
  });

  it('should set search field text and call search with selected query on Enter keydown on history item', () => {
    const saveSubject = new Subject<Search[]>();
    searchServiceMock.saveSearch.mockReturnValue(saveSubject.asObservable());

    const compiled = fixture.nativeElement as HTMLElement;
    const secondItem = compiled.querySelectorAll('.history-list li')[1] as HTMLElement;
    secondItem.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter' }));
    fixture.detectChanges();

    expect(component['queryControl'].value).toBe('test 2');
    expect(searchServiceMock.saveSearch).toHaveBeenCalledWith({query: 'test 2'});

    saveSubject.next(mockHistory);
    expect(component['queryControl'].value).toBe('');
  });
});
