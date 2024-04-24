package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        Iterator<T> iterator = iterator();

        if (!iterator.hasNext()) {
            return null;
        }
        T currentMax = iterator.next();
        while (iterator.hasNext()) {
            T nextValue = iterator.next();
            if (c.compare(currentMax, nextValue) < 0) {
                currentMax = nextValue;
            }
        }
        return currentMax;
    }
}
