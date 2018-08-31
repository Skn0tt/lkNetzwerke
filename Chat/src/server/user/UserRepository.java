package server.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UserRepository implements RecipientSearchable {
  private final Map<String, User> byAddress = new HashMap<>();
  private final Map<String, User> byNickname = new HashMap<>();
  private final Set<String> bannedAddresses = new HashSet<>();

  private final Consumer<String> onChange;

  public UserRepository(Consumer<String> onChange) {
    this.onChange = onChange;
  }

  public User find(String nick) throws UserBannedException {
    User u = byNickname.get(nick);

    if (u != null && isBanned(u)) {
      throw new UserBannedException(u);
    }

    return u;
  }

  public User find(Address address) throws UserBannedException {
    if (isBanned(address)) {
      throw new UserBannedException(address);
    }

    return byAddress.get(address.toString());
  }

  public HashSet<String> getIdentifiers() {
    return new HashSet<>(byNickname.keySet());
  }

  private boolean isBanned(User u) {
    return isBanned(u.address);
  }

  private boolean isBanned(Address a) {
    return bannedAddresses.contains(a.toString());
  }

  public void add(User user) {
    byAddress.put(user.address.toString(), user);
    byNickname.put(user.getIdentifier(), user);

    onChange.accept(user.nickname);
  }

  public void remove(User user) {
    byAddress.remove(user.address.toString());
    byNickname.remove(user.getIdentifier());

    onChange.accept(user.nickname);
  }

  public void ban(Address a) {
    try {
      User u = find(a);

      byAddress.remove(a.toString());
      byNickname.remove(u.nickname);

      onChange.accept(u.nickname);

      bannedAddresses.add(a.toString());
    } catch (UserBannedException ignored) {}
  }

  public void ban(User u) {
    ban(u.address);
  }

  public void ban(String nick) {
    try {
      User u = find(nick);
      ban(u);
    } catch (UserBannedException ignored) {}
  }

  public static class UserBannedException extends Exception {
    UserBannedException(User user) {
      super(user.nickname + "is banned.");
    }

    UserBannedException(Address a) {
      super(a.toString() + "is banned.");
    }
  }

}
