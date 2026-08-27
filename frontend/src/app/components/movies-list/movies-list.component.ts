import {ChangeDetectionStrategy, Component, computed, inject} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {NgOptimizedImage} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {SearchService} from '../../services/search.service';
import {MoviesService} from '../../services/movies.service';
import {Movie} from '../../models/movie.model';
import {isValidUrl} from '../../utils/url.utils';

@Component({
  selector: 'app-movies-list',
  imports: [MatCardModule, MatIconModule, NgOptimizedImage],
  templateUrl: './movies-list.component.html',
  styleUrl: './movies-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MoviesListComponent {
  private readonly searchService = inject(SearchService);
  private readonly moviesService = inject(MoviesService);

  protected readonly title = computed(() => {
    const search = this.searchService.recentSearch();
    return search ? `Search results for “${search.query}”` : '';
  });

  protected readonly moviesResource = rxResource({
    params: () => this.searchService.recentSearch()?.id ?? null,
    stream: ({params: searchId}) => this.moviesService.getMovies(searchId),
    defaultValue: [] as Movie[],
  });

  protected readonly movies = this.moviesResource.value;
  protected readonly searchError = this.searchService.error;
  protected readonly moviesError = this.moviesService.error;
  protected readonly isValidUrl = isValidUrl;
}
