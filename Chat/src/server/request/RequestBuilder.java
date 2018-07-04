package server.request;

import server.Command;
import server.user.Address;
import server.user.User;
import server.user.UserRepository;

import java.util.function.Consumer;

public class RequestBuilder {

  private final FindUser getUser;
  private final Consumer<Answer> onAnswer;

  public interface FindUser {
    User get(Address a) throws UserRepository.UserBannedException;
  }

  public RequestBuilder(FindUser getUser, Consumer<Answer> onAnswer) {
    this.getUser = getUser;
    this.onAnswer = onAnswer;
  }

  public Request build(String ip, int port, String msg) throws Command.CommandVerbUnknownException, UserRepository.UserBannedException {
    Command cmd = Command.parse(msg);
    Address address = Address.from(ip, port);
    User user = getUser.get(address);
    Consumer<String> answer = m -> onAnswer.accept(new Answer(ip, port, m));

    return new Request(cmd, address, answer, user);
  }

  public static class Answer {

    public final String ip;
    public final int port;
    public final String msg;

    Answer(String ip, int port, String msg) {
      this.ip = ip;
      this.port = port;
      this.msg = msg;
    }

  }

}


