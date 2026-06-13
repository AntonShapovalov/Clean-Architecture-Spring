import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Search } from '../../models/search.model';

@Component({
  selector: 'app-search-history',
  imports: [ReactiveFormsModule],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchHistoryComponent implements OnInit {
  private readonly apiService = inject(ApiService);

  protected readonly queryControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(3), Validators.maxLength(29)]
  });

  protected readonly history = signal<Search[]>([]);

  ngOnInit(): void {
    this.loadHistory();
  }

  protected onSearch(): void {
    if (this.queryControl.invalid) {
      return;
    }

    const query = this.queryControl.value;
    this.apiService.saveSearch({ query }).subscribe({
      next: () => {
        this.queryControl.reset();
        this.loadHistory();
      },
      error: (err) => console.error('Error saving search:', err)
    });
  }

  private loadHistory(): void {
    this.apiService.getSearchHistory().subscribe({
      next: (data) => this.history.set(data),
      error: (err) => console.error('Error loading history:', err)
    });
  }
}
