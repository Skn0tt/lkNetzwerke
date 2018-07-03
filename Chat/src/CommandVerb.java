public enum CommandVerb {
  QUIT("QUIT"),
  SEND("SEND"),
  SEND_PUBLIC("SEND_PUBLIC"),
  HELP("HELP"),
  RECIPIENTS_CHANGE("RECIPIENTS_CHANGE"),
  NEW_MESSAGE("NEW_MESSAGE"),
  NEW_PUBLIC_MESSAGE("NEW_PUBLIC_MESSAGE"),
  BAN_USER("BAN_USER"),
  LOGIN("LOGIN"),
  NEW_GROUP("NEW_GROUP")
  ;

  private final String text;

  CommandVerb(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

