import java.util.function.Consumer;
import java.util.function.Function;

public class RequestBuilder {

  private final FindUser getUser;
  private final Consumer<Answer> onAnswer;

  interface FindUser {
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

  static class Answer {

    final String ip;
    final int port;
    final String msg;

    public Answer(String ip, int port, String msg) {
      this.ip = ip;
      this.port = port;
      this.msg = msg;
    }

  }

}


