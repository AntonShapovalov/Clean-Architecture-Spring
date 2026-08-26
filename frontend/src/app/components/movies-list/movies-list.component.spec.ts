import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MoviesListComponent} from './movies-list.component';
import {SearchService} from '../../services/search.service';
import {MoviesService} from '../../services/movies.service';
import {Search} from '../../models/search.model';
import {Movie} from '../../models/movie.model';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {signal, WritableSignal} from '@angular/core';
import {of, Subject, throwError} from 'rxjs';

describe('MoviesListComponent', () => {
  let component: MoviesListComponent;
  let fixture: ComponentFixture<MoviesListComponent>;
  let mockSearchService: {
    recentSearch: WritableSignal<Search | null>;
    error: WritableSignal<string | null>;
  };
  let mockMoviesService: {
    getMovies: ReturnType<typeof vi.fn>;
    error: WritableSignal<string | null>;
  };
  let recentSearchSignal: WritableSignal<Search | null>;
  let searchErrorSignal: WritableSignal<string | null>;
  let moviesErrorSignal: WritableSignal<string | null>;

  const mockMovies: Movie[] = [
    {id: 1, title: 'Inception', year: '2010', imdbId: 'tt1375666', type: 'movie', poster: 'poster.jpg'}
  ];

  beforeEach(async () => {
    recentSearchSignal = signal<Search | null>(null);
    searchErrorSignal = signal<string | null>(null);
    moviesErrorSignal = signal<string | null>(null);
    mockSearchService = {
      recentSearch: recentSearchSignal,
      error: searchErrorSignal,
    };
    mockMoviesService = {
      getMovies: vi.fn().mockReturnValue(of([])),
      error: moviesErrorSignal,
    };

    await TestBed.configureTestingModule({
      imports: [MoviesListComponent],
      providers: [
        {provide: SearchService, useValue: mockSearchService},
        {provide: MoviesService, useValue: mockMoviesService}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MoviesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render h2 when title is empty', () => {
    const heading = fixture.nativeElement.querySelector('h2');
    expect(heading).toBeNull();
  });

  it('should update title when recent search is received', () => {
    const mockSearch: Search = {
      id: 1,
      query: 'Inception',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    };

    recentSearchSignal.set(mockSearch);
    fixture.detectChanges();

    expect(component['title']()).toBe('Search results for “Inception”');
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Search results for “Inception”');
    const heading = fixture.nativeElement.querySelector('h2');
    expect(heading).not.toBeNull();
    expect(heading?.textContent).toBe('Search results for “Inception”');
  });

  it('should render empty placeholder when no movies are returned', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('No movies found');
  });

  it('should not render empty placeholder during movies loading', async () => {
    const moviesSubject = new Subject<Movie[]>();
    mockMoviesService.getMovies.mockReturnValue(moviesSubject);

    recentSearchSignal.set({
      id: 2,
      query: 'Matrix',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).not.toContain('No movies found');

    moviesSubject.next([]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(compiled.textContent).toContain('No movies found');
  });

  it('should render a card for each movie', () => {
    mockMoviesService.getMovies.mockReturnValue(of(mockMovies));

    recentSearchSignal.set({
      id: 1,
      query: 'Inception',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.movie-card') as HTMLElement;
    const image = card.querySelector('img') as HTMLImageElement;
    expect(card.textContent).toContain('Inception');
    expect(card.textContent).toContain('2010');
    expect(card.textContent).toContain('movie');
    expect(image.getAttribute('src')).toBe('poster.jpg');
    expect(image.getAttribute('alt')).toBe('Inception poster');
  });

  it('should render error message and keep title when moviesService fails', async () => {
    mockMoviesService.getMovies.mockReturnValue(throwError(() => new Error('Search with the given ID was not found')));
    moviesErrorSignal.set('Search with the given ID was not found');

    recentSearchSignal.set({
      id: 99,
      query: 'Unknown',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const errorElement = compiled.querySelector('.error-message');
    expect(errorElement).not.toBeNull();
    expect(errorElement?.textContent).toContain('Search with the given ID was not found');
    expect(compiled.querySelector('h2')).not.toBeNull();
    expect(compiled.querySelector('h2')?.textContent).toContain('Search results for “Unknown”');
    expect(compiled.querySelector('.movies-grid')).toBeNull();
    expect(compiled.textContent).not.toContain('No movies found');
  });

  it('should render error message instead of title and movie list when searchService fails', async () => {
    recentSearchSignal.set({
      id: 1,
      query: 'Inception',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    searchErrorSignal.set('Invalid search query');
    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const errorElement = compiled.querySelector('.error-message');
    expect(errorElement).not.toBeNull();
    expect(errorElement?.textContent).toContain('Invalid search query');
    expect(compiled.querySelector('h2')).toBeNull();
    expect(compiled.querySelector('.movies-grid')).toBeNull();
    expect(compiled.textContent).not.toContain('No movies found');
  });

  it('should clear error message and display movies when movie error is resolved on subsequent search', async () => {
    mockMoviesService.getMovies.mockReturnValue(throwError(() => new Error('Search with the given ID was not found')));
    moviesErrorSignal.set('Search with the given ID was not found');
    recentSearchSignal.set({
      id: 99,
      query: 'Unknown',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.error-message')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('h2')).not.toBeNull();

    moviesErrorSignal.set(null);
    mockMoviesService.getMovies.mockReturnValue(of(mockMovies));
    recentSearchSignal.set({
      id: 1,
      query: 'Inception',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.error-message')).toBeNull();
    expect(fixture.nativeElement.querySelector('h2')?.textContent).toContain('Search results for “Inception”');
    expect(fixture.nativeElement.querySelector('.movie-card')).not.toBeNull();
  });

  it('should clear error message and display title and movies when search succeeds after failure', async () => {
    searchErrorSignal.set('Invalid search query');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.error-message')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('h2')).toBeNull();

    searchErrorSignal.set(null);
    mockMoviesService.getMovies.mockReturnValue(of(mockMovies));
    recentSearchSignal.set({
      id: 1,
      query: 'Inception',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.error-message')).toBeNull();
    expect(fixture.nativeElement.querySelector('h2')?.textContent).toContain('Search results for “Inception”');
    expect(fixture.nativeElement.querySelector('.movie-card')).not.toBeNull();
  });
});
