import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {Movie} from '../../models/movie.model';
import {isValidUrl} from '../../utils/url.utils';

/**
 * Presentational component rendering a responsive grid of movie cards.
 *
 * Displays movie posters, titles, release years, and media types with fallback placeholders
 * for missing or invalid poster URLs, and empty state feedback when no movies are found.
 */
@Component({
  selector: 'app-movies-grid',
  imports: [MatCardModule, MatIconModule, NgOptimizedImage],
  templateUrl: './movies-grid.component.html',
  styleUrl: './movies-grid.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MoviesGridComponent {
  /** List of movie items to display in the grid. */
  readonly movies = input.required<Movie[]>();

  /** Indicates whether movies are currently loading to suppress the empty state view. */
  readonly loading = input(false);

  /** Utility reference to validate poster image URLs. */
  protected readonly isValidUrl = isValidUrl;
}
