import {ChangeDetectionStrategy, Component, computed, inject} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MoviesGridComponent} from '../movies-grid/movies-grid.component';
import {SearchService} from '../../services/search.service';
import {MoviesService} from '../../services/movies.service';
import {Movie} from '../../models/movie.model';
import {delayedSignal} from '../../utils/signal.utils';

/**
 * Container component that coordinates movie search results and loading states.
 *
 * Listens to active search selection from {@link SearchService}, fetches associated movies
 * via {@link MoviesService} using `rxResource`, manages delayed loading spinner state to prevent UI
 * flickering, and propagates error and title information to the template.
 */
@Component({
  selector: 'app-movies-container',
  imports: [MatProgressSpinnerModule, MoviesGridComponent],
  templateUrl: './movies-container.component.html',
  styleUrl: './movies-container.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MoviesContainerComponent {
  private readonly searchService = inject(SearchService);
  private readonly moviesService = inject(MoviesService);

  /** Computed header title reflecting the currently selected search query. */
  protected readonly title = computed(() => {
    const search = this.searchService.recentSearch();
    return search ? `Search results for “${search.query}”` : '';
  });

  /** Reactive resource fetching movies for the active search query ID. */
  protected readonly moviesResource = rxResource({
    params: () => this.searchService.recentSearch()?.id ?? null,
    stream: ({params: searchId}) => this.moviesService.getMovies(searchId),
    defaultValue: [] as Movie[],
  });

  /** Delayed loading signal (500ms debounce) to prevent spinner flickering on fast network responses. */
  protected readonly showLoading = delayedSignal(this.moviesResource.isLoading);

  /** Signal containing the list of fetched movie records. */
  protected readonly movies = this.moviesResource.value;

  /** Signal exposing error messages from search operations. */
  protected readonly searchError = this.searchService.error;

  /** Signal exposing error messages from movie retrieval operations. */
  protected readonly moviesError = this.moviesService.error;
}
