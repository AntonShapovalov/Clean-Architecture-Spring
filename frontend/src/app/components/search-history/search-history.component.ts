import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {DatePipe} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {ErrorStateMatcher} from '@angular/material/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {SearchService} from '../../services/search.service';

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
    MatInputModule,
    FormsModule
  ],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchHistoryComponent implements OnInit {
  private readonly searchService = inject(SearchService);

  protected readonly neverErrorStateMatcher = new NeverErrorStateMatcher();

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
    this.searchService.saveSearch({query}).subscribe({
      error: (err) => console.error('Error saving search:', err),
    });
    this.queryControl.reset();
  }
}
