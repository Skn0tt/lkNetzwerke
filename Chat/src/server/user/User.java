package server.user;

import java.util.Collections;
import java.util.List;

public class User implements Recipiable {

  public final Address address;
  public final String nickname;

  public User(Address address, String nickname) {
    this.address = address;
    this.nickname = nickname;
  }

  @Override
  public String getIdentifier() {
    return nickname;
  }

  @Override
  public List<User> getUsers() {
    return Collections.singletonList(this);
  }

}
