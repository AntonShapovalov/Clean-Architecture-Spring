import {ChangeDetectionStrategy, Component, computed, inject} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {NgOptimizedImage} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {SearchService} from '../../services/search.service';
import {MoviesService} from '../../services/movies.service';
import {Movie} from '../../models/movie.model';

@Component({
  selector: 'app-movies-list',
  imports: [MatCardModule, NgOptimizedImage],
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
}
