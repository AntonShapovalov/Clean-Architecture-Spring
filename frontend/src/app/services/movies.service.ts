import {inject, Injectable, signal} from '@angular/core';
import {catchError, Observable, of, tap, throwError} from 'rxjs';
import {Movie} from '../models/movie.model';
import {extractErrorMessage} from '../utils/api.utils';
import {ApiService} from './api.service';

@Injectable({
  providedIn: 'root',
})
export class MoviesService {
  private readonly apiService = inject(ApiService);

  readonly error = signal<string | null>(null);

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
