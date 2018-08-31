public class NimGame {

    private Counter counter = new Counter(15);

    private Ring<Player> players = new Ring<>();

    private Player winner;

    void addPlayer(Player p) {
        players.add(p);
    }

    Player getCurrent() {
        return players.getCurrent();
    }

    int getAmount() {
        return counter.getValue();
    }

    boolean isCurrentPlayer(Player p) {
        return getCurrent().equals(p);
    }

    void take(int amount, Player taker) throws NotPlayersTurnException, TakeAmountNotAllowedException {
        if (isFinished()) {
            throw new NotPlayersTurnException();
        }

        if (!isCurrentPlayer(taker)) {
            throw new NotPlayersTurnException();
        }

        if (!isValidTakeAmount(amount)) {
            throw new TakeAmountNotAllowedException();
        }

        try {
            counter.decrement(amount);
        } catch (Counter.ValueNotAllowedException e) {
            throw new TakeAmountNotAllowedException();
        }

        if (counter.getValue() == 0) {
            winner = taker;
        }

        players.next();
    }

    Player getWinner() {
        return winner;
    }

    boolean isFinished() {
        return winner != null;
    }

    boolean isValidTakeAmount(int amount) {
        return amount > 0 && amount <= 3;
    }

    class NotPlayersTurnException extends Exception {}

    class TakeAmountNotAllowedException extends Exception {}

}
