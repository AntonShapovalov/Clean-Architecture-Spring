import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { SearchService } from '../../services/search-service';

@Component({
  selector: 'app-search-history',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchHistoryComponent implements OnInit {
  private readonly searchService = inject(SearchService);

  protected readonly queryControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(3), Validators.maxLength(29)]
  });

  protected readonly history = this.searchService.history;

  ngOnInit(): void {
    this.searchService.loadHistory().subscribe({
      error: (err) => console.error('Error loading history:', err),
    });
  }

  protected onSearch(): void {
    if (this.queryControl.invalid) {
      return;
    }

    const query = this.queryControl.value;
    this.searchService.saveSearch({ query }).subscribe({
      error: (err) => console.error('Error saving search:', err),
    });
    this.queryControl.reset();
  }
}
