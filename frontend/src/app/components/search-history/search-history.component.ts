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

class NeverErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(): boolean {
    return false;
  }
}

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

  protected readonly isDateToday = isDateToday;
  protected readonly neverErrorStateMatcher = new NeverErrorStateMatcher();

  protected readonly queryControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(3), Validators.maxLength(29)]
  });

  protected readonly queryFilter = toSignal(this.queryControl.valueChanges, {
    initialValue: this.queryControl.value
  });

  protected readonly filteredHistory = computed(() => {
    const filter = this.queryFilter().trim().toLowerCase();
    const history = this.searchService.history();
    if (!filter) {
      return history;
    }
    return history.filter((item) => item.query.toLowerCase().includes(filter));
  });

  ngOnInit(): void {
    this.searchService.loadHistory()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: (err) => console.error('Error loading history:', err),
      });
  }

  protected onClear(): void {
    this.queryControl.reset();
  }

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

  protected onSelect(item: Search): void {
    this.searchService.updateSearch(item)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: (err) => console.error('Error updating search:', err),
      });
  }
}
