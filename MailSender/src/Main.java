public class Main {
  public static void main(String... args) {
    MessageSender.ConnectionOptions connectionOptions = ConnectionOptionsModal.askForConnectionOptions();
    MessageSender.UserCredentials credentials = UserCredentialsModal.askForUserCredentials();

    Composer composer = new Composer();
    composer.onSend(envelope -> {
      MessageSender.send(connectionOptions, credentials, envelope);
    });
  }
}
