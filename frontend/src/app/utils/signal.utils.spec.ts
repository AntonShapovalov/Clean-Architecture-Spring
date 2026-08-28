import {Injector, signal} from '@angular/core';
import {TestBed} from '@angular/core/testing';
import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import {delayedSignal} from './signal.utils';

describe('delayedSignal', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.useRealTimers();
  });

  it('should initialize with false when source signal is false', () => {
    TestBed.runInInjectionContext(() => {
      const source = signal(false);
      const delayed = delayedSignal(source);

      TestBed.tick();
      expect(delayed()).toBe(false);
    });
  });

  it('should delay transitioning to true by default 500ms', () => {
    TestBed.runInInjectionContext(() => {
      const source = signal(false);
      const delayed = delayedSignal(source);

      TestBed.tick();
      expect(delayed()).toBe(false);

      source.set(true);
      TestBed.tick();
      expect(delayed()).toBe(false);

      vi.advanceTimersByTime(500);
      expect(delayed()).toBe(true);
    });
  });

  it('should respect custom delayMs', () => {
    TestBed.runInInjectionContext(() => {
      const source = signal(false);
      const delayed = delayedSignal(source, 300);

      TestBed.tick();
      source.set(true);
      TestBed.tick();

      vi.advanceTimersByTime(300);
      expect(delayed()).toBe(true);
    });
  });

  it('should cancel the pending timer and remain false if source changes back to false before delay passes', () => {
    TestBed.runInInjectionContext(() => {
      const source = signal(false);
      const delayed = delayedSignal(source, 500);

      TestBed.tick();
      source.set(true);
      TestBed.tick();

      vi.advanceTimersByTime(300);
      expect(delayed()).toBe(false);

      source.set(false);
      TestBed.tick();
      expect(delayed()).toBe(false);

      vi.advanceTimersByTime(500);
      expect(delayed()).toBe(false);
    });
  });

  it('should immediately transition to false when source becomes false after delay elapsed', () => {
    TestBed.runInInjectionContext(() => {
      const source = signal(false);
      const delayed = delayedSignal(source, 500);

      TestBed.tick();
      source.set(true);
      TestBed.tick();

      vi.advanceTimersByTime(500);
      expect(delayed()).toBe(true);

      source.set(false);
      TestBed.tick();
      expect(delayed()).toBe(false);
    });
  });

  it('should delay transitioning to true even if source is initially true', () => {
    TestBed.runInInjectionContext(() => {
      const source = signal(true);
      const delayed = delayedSignal(source, 500);

      expect(delayed()).toBe(false);
      TestBed.tick();
      expect(delayed()).toBe(false);

      vi.advanceTimersByTime(500);
      expect(delayed()).toBe(true);
    });
  });

  it('should work when created outside injection context by passing an injector in options', () => {
    const injector = TestBed.inject(Injector);
    const source = signal(false);
    const delayed = delayedSignal(source, 500, { injector });

    TestBed.tick();
    expect(delayed()).toBe(false);

    source.set(true);
    TestBed.tick();
    expect(delayed()).toBe(false);

    vi.advanceTimersByTime(500);
    expect(delayed()).toBe(true);
  });
});
