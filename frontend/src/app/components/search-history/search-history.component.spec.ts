import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {SearchHistoryComponent} from './search-history.component';
import {SearchService} from '../../services/search.service';
import {Search} from '../../models/search.model';
import {beforeEach, describe, expect, it, Mock, vi} from 'vitest';
import {Observable, of, Subject, throwError} from 'rxjs';
import {signal, WritableSignal} from '@angular/core';

describe('SearchHistoryComponent', () => {
  let component: SearchHistoryComponent;
  let fixture: ComponentFixture<SearchHistoryComponent>;
  let searchServiceMock: {
    loadHistory: Mock;
    saveSearch: Mock;
    updateSearch: Mock;
    getFilteredHistory: Mock;
    history: WritableSignal<Search[]>;
  };

  const mockHistory: Search[] = [
    {id: 1, query: 'test 1', updatedDate: '2023-01-01', lastSeenAt: '', isExpired: false},
    {id: 2, query: 'test 2', updatedDate: '2023-01-02', lastSeenAt: '', isExpired: false}
  ];

  const createComponent = () => {
    fixture = TestBed.createComponent(SearchHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    searchServiceMock = {
      loadHistory: vi.fn().mockReturnValue(of(true)),
      saveSearch: vi.fn().mockReturnValue(of(true)),
      updateSearch: vi.fn().mockReturnValue(of(true)),
      getFilteredHistory: vi.fn(() => searchServiceMock.history()),
      history: signal(mockHistory)
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SearchHistoryComponent],
      providers: [
        {provide: SearchService, useValue: searchServiceMock}
      ]
    }).compileComponents();

    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load history through the resource', () => {
    expect(searchServiceMock.loadHistory).toHaveBeenCalled();
    expect(component['historyResource'].value()).toBe(true);
  });

  it('should display history items in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const historyList = compiled.querySelector('.history-list');
    expect(historyList?.tagName.toLowerCase()).toBe('mat-action-list');
    expect(historyList?.getAttribute('aria-labelledby')).toBe('history-heading');
    expect(compiled.querySelector('#history-heading')?.textContent).toContain('Recent Searches');

    const items = compiled.querySelectorAll('.history-list [mat-list-item]');
    expect(items.length).toBe(2);
    expect(items[0].textContent).toContain('test 1');
    expect(items[1].textContent).toContain('test 2');
  });

  it('should display the search date in the Material list item meta', () => {
    searchServiceMock.getFilteredHistory.mockReturnValue([
      {...mockHistory[0], lastSeenAt: '2023-01-01T12:00:00Z'}
    ]);
    component['queryControl'].setValue('dated');
    fixture.detectChanges();

    const date = fixture.nativeElement.querySelector('.history-list [matListItemMeta]') as HTMLElement;
    expect(date.textContent?.trim()).not.toBe('');
  });

  it('should show an empty message outside the action list when there is no history', () => {
    searchServiceMock.getFilteredHistory.mockReturnValue([]);
    component['queryControl'].setValue('missing');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-action-list')).toBeNull();
    expect(compiled.querySelector('p')?.textContent).toContain('No recent searches');
    expect(compiled.querySelector('button[mat-list-item]')).toBeNull();
  });

  it('should not call saveSearch if query is invalid (too short)', () => {
    component['queryControl'].setValue('ab');
    component['onSearch']();
    expect(searchServiceMock.saveSearch).not.toHaveBeenCalled();
  });

  it('should reset the search field after a successful resource save', async () => {
    const saveSubject = new Subject<boolean>();
    const newQuery = 'new search';
    searchServiceMock.saveSearch.mockReturnValue(saveSubject.asObservable());
    component['queryControl'].setValue(newQuery);

    component['onSearch']();
    fixture.detectChanges();

    expect(searchServiceMock.saveSearch).toHaveBeenCalledWith({query: newQuery});
    expect(component['queryControl'].value).toBe(newQuery);
    expect(component['isActionPending']()).toBe(true);
    expect(fixture.nativeElement.querySelector('button[type="submit"]')?.disabled).toBe(true);

    saveSubject.next(true);
    saveSubject.complete();
    await fixture.whenStable();
    fixture.detectChanges();
    expect(component['queryControl'].value).toBe('');
    expect(component['isActionPending']()).toBe(false);
  });

  it('should not reset search field if component is destroyed before save completes', () => {
    const saveSubject = new Subject<boolean>();
    searchServiceMock.saveSearch.mockReturnValue(saveSubject.asObservable());

    component['queryControl'].setValue('pending query');
    component['onSearch']();
    fixture.detectChanges();

    fixture.destroy();

    saveSubject.next(true);
    expect(component['queryControl'].value).toBe('pending query');
  });

  it('should expose history load errors through the resource', () => {
    fixture.destroy();
    const error = new Error('Load error');
    searchServiceMock.loadHistory.mockReturnValue(throwError(() => error));

    createComponent();

    expect(component['historyResource'].error()).toBe(error);
  });

  it('should cancel a pending history load when the component is destroyed', () => {
    const teardown = vi.fn();
    fixture.destroy();
    searchServiceMock.loadHistory.mockReturnValue(new Observable<boolean>(() => teardown));

    createComponent();

    expect(component['historyResource'].isLoading()).toBe(true);
    fixture.destroy();
    expect(teardown).toHaveBeenCalledOnce();
  });

  it('should prevent another mutation while a save is pending', () => {
    const saveSubject = new Subject<boolean>();
    searchServiceMock.saveSearch.mockReturnValue(saveSubject.asObservable());
    component['queryControl'].setValue('valid query');

    component['onSearch']();
    component['onSearch']();
    component['onSelect'](mockHistory[0]);
    fixture.detectChanges();

    expect(searchServiceMock.saveSearch).toHaveBeenCalledOnce();
    expect(searchServiceMock.updateSearch).not.toHaveBeenCalled();
    expect(component['isActionPending']()).toBe(true);
  });

  it('should prevent another mutation while an update is pending, even before change detection', () => {
    const updateSubject = new Subject<boolean>();
    searchServiceMock.updateSearch.mockReturnValue(updateSubject.asObservable());
    component['queryControl'].setValue('valid query');

    const compiled = fixture.nativeElement as HTMLElement;
    const historyItems = compiled.querySelectorAll<HTMLButtonElement>('.history-list button[mat-list-item]');
    historyItems[0].focus();
    historyItems[0].click();
    fixture.detectChanges();
    historyItems[1].click();
    component['onSearch']();

    expect(searchServiceMock.updateSearch).toHaveBeenCalledOnce();
    expect(searchServiceMock.saveSearch).not.toHaveBeenCalled();
    expect(component['isActionPending']()).toBe(true);
    expect(fixture.nativeElement.querySelector('button[type="submit"]')?.disabled).toBe(true);
    expect(historyItems[0].disabled).toBe(false);
    expect(historyItems[0].getAttribute('aria-disabled')).toBe('true');
    expect(document.activeElement).toBe(historyItems[0]);
  });

  it('should expose save errors through the resource and allow retry without resetting the input', () => {
    const error = new Error('Save error');
    searchServiceMock.saveSearch.mockReturnValue(throwError(() => error));
    component['queryControl'].setValue('valid query');

    component['onSearch']();
    fixture.detectChanges();

    expect(component['saveResource'].error()).toBe(error);
    expect(component['queryControl'].value).toBe('valid query');
    expect(component['isActionPending']()).toBe(false);

    searchServiceMock.saveSearch.mockReturnValue(of(true));
    component['onSearch']();
    fixture.detectChanges();

    expect(component['queryControl'].value).toBe('');
  });

  it('should pass the filter to the service and display its results', () => {
    searchServiceMock.getFilteredHistory.mockReturnValue([mockHistory[1]]);
    component['queryControl'].setValue(' TEST ');
    fixture.detectChanges();

    expect(searchServiceMock.getFilteredHistory).toHaveBeenLastCalledWith(' TEST ');
    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.history-list [mat-list-item]');
    expect(items.length).toBe(1);
    expect(items[0].textContent).toContain('test 2');
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
    searchServiceMock.getFilteredHistory.mockReturnValueOnce([mockHistory[0]]);
    component['queryControl'].setValue('test 1');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelectorAll('.history-list [mat-list-item]').length).toBe(1);

    const clearBtn = compiled.querySelector('button[aria-label="Clear search"]') as HTMLButtonElement;
    clearBtn.click();
    fixture.detectChanges();

    expect(component['queryControl'].value).toBe('');
    expect(searchServiceMock.getFilteredHistory).toHaveBeenLastCalledWith('');
    expect(compiled.querySelectorAll('.history-list [mat-list-item]').length).toBe(2);
  });

  it('should update a selected search through the resource without clearing the input', async () => {
    const updateSubject = new Subject<boolean>();
    searchServiceMock.updateSearch.mockReturnValue(updateSubject.asObservable());
    component['queryControl'].setValue('typed query');

    const compiled = fixture.nativeElement as HTMLElement;
    const firstItem = compiled.querySelector('.history-list button[mat-list-item]') as HTMLButtonElement;
    firstItem.focus();
    firstItem.click();
    fixture.detectChanges();

    expect(searchServiceMock.updateSearch).toHaveBeenCalledWith(mockHistory[0]);
    expect(component['isActionPending']()).toBe(true);
    expect(document.activeElement).toBe(firstItem);

    updateSubject.next(true);
    updateSubject.complete();
    await fixture.whenStable();
    fixture.detectChanges();
    expect(component['queryControl'].value).toBe('typed query');
    expect(component['isActionPending']()).toBe(false);
    expect(document.activeElement).toBe(firstItem);
  });

  it('should allow an update retry after its resource reports an error', () => {
    const error = new Error('Update error');
    searchServiceMock.updateSearch.mockReturnValue(throwError(() => error));

    const compiled = fixture.nativeElement as HTMLElement;
    const firstItem = compiled.querySelector('.history-list button[mat-list-item]') as HTMLButtonElement;
    firstItem.focus();
    firstItem.click();
    fixture.detectChanges();

    expect(component['updateResource'].error()).toBe(error);
    expect(component['isActionPending']()).toBe(false);
    expect(document.activeElement).toBe(firstItem);

    searchServiceMock.updateSearch.mockReturnValue(of(true));
    firstItem.click();
    fixture.detectChanges();

    expect(searchServiceMock.updateSearch).toHaveBeenCalledTimes(2);
    expect(component['updateResource'].value()).toBe(true);
    expect(component['isActionPending']()).toBe(false);
    expect(document.activeElement).toBe(firstItem);
  });

  it('should allow switching from a completed save to an update', async () => {
    component['queryControl'].setValue('saved query');
    component['onSearch']();
    fixture.detectChanges();

    expect(component['queryControl'].value).toBe('');
    expect(component['saveResource'].value()).toBe(true);

    const updateSubject = new Subject<boolean>();
    searchServiceMock.updateSearch.mockReturnValue(updateSubject.asObservable());
    component['onSelect'](mockHistory[0]);
    fixture.detectChanges();

    expect(searchServiceMock.updateSearch).toHaveBeenCalledWith(mockHistory[0]);
    expect(component['isActionPending']()).toBe(true);

    updateSubject.next(true);
    updateSubject.complete();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component['isActionPending']()).toBe(false);
  });

  it('should cancel a pending update when the component is destroyed', () => {
    const teardown = vi.fn();
    searchServiceMock.updateSearch.mockReturnValue(new Observable<boolean>(() => teardown));

    const compiled = fixture.nativeElement as HTMLElement;
    const firstItem = compiled.querySelector('.history-list button[mat-list-item]') as HTMLButtonElement;
    firstItem.click();
    fixture.detectChanges();
    expect(component['isActionPending']()).toBe(true);

    fixture.destroy();

    expect(teardown).toHaveBeenCalledOnce();
  });

  it('should not reset search field if component is destroyed before updateSearch completes', () => {
    const updateSubject = new Subject<boolean>();
    searchServiceMock.updateSearch.mockReturnValue(updateSubject.asObservable());

    const compiled = fixture.nativeElement as HTMLElement;
    const firstItem = compiled.querySelector('.history-list button[mat-list-item]') as HTMLButtonElement;
    firstItem.click();
    fixture.detectChanges();

    component['queryControl'].setValue('dirty query');
    fixture.destroy();

    updateSubject.next(true);
    expect(component['queryControl'].value).toBe('dirty query');
  });

  it('should expose update errors through the resource', () => {
    const error = new Error('Update error');
    searchServiceMock.updateSearch.mockReturnValue(throwError(() => error));

    const compiled = fixture.nativeElement as HTMLElement;
    const firstItem = compiled.querySelector('.history-list button[mat-list-item]') as HTMLButtonElement;
    firstItem.click();
    fixture.detectChanges();

    expect(component['updateResource'].error()).toBe(error);
    expect(component['isActionPending']()).toBe(false);
  });
});
