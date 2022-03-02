public class LinkedListDeque<T> {
    private static class TNode<T> {
        private T item;
        private TNode prev;
        private TNode next;

        TNode(T i, TNode p, TNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private TNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new TNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        TNode p = new TNode(item, sentinel, sentinel.next);
        sentinel.next.prev = p;
        sentinel.next = p;
        size += 1;
    }

    public void addLast(T item) {
        TNode p = new TNode(item, sentinel.prev, sentinel);
        sentinel.prev.next = p;
        sentinel.prev = p;
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (TNode p = sentinel.next; p != sentinel; p = p.next) {
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        TNode ans = sentinel.next;
        sentinel.next = ans.next;
        ans.next.prev = sentinel;
        size -= 1;
        return (T) ans.item;
    }

    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        TNode ans = sentinel.prev;
        sentinel.prev = ans.prev;
        ans.prev.next = sentinel;
        size -= 1;
        return (T) ans.item;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        TNode p = sentinel;
        for (int i = 0; i <= index; i++) {
            p = p.next;
        }
        return (T) p.item;
    }

    private T getRec(int index, TNode p) {
        if (index == 0) {
            return (T) p.item;
        }
        return getRec(index - 1, p.next);
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRec(index, sentinel.next);
    }
}
