import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Search, SearchQuery} from '../models/search.model';
import {Movie} from '../models/movie.model';

/**
 * Low-level HTTP client service interfacing with backend REST API endpoints.
 *
 * Provides raw HTTP request methods for persisting searches, retrieving search history,
 * and fetching movie collections for specific searches.
 */
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly http = inject(HttpClient);

  /**
   * Submits a new search query to be executed and stored on the backend.
   *
   * @param query The search query payload containing the query string.
   * @returns An `Observable<void>` completing when the search is saved.
   */
  saveSearch(query: SearchQuery): Observable<void> {
    return this.http.post<void>('/api/search', query);
  }

  /**
   * Fetches all recorded movie search history entries from the backend.
   *
   * @returns An `Observable` emitting an array of {@link Search} history items.
   */
  getSearchHistory(): Observable<Search[]> {
    return this.http.get<Search[]>('/api/search/history');
  }

  /**
   * Fetches movie results associated with a specific search query ID.
   *
   * @param searchId Identifier of the search record.
   * @returns An `Observable` emitting an array of {@link Movie} objects found for the search.
   */
  getMovies(searchId: number): Observable<Movie[]> {
    return this.http.get<Movie[]>(`/api/search/${searchId}/movies`);
  }
}
