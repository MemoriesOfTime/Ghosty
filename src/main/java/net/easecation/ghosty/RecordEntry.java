package net.easecation.ghosty;

/**
 * @author glorydark
 */
public class RecordEntry<T> {

    private final T entry;
    private final int tick;

    public RecordEntry(int tick, T entry) {
        this.tick = tick;
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }

    public int getTick() {
        return tick;
    }
}
