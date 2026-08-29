import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {MatSidenavModule} from '@angular/material/sidenav';
import {SearchHistoryComponent} from './components/search-history/search-history.component';

/**
 * Root application component for OMDB frontend.
 *
 * Provides the global layout shell featuring a persistent sidebar with search history
 * and a main content viewport connected to the Angular router outlet.
 */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatSidenavModule, SearchHistoryComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class App {
  /** Application title identifier. */
  protected readonly title = signal('omdb-frontend');
}
