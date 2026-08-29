import {inject, Injectable, signal} from '@angular/core';
import {catchError, Observable, of, tap, throwError} from 'rxjs';
import {Movie} from '../models/movie.model';
import {extractErrorMessage} from '../utils/api.utils';
import {ApiService} from './api.service';

/**
 * Domain service managing movie retrieval and error state.
 *
 * Wraps {@link ApiService} calls, validates input search identifiers, captures and extracts
 * user-friendly error messages into a reactive signal, and emits movie collections.
 */
@Injectable({
  providedIn: 'root',
})
export class MoviesService {
  private readonly apiService = inject(ApiService);

  /** Reactive signal holding the latest error message, or `null` if no error exists. */
  readonly error = signal<string | null>(null);

  /**
   * Retrieves movies for the given search identifier.
   *
   * Returns an empty list immediately when `searchId` is `null`, `undefined`, or `<= 0`.
   * On failure, extracts a formatted error message, updates the {@link error} signal, and re-throws.
   *
   * @param searchId Identifier of the search query whose movies are to be retrieved.
   * @returns An `Observable` emitting an array of {@link Movie} items.
   */
  getMovies(searchId: number | null | undefined): Observable<Movie[]> {
    if (searchId == null || searchId <= 0) {
      this.error.set(null);
      return of<Movie[]>([]);
    }
    return this.apiService.getMovies(searchId).pipe(
      catchError((error: unknown) => {
        const message = extractErrorMessage(error);
        this.error.set(message);
        return throwError(() => new Error(message));
      }),
      tap(() => this.error.set(null))
    );
  }
}
