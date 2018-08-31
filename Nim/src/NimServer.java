import java.util.List;
import java.util.function.Consumer;

public class NimServer extends Server {

    private Phases phase = Phases.PRE_GAME;
    private final NimGame game = new NimGame();
    private final Consumer<Player> onConnect;

    NimServer(int port, Consumer<Player> onConnect) {
        super(port);
        this.onConnect = onConnect;
    }

    @Override
    void processNewConnection(String pClientIP, int pClientPort) {
        Player p = new Player(pClientIP, pClientPort);
        game.addPlayer(p);
        success(pClientIP, pClientPort);
        onConnect.accept(p);
    }

    @Override
    void processMessage(String pClientIP, int pClientPort, String pMessage) {
        Player player = new Player(pClientIP, pClientPort);
        Request r = Request.parse(pMessage);

        System.out.println(player.toString() + " " + pMessage);

        switch (phase) {
            case PRE_GAME:
                failure(player, "Game not started");
                break;
            case AFTER_GAME:
                failure(player, "Game already finished");
                break;
            case GAME_STARTED:
                switch (r.p) {
                    case TAKE:
                        onTake(player, r.args);
                }
                break;
        }
    }

    void onTake(Player player, String[] args) {
        try {
            int amount = Integer.parseInt(args[0]);
            game.take(amount, player);
            success(player);
            nextRound();
        } catch (NumberFormatException e) {
            failure(player, "Number not legal");
        } catch (NimGame.TakeAmountNotAllowedException e) {
            failure(player, "Amount not allowed");
        } catch (NimGame.NotPlayersTurnException e) {
            failure(player, "Not your turn");
        }
    }

    public void startGame() {
        this.phase = Phases.GAME_STARTED;
        String msg = Protocol.construct(Protocol.GAME_STARTS);
        sendToAll(msg);
        nextRound();
    }

    void nextRound() {
        if (game.isFinished()) {
            gameFinished();
            return;
        }

        Player currentPlayer = game.getCurrent();
        int currentAmount = game.getAmount();
        String msg = Protocol.construct(
                Protocol.NEXT_ROUND,
                "" + currentAmount,
                currentPlayer.toString()
        );
        sendToAll(msg);
    }

    void gameFinished() {
        String msg = Protocol.construct(Protocol.GAME_ENDS, game.getWinner().toString());
        this.phase = Phases.AFTER_GAME;
        sendToAll(msg);
    }

    @Override
    void processClosedConnection(String pClientIP, int pClientPort) {}

    private void success(Player p) {
        success(p.ip, p.port);
    }

    private void success(String ip, int port) {
        send(ip, port, "OK");
    }

    private void failure(Player p, String msg) {
        failure(p.ip, p.port, msg);
    }

    private void failure(String ip, int port, String message) {
        String txt = String.format("ERR %s", message);
        send(ip, port, txt);
    }
}
