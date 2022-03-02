public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int id = nextFirst;
        for (int i = 0; i < size; i++) {
            id = (id + 1) % items.length;
            a[i] = items[id];
        }
        items = a;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int id = nextFirst;
        for (int i = 0; i < size; i++) {
            id = (id + 1) % items.length;
            System.out.print(items[id] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        int newid = (nextFirst + 1) % items.length;
        T ans = items[newid];
//        items[newid] = null;
        nextFirst = newid;
        size -= 1;
        if (items.length >= 16 && (double)size / items.length < 0.25) {
            resize((int) (items.length / 2));
        }
        return ans;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int newid = (nextLast - 1 + items.length) % items.length;
        T ans = items[newid];
//        items[newid] = null;
        nextLast = newid;
        size -= 1;
        if (size >= 16 && (double)size / items.length < 0.25) {
            resize((int) (items.length / 2));
        }
        return ans;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int id = (nextFirst + 1 + index) % items.length;
        return items[id];
    }
}
