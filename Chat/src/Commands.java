import java.util.Arrays;

/**
 * Created by simon.knott on 29.06.2018.
 */
public class Commands {
    static final String SEND_ALL = "SEND_ALL";
    static final String QUIT = "QUIT";
    static final String WHISPER = "WHISPER";
    static final String SET_NICK = "SET_NICK";
    static final String NEW_MESSAGE = "NEW_MESSAGE";
    static final String USER_CHANGE = "USER_CHANGE";

    static CommandInfo parse(String cmd) {
        String[] parts = cmd.split(" ");
        String command = parts[0];
        String[] args = tail(parts);

        return new CommandInfo(command, args);
    }

    public static String build(String cmd, Iterable<String> args) {
        return cmd + " " + String.join(" ", args);
    }

    public static String build(String cmd, String... args) {
        return cmd + " " + String.join(" ", args);
    }

    // https://stackoverflow.com/questions/33280240/generic-array-tail-function
    private static <T> T[] tail(T[] array) {
        if (array.length == 0)
            throw new IllegalArgumentException("Array cannot be empty");

        return java.util.Arrays.copyOfRange(array, 1, array.length);
    }

    static class CommandInfo {
        final String cmd;
        final String[] args;

        public CommandInfo(String cmd, String... args) {
            this.cmd = cmd;
            this.args = args;
        }
    }

}
