package server;

import nrw.Server;
import server.request.Request;
import server.request.RequestBuilder;
import server.user.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatServer extends Server {

  private UserRepository userRepository = new UserRepository(s -> this.publishRecipients());
  private GroupRepository groupRepository = new GroupRepository(s -> this.publishRecipients());

  private RequestBuilder requestBuilder = new RequestBuilder(
    userRepository::find,
    answer -> send(answer.ip, answer.port, answer.msg)
  );

  private ChatServer(int port) {
    super(port);

    System.out.printf("Chatserver listening on %s.", port);
  }

  public void processNewConnection(String pClientIP, int pClientPort) {}

  public void processMessage(String pClientIP, int pClientPort, String pMessage) {
    try {
      Request r = requestBuilder.build(pClientIP, pClientPort, pMessage);
      onRequest(r);
    } catch (Command.CommandVerbUnknownException e) {
      send(pClientIP, pClientPort, "-server.CommandVerb unknown");
    } catch (UserRepository.UserBannedException e) {
      send(pClientIP, pClientPort, "-You are banned.");
    }
  }

  public void processClosedConnection(String pClientIP, int pClientPort) {}

  private void onRequest(Request r) {
    log(r);

    if (!r.userKnown) {
      switch (r.cmd.verb) {
        case LOGIN:
          login(r);
          break;
      }
      return;
    }

    switch (r.cmd.verb) {
      case QUIT:
        quitHandler(r);
        break;
      case NEW_GROUP:
        newGroup(r);
        break;
      case BAN_USER:
        banUserHandler(r);
        break;
      case HELP:
        helpHandler(r);
        break;
      case SEND:
        sendHandler(r);
        break;
      case SEND_PUBLIC:
        sendPublicHandler(r);
        break;
      default:
        r.error("Unknown Error");
    }
  }

  private void log(Request r) {
    String nickname = "";
    System.out.printf(
      "%s@%s:%s : %s %s",
      nickname, r.address.ip, r.address.port,
      r.cmd.verb, String.join(" ", r.cmd.args)
    );

    System.out.println();
  }

  /*
    # Handlers
   */

  private void login(Request r) {
    String nickname = r.cmd.args.get(0);

    User newUser = new User(r.address, nickname);

    userRepository.add(newUser);

    r.success("203 Logged in");
  }

  private void newGroup(Request r) {
    String groupId = r.cmd.args.get(0);
    List<String> userNames = r.cmd.args.subList(1, r.cmd.args.size());

    List<User> users = userNames
      .parallelStream()
      .map(u -> {
        try {
          return userRepository.find(u);
        } catch (UserRepository.UserBannedException ignored) {
          return null;
        }
      })
      .collect(Collectors.toList());

    boolean allKnown = users
      .parallelStream()
      .allMatch(Objects::nonNull);

    if (!allKnown) {
      r.error("One of the users is unknown.");
      return;
    }

    Group g = new Group(groupId, users);
    groupRepository.add(g);

    r.success("200 Success.");
  }

  private void sendHandler(Request r) {
    String recipientIdentifier = r.cmd.args.get(0);
    String msg = r.cmd.args.get(1);

    Recipiable recipient = findRecipient(recipientIdentifier);
    String cmd = Command.build(CommandVerb.NEW_MESSAGE, r.user.nickname, recipient.getIdentifier(), msg);

    for (User u : recipient.getUsers()) {
      send(u, cmd);
    }

    r.success("200 Success");
  }

  private void helpHandler(Request r) {
    r.answer(
      "THIS IS THE HELP :)"
    );
  }

  private void banUserHandler(Request r) {
    String userToBan = r.cmd.args.get(0);

    userRepository.ban(userToBan);

    r.success("200 Success");
  }

  private void quitHandler(Request r) {
    userRepository.remove(r.user);

    r.success("200 Success");
  }

  private void sendPublicHandler(Request r) {
    String msg = r.cmd.args.get(0);

    String cmd = Command.build(CommandVerb.NEW_PUBLIC_MESSAGE, r.user.nickname, msg);
    sendToAll(cmd);

    r.success("200 Success");
  }

  /*
    # Helpers
   */
  private void publishRecipients() {
    HashSet<String> recipients = userRepository.getIdentifiers();
    recipients.addAll(groupRepository.getIdentifiers());

    String cmd = Command.build(CommandVerb.RECIPIENTS_CHANGE, recipients);
    sendToAll(cmd);
  }

  private Recipiable findRecipient(String identifier) {
    try {
      Recipiable u = userRepository.find(identifier);
      if (u != null) {
        return u;
      }

      Recipiable g = groupRepository.find(identifier);
      return g;
    } catch (UserRepository.UserBannedException e) {
      return null;
    }
  }

  private void send(User u, String msg) {
    send(u.address, msg);
  }

  private void send(Address a, String msg) {
    send(a.ip, a.port, msg);
  }

  public static void main(String... args) {
    new ChatServer(3000);
  }
}
