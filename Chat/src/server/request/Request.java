package server.request;

import com.sun.istack.internal.Nullable;
import server.Command;
import server.user.Address;
import server.user.User;

import java.util.function.Consumer;

public class Request {

  public final Command cmd;
  public final Address address;
  public final boolean userKnown;
  public final User user;

  private final Consumer<String> answer;

  Request(Command cmd, Address address, Consumer<String> answer, @Nullable User user) {
    this.cmd = cmd;
    this.address = address;
    this.userKnown = user != null;
    this.answer = answer;
    this.user = user;
  }

  public void answer(String msg) {
    answer.accept(msg);
  }

  public void success(String msg) {
    answer("+" + msg);
  }

  public void error(String msg) {
    answer("-" + msg);
  }

}
