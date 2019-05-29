package utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class LastElementCollection<T> implements Collection<T> {

    private boolean hasValue = false;
    private T value;

    public LastElementCollection() {

    }

    @Override
    public int size() {
        return hasValue ? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        return ! hasValue;
    }

    @Override
    public boolean contains(Object o) {
        return hasValue && Objects.equals(o, value);
    }

    @Override
    public Iterator<T> iterator() {
        final T val = value;
        return new Iterator<T>() {
            private boolean first = true;

            @Override
            public boolean hasNext() {
                return first;
            }

            @Override
            public T next() {
                if(!first)
                    return null;
                first = false;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        if(hasValue)
            return new Object[]{value};
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        value = t;
        hasValue = true;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if(hasValue) {
            if(Objects.equals(o, value)) {
                clear();
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        hasValue = false;
    }

    public T pop() {
        if(hasValue) {
            hasValue = false;
            return value;
        }
        throw new IllegalStateException("Il n'y a pas d'élément dans la collection!");
    }
}
