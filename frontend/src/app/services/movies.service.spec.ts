import {TestBed} from '@angular/core/testing';
import {beforeEach, describe, expect, it, Mock, vi} from 'vitest';
import {of, throwError} from 'rxjs';
import {Movie} from '../models/movie.model';
import {ApiService} from './api.service';
import {MoviesService} from './movies.service';

describe('MoviesService', () => {
  let service: MoviesService;
  let apiServiceMock: { getMovies: Mock };

  const movies: Movie[] = [
    {id: 10, title: 'Inception', year: '2010', imdbId: 'tt1375666', type: 'movie', poster: 'poster.jpg'}
  ];

  beforeEach(() => {
    apiServiceMock = {getMovies: vi.fn()};

    TestBed.configureTestingModule({
      providers: [
        MoviesService,
        {provide: ApiService, useValue: apiServiceMock},
      ]
    });
    service = TestBed.inject(MoviesService);
  });

  it('returns empty array observable when searchId is null or undefined', () => {
    let resultNull: Movie[] | undefined;
    service.getMovies(null).subscribe((data) => (resultNull = data));

    let resultUndefined: Movie[] | undefined;
    service.getMovies(undefined).subscribe((data) => (resultUndefined = data));

    expect(apiServiceMock.getMovies).not.toHaveBeenCalled();
    expect(resultNull).toEqual([]);
    expect(resultUndefined).toEqual([]);
  });

  it('returns empty array observable when searchId is non-positive', () => {
    let resultZero: Movie[] | undefined;
    service.getMovies(0).subscribe((data) => (resultZero = data));

    let resultNegative: Movie[] | undefined;
    service.getMovies(-1).subscribe((data) => (resultNegative = data));

    expect(apiServiceMock.getMovies).not.toHaveBeenCalled();
    expect(resultZero).toEqual([]);
    expect(resultNegative).toEqual([]);
  });

  it('calls apiService.getMovies when searchId is provided', () => {
    apiServiceMock.getMovies.mockReturnValue(of(movies));

    let result: Movie[] | undefined;
    service.getMovies(1).subscribe((data) => (result = data));

    expect(apiServiceMock.getMovies).toHaveBeenCalledWith(1);
    expect(result).toEqual(movies);
  });

  it('propagates request errors from apiService', () => {
    const error = new Error('Load error');
    const errorHandler = vi.fn();
    apiServiceMock.getMovies.mockReturnValue(throwError(() => error));

    service.getMovies(1).subscribe({error: errorHandler});

    expect(errorHandler).toHaveBeenCalledWith(expect.objectContaining({message: 'Load error'}));
  });
});
