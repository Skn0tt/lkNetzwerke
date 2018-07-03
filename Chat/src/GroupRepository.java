import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

public class GroupRepository implements RecipientSearchable {
  private final Map<String, Group> groups = new HashMap<>();
  private final Consumer<String> onChange;

  GroupRepository(Consumer<String> onChange) {
    this.onChange = onChange;
  }

  public Group find(String identifier) {
    return groups.get(identifier);
  }

  @Override
  public HashSet<String> getIdentifiers() {
    return new HashSet<>(groups.keySet());
  }

  void add(Group group) {
    groups.put(group.getIdentifier(), group);
    onChange.accept(group.getIdentifier());
  }

  void remove(Group group) {
    groups.remove(group.getIdentifier());
    onChange.accept(group.getIdentifier());
  }
}
