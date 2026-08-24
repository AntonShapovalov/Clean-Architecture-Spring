import {inject, Injectable, signal} from '@angular/core';
import {ApiService} from './api.service';
import {Search, SearchQuery} from '../models/search.model';
import {map, Observable, switchMap, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly apiService = inject(ApiService);

  readonly history = signal<Search[]>([]);
  readonly recentSearch = signal<Search | null>(null);

  loadHistory(): Observable<Search[]> {
    return this.apiService.getSearchHistory().pipe(
      tap((history) => {
        this.history.set(history);
        this.recentSearch.set(history[0] ?? null);
      })
    );
  }

  saveSearch(query: SearchQuery): Observable<Search[]> {
    return this.apiService.saveSearch(query).pipe(
      switchMap(() => this.loadHistory())
    );
  }

  updateSearch(search: Search): Observable<Search[]> {
    return this.apiService.saveSearch({query: search.query}).pipe(
      tap(() => this.updateSearchHistory(search)),
      map(() => this.history())
    );
  }

  private updateSearchHistory(search: Search): void {
    const updatedSearch = {...search, lastSeenAt: new Date().toISOString(),};
    this.history.update((history) =>
      history.map((item) => (item.id === search.id ? updatedSearch : item))
    );
    this.recentSearch.set(updatedSearch);
  }
}
