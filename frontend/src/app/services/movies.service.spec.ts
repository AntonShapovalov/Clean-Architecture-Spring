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

  it('should have null initial error', () => {
    expect(service.error()).toBeNull();
  });

  it('returns empty array observable and clears error when searchId is null or undefined', () => {
    service.error.set('Previous error');

    let resultNull: Movie[] | undefined;
    service.getMovies(null).subscribe((data) => (resultNull = data));

    let resultUndefined: Movie[] | undefined;
    service.getMovies(undefined).subscribe((data) => (resultUndefined = data));

    expect(apiServiceMock.getMovies).not.toHaveBeenCalled();
    expect(resultNull).toEqual([]);
    expect(resultUndefined).toEqual([]);
    expect(service.error()).toBeNull();
  });

  it('returns empty array observable and clears error when searchId is non-positive', () => {
    service.error.set('Previous error');

    let resultZero: Movie[] | undefined;
    service.getMovies(0).subscribe((data) => (resultZero = data));

    let resultNegative: Movie[] | undefined;
    service.getMovies(-1).subscribe((data) => (resultNegative = data));

    expect(apiServiceMock.getMovies).not.toHaveBeenCalled();
    expect(resultZero).toEqual([]);
    expect(resultNegative).toEqual([]);
    expect(service.error()).toBeNull();
  });

  it('calls apiService.getMovies when searchId is provided', () => {
    apiServiceMock.getMovies.mockReturnValue(of(movies));

    let result: Movie[] | undefined;
    service.getMovies(1).subscribe((data) => (result = data));

    expect(apiServiceMock.getMovies).toHaveBeenCalledWith(1);
    expect(result).toEqual(movies);
  });

  it('propagates request errors from apiService and updates error signal', () => {
    const error = new Error('Load error');
    const errorHandler = vi.fn();
    apiServiceMock.getMovies.mockReturnValue(throwError(() => error));

    service.getMovies(1).subscribe({error: errorHandler});

    expect(errorHandler).toHaveBeenCalledWith(expect.objectContaining({message: 'Load error'}));
    expect(service.error()).toBe('Load error');
  });

  it('clears error signal on successful getMovies', () => {
    service.error.set('Previous error');
    apiServiceMock.getMovies.mockReturnValue(of(movies));

    service.getMovies(1).subscribe();

    expect(service.error()).toBeNull();
  });
});
