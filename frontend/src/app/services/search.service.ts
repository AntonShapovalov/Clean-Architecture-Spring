import { computed, inject, Injectable, signal } from '@angular/core';
import { ApiService } from './api.service';
import { Search, SearchQuery } from '../models/search.model';
import { Observable, switchMap, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly apiService = inject(ApiService);

  readonly history = signal<Search[]>([]);
  readonly recentSearch = computed(() => this.history()[0] ?? null);

  loadHistory(): Observable<Search[]> {
    return this.apiService.getSearchHistory().pipe(
      tap((history) => this.history.set(history))
    );
  }

  saveSearch(query: SearchQuery): Observable<Search[]> {
    return this.apiService.saveSearch(query).pipe(
      switchMap(() => this.loadHistory())
    );
  }
}
