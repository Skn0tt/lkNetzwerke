import javafx.util.Pair;

public class TwoObjectRepository<T> {
  private T a;
  private T b;

  public void add(T t) throws TooManyObjectsAddedException {
    if (this.a == null) {
      this.a = t;
      return;
    }

    if (this.b == null) {
      this.b = t;
      return;
    }

    throw new TooManyObjectsAddedException();
  }

  public Pair<T, T> get(T t) {
    if (this.a.equals(t)) {
      return new Pair<>(this.a, this.b);
    }

    if (this.b.equals(t)) {
      return new Pair<>(this.b, this.a);
    }

    return null;
  }

  public int numberOfSetObjects() {
    int result = 0;

    if (a != null) {
      result++;
    }

    if (b!= null) {
      result++;
    }

    return result;
  }

  public boolean isFull() {
    return numberOfSetObjects() >= 2;
  }

  public void remove(T t) {
    if (a != null && a.equals(t)) {
      a = null;
    }

    if (a != null && b.equals(t)) {
      b = null;
    }
  }

  static class TooManyObjectsAddedException extends Exception {}

}
