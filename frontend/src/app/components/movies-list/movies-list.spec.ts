import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MoviesList } from './movies-list';
import { SearchService } from '../../services/search-service';
import { Search } from '../../models/search.model';
import { describe, it, expect, beforeEach } from 'vitest';
import { signal, WritableSignal } from '@angular/core';

describe('MoviesList', () => {
  let component: MoviesList;
  let fixture: ComponentFixture<MoviesList>;
  let mockSearchService: {
    recentSearch: WritableSignal<Search | null>;
  };
  let recentSearchSignal: WritableSignal<Search | null>;

  beforeEach(async () => {
    recentSearchSignal = signal<Search | null>(null);
    mockSearchService = {
      recentSearch: recentSearchSignal
    };

    await TestBed.configureTestingModule({
      imports: [MoviesList],
      providers: [
        { provide: SearchService, useValue: mockSearchService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoviesList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update title when recent search is received', () => {
    const mockSearch: Search = {
      id: 1,
      query: 'Inception',
      updatedDate: '2023-01-01',
      lastSeenAt: '2023-01-01T00:00:00Z',
      isExpired: false
    };

    recentSearchSignal.set(mockSearch);
    fixture.detectChanges();

    expect(component['title']()).toBe('Search results for “Inception”');
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Search results for “Inception”');
  });
});
