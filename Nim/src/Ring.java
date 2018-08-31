import java.util.LinkedList;
import java.util.Queue;

public class Ring<T> {

    Queue<T> queue = new LinkedList<>();

    void add(T v) {
        queue.add(v);
    }

    T getCurrent() {
        return queue.peek();
    }

    void next() {
        queue.add(queue.poll());
    }

}
