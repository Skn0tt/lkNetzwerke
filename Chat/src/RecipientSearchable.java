import java.util.HashSet;

interface RecipientSearchable {
  public Recipiable find(String identifier) throws UserRepository.UserBannedException;
  public HashSet<String> getIdentifiers();
}
