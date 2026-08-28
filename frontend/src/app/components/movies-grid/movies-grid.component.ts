import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {Movie} from '../../models/movie.model';
import {isValidUrl} from '../../utils/url.utils';

@Component({
  selector: 'app-movies-grid',
  imports: [MatCardModule, MatIconModule, NgOptimizedImage],
  templateUrl: './movies-grid.component.html',
  styleUrl: './movies-grid.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MoviesGridComponent {
  readonly movies = input.required<Movie[]>();
  readonly loading = input(false);

  protected readonly isValidUrl = isValidUrl;
}
