import java.util.List;

public class Group implements Recipiable {
  final String identifier;
  final List<User> members;

  public Group(String identifier, List<User> members) {
    this.identifier = identifier;
    this.members = members;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public List<User> getUsers() {
    return members;
  }
}
