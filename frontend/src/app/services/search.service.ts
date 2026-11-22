import {inject, Injectable, signal} from '@angular/core';
import {ApiService} from './api.service';
import {Search, SearchQuery} from '../models/search.model';
import {catchError, map, Observable, switchMap, tap, throwError} from 'rxjs';
import {extractErrorMessage} from '../utils/api.utils';

/**
 * State management service coordinating search operations and search history.
 *
 * Maintains reactive signals for the search history list, current active search selection,
 * and operation error messages. Provides methods to load, create, and update search queries.
 */
@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly apiService = inject(ApiService);

  /** Reactive signal holding the full list of search history entries. */
  readonly history = signal<Search[]>([]);

  /** Reactive signal holding the currently active search selection. */
  readonly recentSearch = signal<Search | null>(null);

  /** Reactive signal holding the latest search error message, or `null` if none. */
  readonly error = signal<string | null>(null);

  /**
   * Returns history entries matching a case-insensitive query substring.
   *
   * @param filter The query text used to filter history.
   */
  getFilteredHistory(filter: string): Search[] {
    const normalizedFilter = filter.trim().toLowerCase();
    const history = this.history();
    if (!normalizedFilter) {
      return history;
    }
    return history.filter((item) => item.query.toLowerCase().includes(normalizedFilter));
  }

  /**
   * Loads search history from the backend, updates {@link history}, and sets the most recent search.
   *
   * @returns An `Observable` emitting `true` when the history loads successfully.
   */
  loadHistory(): Observable<boolean> {
    return this.apiService.getSearchHistory().pipe(
      tap((history) => {
        this.history.set(history);
        this.recentSearch.set(history[0] ?? null);
      }),
      map(() => true),
      tap({error: (error: unknown) => console.error('Error loading history:', error)})
    );
  }

  /**
   * Saves a new search query to the backend, clears any prior error, and reloads history.
   *
   * @param query The search query to save.
   * @returns An `Observable` emitting `true` when the search is saved and history reloads.
   */
  saveSearch(query: SearchQuery): Observable<boolean> {
    return this.apiService.saveSearch(query).pipe(
      catchError((error: unknown) => {
        const message = extractErrorMessage(error);
        this.error.set(message);
        console.error('Error saving search:', error);
        return throwError(() => new Error(message));
      }),
      tap(() => this.error.set(null)),
      switchMap(() => this.loadHistory())
    );
  }

  /**
   * Re-submits an existing search query to refresh its timestamp, updates local history state,
   * and sets it as the active recent search.
   *
   * @param search The search record to update.
   * @returns An `Observable` emitting `true` when the selected search is updated.
   */
  updateSearch(search: Search): Observable<boolean> {
    return this.apiService.saveSearch({query: search.query}).pipe(
      catchError((error: unknown) => {
        const message = extractErrorMessage(error);
        this.error.set(message);
        console.error('Error updating search:', error);
        return throwError(() => new Error(message));
      }),
      tap(() => {
        this.error.set(null);
        this.updateSearchHistory(search);
      }),
      map(() => true)
    );
  }

  /**
   * Optimistically updates the lastSeenAt timestamp for a search in the local history state
   * and marks it as the currently active search.
   *
   * @param search The search record to update.
   */
  private updateSearchHistory(search: Search): void {
    const updatedSearch = {...search, lastSeenAt: new Date().toISOString(),};
    this.history.update((history) =>
      history.map((item) => (item.id === search.id ? updatedSearch : item))
    );
    this.recentSearch.set(updatedSearch);
  }
}
