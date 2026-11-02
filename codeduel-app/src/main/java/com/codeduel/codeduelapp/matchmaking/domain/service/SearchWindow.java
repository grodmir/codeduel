package com.codeduel.codeduelapp.matchmaking.domain.service;

import java.time.Duration;

public class SearchWindow {

    private final int base;
    private final int stepPerSecond;
    private final int max;

    public SearchWindow(int base, int stepPerSecond, int max) {
        if (base < 0) {
            throw new IllegalArgumentException("base must be >= 0");
        }
        if (stepPerSecond < 0) {
            throw new IllegalArgumentException("stepPerSecond must be >= 0");
        }
        if (max < base) {
            throw new IllegalArgumentException("max must be >= base");
        }
        this.base = base;
        this.stepPerSecond = stepPerSecond;
        this.max = max;
    }

    /** Размер окна (±) для игрока, который ждёт {@code waited}. Отрицательное время считается нулевым. */
    public int sizeFor(Duration waited) {
        long seconds = Math.clamp(waited.toSeconds(), 0L, Integer.MAX_VALUE);
        return (int) Math.min(max, base + (long) stepPerSecond * seconds);
    }
}
