import {inject, Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {Movie} from '../models/movie.model';
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
    return this.apiService.getMovies(searchId);
  }
}
