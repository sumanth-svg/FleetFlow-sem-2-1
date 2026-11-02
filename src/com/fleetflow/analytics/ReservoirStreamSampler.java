package com.fleetflow.analytics;

import com.fleetflow.model.TelemetryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reservoir Sampling (Algorithm R) for continuous stream analytics.
 * Space Complexity: O(k) for reservoir of size k.
 * Time Complexity: O(N) stream processing.
 *
 * Mathematical Guarantee:
 * Every element in a stream of unknown size N has an exact, uniform probability of k / N
 * of being included in the sample.
 *
 * Used in FleetFlow to maintain an unbiased random sample of continuous vehicle IoT telemetry,
 * speed anomalies, and engine temperature events from an unbounded stream.
 */
public class ReservoirStreamSampler<T> {

    private final int sampleSize;
    private final List<T> reservoir;
    private int itemsSeen;
    private final Random random;

    public ReservoirStreamSampler(int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Sample size must be positive");
        }
        this.sampleSize = sampleSize;
        this.reservoir = new ArrayList<>(sampleSize);
        this.itemsSeen = 0;
        this.random = new Random();
    }

    /**
     * Feeds a single item from the continuous stream into the reservoir.
     */
    public void processItem(T item) {
        itemsSeen++;
        if (reservoir.size() < sampleSize) {
            reservoir.add(item);
        } else {
            // Generate random index j in [0, itemsSeen - 1]
            int j = random.nextInt(itemsSeen);
            if (j < sampleSize) {
                reservoir.set(j, item);
            }
        }
    }

    /**
     * Batch process a stream sequence.
     */
    public void processStream(Iterable<T> stream) {
        for (T item : stream) {
            processItem(item);
        }
    }

    public List<T> getSample() {
        return new ArrayList<>(reservoir);
    }

    public int getItemsSeen() {
        return itemsSeen;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public double getInclusionProbability() {
        if (itemsSeen == 0) return 0.0;
        return Math.min(1.0, (double) sampleSize / itemsSeen);
    }
}
