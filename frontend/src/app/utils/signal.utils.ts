import {effect, Injector, signal, Signal} from '@angular/core';

export interface DelayedSignalOptions {
  injector?: Injector;
}

/**
 * Creates a Signal<boolean> that delays transitioning to `true` by `delayMs`,
 * but transitions to `false` immediately and cancels pending timers if the source becomes `false`.
 *
 * @param source Source boolean signal to observe
 * @param delayMs Delay in milliseconds before setting value to true (default: 500ms)
 * @param options Optional configuration options including an Injector
 * @returns A readonly boolean signal that reflects the delayed state of the source signal.
 */
export function delayedSignal(
  source: Signal<boolean>,
  delayMs = 500,
  options?: DelayedSignalOptions
): Signal<boolean> {
  const delayed = signal(false);

  effect(
    (onCleanup) => {
      const value = source();

      if (!value) {
        delayed.set(false);
        return;
      }

      const timerId = setTimeout(() => delayed.set(true), delayMs);
      onCleanup(() => clearTimeout(timerId));
    },
    {
      allowSignalWrites: true,
      injector: options?.injector,
    }
  );

  return delayed.asReadonly();
}
