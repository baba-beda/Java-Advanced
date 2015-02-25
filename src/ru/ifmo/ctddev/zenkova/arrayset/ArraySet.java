package ru.ifmo.ctddev.zenkova.arrayset;

import java.util.*;

/**
 * Created by daria on 25.02.15.
 */
public class ArraySet<E extends Comparable> implements SortedSet<E> {

    List<E> eList;
    Comparator<E> eComparator;

    public ArraySet() {
        eList = new ArrayList<>();
        eComparator = null;
    }

    public ArraySet(Collection<E> collection) {
        eComparator = Comparator.naturalOrder();
        constructorHelper(collection);
    }

    public ArraySet(Collection<E> collection, Comparator<E> comparator) {
        eComparator = comparator;
        constructorHelper(collection);
    }

    private ArraySet(List<E> list, boolean sorted, Comparator<E> comparator) {
        if (sorted) {
            eComparator = comparator;
            eList = list;
        }
    }

    private void constructorHelper(Collection<E> collection) {
        eList = new ArrayList<>();

        for (E e : collection) {
            if (size() == 0) {
                eList.add(e);
                continue;
            }
            int insertionPoint = Collections.binarySearch(eList, e, eComparator);
            if (insertionPoint < 0) {
                eList.add(- insertionPoint - 1, e);
            }
        }
    }

    @Override
    public Comparator<? super E> comparator() {
        if (eComparator.equals(Comparator.naturalOrder())) {
            return null;
        }
        return eComparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        int fromPos = findPosition(fromElement);
        int toPos = findPosition(toElement);

        return subSet(fromPos, toPos);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        int toPos = findPosition(toElement);

        return subSet(0, toPos);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        int fromPos = findPosition(fromElement);

        return subSet(fromPos, size());
    }

    private SortedSet<E> subSet(int fromPos, int toPos) {
        return new ArraySet<>(eList.subList(fromPos, toPos), true, eComparator);
    }

    private int findPosition(E element) {
        int pos = Collections.binarySearch(eList, element, eComparator);

        if (pos < 0) {
            pos = - pos - 1;
        }
        return pos;
    }

    @Override
    public E first() {
        firstLastHelper();
        return eList.get(0);
    }

    @Override
    public E last() {
        firstLastHelper();
        return eList.get(size() - 1);
    }

    private void firstLastHelper() {
        if (isEmpty()) {
            throw new NoSuchElementException("No such element");
        }
    }

    @Override
    public int size() {
        return eList.size();
    }

    @Override
    public boolean isEmpty() {
        return eList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        int pos;
        try {
            pos = Collections.binarySearch(eList, (E) o, eComparator);
        } catch (ClassCastException e) {
            return false;
        }
        return (pos >= 0);
    }

    @Override
    public Iterator<E> iterator() {
        return new SetIterator<>(eList);
    }

    @Override
    public Object[] toArray() {
        return eList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return eList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Set is immutable");
    }
}
