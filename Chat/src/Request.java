import com.sun.istack.internal.Nullable;

import java.util.function.Consumer;

class Request {

  final Command cmd;
  final Address address;
  final boolean userKnown;
  final User user;

  private final Consumer<String> answer;

  Request(Command cmd, Address address, Consumer<String> answer, @Nullable User user) {
    this.cmd = cmd;
    this.address = address;
    this.userKnown = user != null;
    this.answer = answer;
    this.user = user;
  }

  void answer(String msg) {
    answer.accept(msg);
  }

  void success(String msg) {
    answer("+" + msg);
  }

  void error(String msg) {
    answer("-" + msg);
  }

}
