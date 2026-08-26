import {inject, Injectable} from '@angular/core';
import {catchError, Observable, of, throwError} from 'rxjs';
import {Movie} from '../models/movie.model';
import {extractErrorMessage} from '../utils/api.utils';
import {ApiService} from './api.service';

@Injectable({
  providedIn: 'root',
})
export class MoviesService {
  private readonly apiService = inject(ApiService);

  getMovies(searchId: number | null | undefined): Observable<Movie[]> {
    if (searchId == null || searchId <= 0) {
      return of<Movie[]>([]);
    }
    return this.apiService.getMovies(searchId).pipe(
      catchError((error: unknown) => {
        const message = extractErrorMessage(error);
        return throwError(() => new Error(message));
      })
    );
  }
}
