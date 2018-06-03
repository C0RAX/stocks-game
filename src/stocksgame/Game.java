package stocksgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Game {

    private Map<Integer, Deck> decks;
    private final Map<Integer, Player> players;
    private int[] prices = {100, 100, 100, 100, 100};
    private List<Player> playingplayers;
    private Card[] turncards;
    private int[] votes;
    private int turn;

    public Card[] getTurncards() {
        return turncards;
    }

    public final void setTurncards() {
        for (Stock s : Stock.values()) {
            int p = (int) (Math.random() * decks.get(s.ordinal()).cards.size());
            turncards[s.ordinal()] = decks.get(s.ordinal()).cards.get(p);
        }
    }

    public void setDecks(Map<Integer, Deck> decks) {
        this.decks = decks;
    }

    public void setPrices(int[] prices) {
        this.prices = prices;
    }

    public void setPlayingplayers(Player playingplayers, int n) {
        this.playingplayers.add(n, playingplayers);
    }

    // create a random game 
    public Game() {
        this.playingplayers = new ArrayList<>();
        this.votes = new int[]{0, 0, 0, 0, 0};
        this.turncards = new Card[]{null, null, null, null, null};
        players = new TreeMap<>();
        decks = new TreeMap<>();
        for (Stock s : Stock.values()) {
            decks.put(s.ordinal(), new Deck(s));
        }
        setTurncards();
        this.players.put(0, new Player(0, 500, generatestock()));
        this.players.put(1, new Player(0, 500, generatestock()));
        this.players.put(2, new Player(0, 500, generatestock()));
        this.players.put(3, new Player(0, 500, generatestock()));

    }

    // create a game with specific initial decks and share holdings
    // used for unit testing 
    public Game(Deck[] Decks, int[][] shares) {
        this.playingplayers = new ArrayList<>();
        this.votes = new int[]{0, 0, 0, 0, 0};
        this.turncards = new Card[]{null, null, null, null, null, null};
        players = new TreeMap<>();
        decks = new TreeMap<>();
        int i = 0;
        for (Stock s : Stock.values()) {
            decks.put(s.ordinal(), Decks[i]);
            i++;
        }
        setTurncards();
        for (int[] share : shares) {
            this.players.put(0, new Player(0, 500, share));
        }
        for (int[] share : shares) {
            this.players.put(1, new Player(0, 500, share));
        }
        for (int[] share : shares) {
            this.players.put(2, new Player(0, 500, share));
        }
        for (int[] share : shares) {
            this.players.put(3, new Player(0, 500, share));
        }

    }

    public boolean existsPlayer(String playerId) {
        return players.containsKey(playerId);
    }

    public String getCash(int playerId) {
        return players.get(playerId).getCash();
    }

    public int[] getShares(int playerId) {
        return players.get(playerId).getShares();
    }

    public int[] getPrices() {
        return prices;
    }

    public int getPrice(int n) {
        return prices[n];
    }

    public int[] getCards() {
        return Card.EFFECTS;
    }

    public String executeVotes() {
        boolean allvoted = false;
        for (Player p : playingplayers) {
            if (p.getVotes() < 2) {
                allvoted = false;
                break;
            } else {
                allvoted = true;
            }
        }
        if (allvoted) {
            playingplayers.stream().forEach((p) -> {
                p.setVotes(0);
            });
            for (int i = 0; i < votes.length; i++) {
                if (votes[i] < 0) {
                    decks.get(i).cards.remove(turncards[i]);
                } else if (votes[i] > 0) {
                    prices[i] += turncards[i].effect;
                    decks.get(i).cards.remove(turncards[i]);
                }
            }
            setTurncards();
            return "";
        } else {
            return " ";
        }

    }

    public boolean getPlayer(String s) {
        return players.containsKey(Integer.parseInt(s));
    }

    public Player getPlayerObj(String s) {
        return players.get(Integer.parseInt(s));
    }

    public String buy(int id, Stock s, int amount) {
        players.get(id).setShares(s.ordinal(), amount, -(prices[s.ordinal()] + 3));
        String shares = Arrays.toString(players.get(id).getShares());

        players.get(id).setTrades(players.get(id).getTrades() + 1);

        return "New shares:" + shares;
    }

    public String sell(int id, Stock s, int amount) {
        players.get(id).setShares(s.ordinal(), -amount, prices[s.ordinal()]);
        String shares = Arrays.toString(players.get(id).getShares());
        players.get(id).setTrades(players.get(id).getTrades() + 1);

        return "New shares:" + shares;
    }

    public String vote(int id, Stock s, boolean vote) {
        String a;
        if (vote) {
            votes[s.ordinal()] += 1;
            a = "yes";
        } else {
            votes[s.ordinal()] -= 1;
            a = "no";
        }

        players.get(id).setVotes(players.get(id).getVotes() + 1);
        if ("".equals(executeVotes())) {
            StockServer.clientList.stream().forEach((service) -> {
                if (turn < 5) {
                    service.getOut().println("All Votes have been Cast and executed, the new cards this round are" + Arrays.toString(getTurncards()));
                    service.getOut().println("Prices = " + Arrays.toString(this.getPrices()));
                    service.getOut().println("Cards = " + Arrays.toString(this.getTurncards()));
                    for (int i = 0; i < 4; i++) {
                        service.getOut().println("Player " + i + ", Cash = " + (this.getPlayerObj(String.valueOf(i)).getCash())
                                + ", Shares = " + Arrays.toString(this.getPlayerObj(String.valueOf(i)).getShares()));
                    }
                    turn++;
                } else {
                    service.getOut().println("All five turns played " + Arrays.toString(getPrices()));
                    playingplayers.stream().forEach((p) -> {
                        service.getOut().println(p.sellall(prices));
                    });

                }
            });
            players.get(id).setVotecheck(s.ordinal(), ((vote) ? 1 : -1));
            return "You voted " + a + " for " + (s.name());
        }
        players.get(id).setVotecheck(s.ordinal(), ((vote) ? 1 : -1));
        return "You voted " + a + " for " + (s.name());
    }

    private int[] generatestock() {
        int n = 10;
        int[] stocks = {0, 0, 0, 0, 0};
        for (int i = 0; i < stocks.length; i++) {
            int p = (int) (Math.random() * n);
            stocks[i] = p;
            n = n - p;
            if (i == 4) {
                stocks[i] = stocks[i] + n;
            }
        }
        return stocks;
    }
}
