import {ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit} from '@angular/core';
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {DatePipe} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {ErrorStateMatcher} from '@angular/material/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {SearchService} from '../../services/search.service';
import {Search} from '../../models/search.model';
import {isDateToday} from '../../utils/date.utils';

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
    FormsModule
  ],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchHistoryComponent implements OnInit {
  private readonly searchService = inject(SearchService);
  private readonly destroyRef = inject(DestroyRef);

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
    const filter = this.queryFilter().trim().toLowerCase();
    const history = this.searchService.history();
    if (!filter) {
      return history;
    }
    return history.filter((item) => item.query.toLowerCase().includes(filter));
  });

  /** Initializes component and triggers initial search history loading from the backend. */
  ngOnInit(): void {
    this.searchService.loadHistory()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: (err) => console.error('Error loading history:', err),
      });
  }

  /** Clears the current search input text. */
  protected onClear(): void {
    this.queryControl.reset();
  }

  /** Submits a new search query if the input is valid, and resets the input field upon completion. */
  protected onSearch(): void {
    if (this.queryControl.invalid) {
      return;
    }
    this.searchService.saveSearch({query: this.queryControl.value})
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => this.queryControl.reset(),
        error: (err) => console.error('Error saving search:', err),
      });
  }

  /**
   * Selects an existing search history entry and updates its last seen timestamp.
   *
   * @param item The selected search history record.
   */
  protected onSelect(item: Search): void {
    this.searchService.updateSearch(item)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: (err) => console.error('Error updating search:', err),
      });
  }
}
