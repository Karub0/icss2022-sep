package nl.han.ica.datastructures;

import java.util.ArrayList;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
    private List<T> items;

    public HANStack() {
        items = new ArrayList<>();
    }

    @Override
    public void push(T value) {
        items.add(value);
    }

    @Override
    public T pop() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Stack is leeg!");
        }
        return items.remove(items.size() - 1);
    }

    @Override
    public T peek() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Stack is leeg!");
        }
        return items.get(items.size() - 1);
    }

    public int size() {
        return items.size();
    }
}
