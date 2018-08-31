public enum Protocol {
    TAKE,
    GAME_STARTS,
    NEXT_ROUND,
    GAME_ENDS;

    static Protocol parse(String msg) {
        String first = msg.split(":")[0];
        return Protocol.valueOf(first);
    }

    static String construct(Protocol p, String... args) {
        String arg = String.join(":", args);
        return p.name() + ":" + arg;
    }

}
