import { Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SearchHistoryComponent } from './components/search-history/search-history.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SearchHistoryComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class App {
  protected readonly title = signal('omdb-frontend');
}
