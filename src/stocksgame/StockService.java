package stocksgame;

import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StockService implements Runnable {

    private Scanner in;
    private PrintWriter out;

    private String playerID;
    private boolean login;
    private Game game;

    public StockService(Game game, Socket socket) {
        this.game = game;
        playerID = null;
        login = false;
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void run() {
        System.out.println("Service Run");
        login();
        while (login) {
            try {
                Request request
                        = Request.parse(in.nextLine());
                String response = execute(game, request);
                // send CRLF to indicate end of response
                out.println(response + "\r\n");
            } catch (Exception e) {
                login = false;
                System.out.println("exception");
            }
        }
        logout();
    }

    public synchronized String execute(Game game, Request request) {
        try {
            switch (request.type) {
                case CARDS:
                    return Arrays.toString(game.getTurncards());
                case VOTE:
                    String stockID = (request.params[0]);
                    if (game.getPlayerObj(playerID).getVotes() < 2
                            && (game.getPlayerObj(playerID).getVotecheck()[Stock.parse(stockID).ordinal()]) == 0) {
                        if ("yes".equals(request.params[1].toLowerCase())) {
                            return "" + game.vote(Integer.parseInt(this.playerID), Stock.parse(stockID), true) + game.executeVotes();
                        } else if ("no".equals(request.params[1].toLowerCase())) {
                            return "" + game.vote(Integer.parseInt(this.playerID), Stock.parse(stockID), false) + game.executeVotes();
                        }
                    } else if ((game.getPlayerObj(playerID).getVotecheck()[Stock.parse(stockID).ordinal()]) == 1
                            || (game.getPlayerObj(playerID).getVotecheck()[Stock.parse(stockID).ordinal()]) == -1) {
                        return "Duplicate votes are not allowed";
                    }
                    if (game.getPlayerObj(playerID).getVotes() == 2)
                    return "No votes left";
                    return "Invalid input";
                case PRICES:
                    return Arrays.toString(game.getPrices());
                case CASH:
                    //playerID = Integer.parseInt(request.params[0]);
                    return game.getCash(Integer.parseInt(this.playerID));
                case BUY:
                    stockID = (request.params[0]);
                    int amount = Integer.parseInt(request.params[1]);

                    int cost = 3 + (amount * game.getPrice(Stock.parse(stockID).ordinal()));

                    if (game.getPlayerObj(playerID).getTrades() < 2) {
                        if (cost > Integer.parseInt(game.getPlayerObj(playerID).getCash())) {
                            return "Not enough Cash";
                        } else {
                            return game.buy(Integer.parseInt(this.playerID), Stock.parse(stockID), amount);
                        }
                    } else {
                        return "No trades left";
                    }
                case SELL:
                    stockID = (request.params[0]);
                    amount = Integer.parseInt(request.params[1]);

                    if (game.getPlayerObj(playerID).getTrades() < 2) {
                        if (game.getPlayerObj(playerID).getShare(Stock.parse(stockID).ordinal()) < amount) {
                            return "Not enough shares";
                        } else {
                            return game.sell(Integer.parseInt(this.playerID), Stock.parse(stockID), amount);
                        }
                    } else {
                        return "No trades left";
                    }
                case SHARES:
                    return Arrays.toString(game.getShares(Integer.parseInt(this.playerID)));
                case INVALID:
                    return "Command invalid or failed!";
                case LOGOUT:
                    login = false;
                    return "Goodbye!";
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void login() {
        // putty adds some characters when it establishes a telnet connection
        // this makes the user remove the first line with these characters 
        out.println("Please press enter");
        String dummy = in.nextLine().trim();
        out.println("Please enter your player id");
        try {
            String input = in.nextLine().trim();
            if (game.getPlayer(input)) {
                playerID = input;
                out.println("Welcome Player " + playerID);
                System.out.println("Login: " + playerID);
                game.setPlayingplayers(game.getPlayerObj(input), Integer.parseInt(input));
                out.println("Prices = " + Arrays.toString(game.getPrices()));
                out.println("Cards = " + Arrays.toString(game.getTurncards()));
                for (int i = 0; i < 4; i++) {
                    out.println("Player " + i + ", Cash = " + (game.getPlayerObj(String.valueOf(i)).getCash())
                            + ", Shares = " + Arrays.toString(game.getPlayerObj(String.valueOf(i)).getShares()));
                }
                login = true;
            } else {
                out.println("Invalid login attempt!");
            }
            out.println(); // don't forget empty line terminator!
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        if (playerID != null) {
            System.out.println("Logout: " + playerID);
        }
        try {
            Thread.sleep(2000);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
