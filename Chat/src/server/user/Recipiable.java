package server.user;

import java.util.List;

public interface Recipiable {
  String getIdentifier();
  List<User> getUsers();
}
