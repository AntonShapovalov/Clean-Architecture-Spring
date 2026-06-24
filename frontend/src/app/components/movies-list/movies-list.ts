import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { SearchService } from '../../services/search-service';

@Component({
  selector: 'app-movies-list',
  imports: [],
  templateUrl: './movies-list.html',
  styleUrl: './movies-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MoviesList {
  private readonly searchService = inject(SearchService);
  protected readonly title = computed(() => {
    const search = this.searchService.recentSearch();
    return search ? `Search results for “${search.query}”` : '';
  });
}
