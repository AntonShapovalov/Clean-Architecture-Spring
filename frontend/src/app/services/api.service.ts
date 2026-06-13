import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Search, SearchQuery } from '../models/search.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly http = inject(HttpClient);

  saveSearch(query: SearchQuery): Observable<void> {
    return this.http.post<void>('/api/search', query);
  }

  getSearchHistory(): Observable<Search[]> {
    return this.http.get<Search[]>('/api/search/history');
  }
}
