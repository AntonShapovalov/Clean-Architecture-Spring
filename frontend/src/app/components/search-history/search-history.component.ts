import {ChangeDetectionStrategy, Component, computed, inject, signal} from '@angular/core';
import {rxResource, toSignal} from '@angular/core/rxjs-interop';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {DatePipe} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {ErrorStateMatcher} from '@angular/material/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {SearchService} from '../../services/search.service';
import {Search, SearchQuery} from '../../models/search.model';
import {isDateToday} from '../../utils/date.utils';
import {tap} from 'rxjs';

/** Custom error state matcher that prevents form fields from displaying error borders while typing. */
class NeverErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(): boolean {
    return false;
  }
}

/**
 * Sidebar component managing movie search query submissions and search history list.
 *
 * Provides real-time filtering of past searches, form validation for search queries,
 * and handles selecting past searches or triggering new ones.
 */
@Component({
  selector: 'app-search-history',
  imports: [
    ReactiveFormsModule,
    DatePipe,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    FormsModule
  ],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchHistoryComponent {
  private readonly searchService = inject(SearchService);

  /** Utility function to check if a date falls on the current calendar day. */
  protected readonly isDateToday = isDateToday;

  /** Error state matcher suppressing error borders during user input. */
  protected readonly neverErrorStateMatcher = new NeverErrorStateMatcher();

  /** Form control holding the search input value with validation constraints (3-29 characters). */
  protected readonly queryControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(3), Validators.maxLength(29)]
  });

  /** Signal reflecting live changes to the search input text. */
  protected readonly queryFilter = toSignal(this.queryControl.valueChanges, {
    initialValue: this.queryControl.value
  });

  /** Computed signal filtering the search history based on the active query input. */
  protected readonly filteredHistory = computed(() => {
    return this.searchService.getFilteredHistory(this.queryFilter());
  });

  /** Loads initial search history and exposes the request state through a resource. */
  protected readonly historyResource = rxResource({
    params: () => true,
    stream: () => this.searchService.loadHistory(),
    defaultValue: false,
  });

  private readonly saveRequest = signal<SearchQuery | undefined>(undefined);
  private readonly updateRequest = signal<{search: Search} | undefined>(undefined);

  /** Saves a new search and exposes its request state. */
  protected readonly saveResource = rxResource({
    params: () => this.saveRequest(),
    stream: ({params}) => this.searchService.saveSearch(params).pipe(
      tap((saved) => {
        if (saved) {
          this.queryControl.reset();
        }
      })
    ),
  });

  /** Updates a selected search and exposes its request state. */
  protected readonly updateResource = rxResource({
    params: () => this.updateRequest(),
    stream: ({params}) => this.searchService.updateSearch(params.search),
  });

  /** Indicates that a save or history update is still in progress. */
  protected readonly isActionPending = computed(() => {
    return this.saveResource.isLoading()
      || this.updateResource.isLoading();
  });

  /** Clears the current search input text. */
  protected onClear(): void {
    this.queryControl.reset();
  }

  /** Submits a new search query if the input is valid, and resets the input field upon completion. */
  protected onSearch(): void {
    if (this.queryControl.invalid || this.isActionPending()) {
      return;
    }
    this.saveRequest.set({query: this.queryControl.value});
  }

  /**
   * Selects an existing search history entry and updates its last seen timestamp.
   *
   * @param item The selected search history record.
   */
  protected onSelect(item: Search): void {
    if (this.isActionPending()) {
      return;
    }
    this.updateRequest.set({search: item});
  }
}
