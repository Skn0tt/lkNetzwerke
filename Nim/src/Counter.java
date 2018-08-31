public class Counter {

    int value;

    Counter(int startValue) {
        value = startValue;
    }

    void decrement() throws ValueNotAllowedException {
        decrement(1);
    }

    void decrement(int v) throws ValueNotAllowedException {
        if (value < v) {
            throw new ValueNotAllowedException();
        }

        value -= v;
    }

    int getValue() {
        return value;
    }

    class ValueNotAllowedException extends Exception {}

}
