package server;

import java.util.Arrays;
import java.util.List;

/**
 * Created by simon.knott on 29.06.2018.
 */
public class Command {
    public final CommandVerb verb;
    public final List<String> args;

    private Command(CommandVerb verb, List<String> args) {
        this.verb = verb;
        this.args = args;
    }

    public static String build(CommandVerb cmd, String... args) {
        return cmd + " " + String.join(" ", args);
    }

    public static String build(CommandVerb cmd, Iterable<String> args) {
        return cmd + " " + String.join(" ", args);
    }

    public static Command parse(String cmd) throws CommandVerbUnknownException {
        List<String> parts = Arrays.asList(cmd.split(" "));
        String rawVerb = parts.get(0);
        List<String> args = parts.subList(1, parts.size());

        try {
            CommandVerb verb = CommandVerb.valueOf(rawVerb);

            return new Command(verb, args);
        } catch (IllegalArgumentException e) {
            throw new CommandVerbUnknownException(rawVerb);
        }
    }

    public static class CommandVerbUnknownException extends Exception {
        CommandVerbUnknownException(String args) {
            super("server.Command unknown: " + args);
        }
    }

}
