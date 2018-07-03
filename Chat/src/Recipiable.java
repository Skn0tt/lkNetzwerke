import com.sun.istack.internal.NotNull;

import java.util.List;

public interface Recipiable {
  String getIdentifier();
  List<User> getUsers();
}
