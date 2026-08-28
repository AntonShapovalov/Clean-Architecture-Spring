import {ChangeDetectionStrategy, Component, computed, inject} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MoviesGridComponent} from '../movies-grid/movies-grid.component';
import {SearchService} from '../../services/search.service';
import {MoviesService} from '../../services/movies.service';
import {Movie} from '../../models/movie.model';
import {delayedSignal} from '../../utils/signal.utils';

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

  protected readonly title = computed(() => {
    const search = this.searchService.recentSearch();
    return search ? `Search results for “${search.query}”` : '';
  });

  protected readonly moviesResource = rxResource({
    params: () => this.searchService.recentSearch()?.id ?? null,
    stream: ({params: searchId}) => this.moviesService.getMovies(searchId),
    defaultValue: [] as Movie[],
  });

  protected readonly showLoading = delayedSignal(this.moviesResource.isLoading);
  protected readonly movies = this.moviesResource.value;
  protected readonly searchError = this.searchService.error;
  protected readonly moviesError = this.moviesService.error;
}
