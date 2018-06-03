package stocksgame;

public class Player {

    public final int id;
    private int cash;
    private int[] stocks;
    private int votes;
    private int trades;
    private int[] votecheck;

    public int[] getVotecheck() {
        return votecheck;
    }

    public void setVotecheck(int i, int votecheck) {
        this.votecheck[i] = votecheck;
    }
    
    public void resetVotecheck(int i, boolean votecheck) {
        this.votecheck = new int[]{0,0 , 0, 0, 0};
    }

    public int getTrades() {
        return trades;
    }

    public void setTrades(int trades) {
        this.trades = trades;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int vote) {
        this.votes = vote;
    }

    public Player(int id, Integer cash, int[] Stocks) {
        this.votecheck = new int[]{0,0 , 0, 0, 0};
        this.id = id;
        this.cash = cash;
        this.stocks = Stocks;

    }

    public void setCash(Integer cash) {
        this.cash = cash + this.cash;
    }

    public void setShares(int stock, int amount, int price) {
        cash = cash - amount * price;
        this.stocks[stock] = amount + this.stocks[stock];
    }

    public int sellall(int[] prices) {
        int cash = this.cash;
        for (int i = 0; i < stocks.length; i++) {
            cash = (stocks[i]*prices[i]);
        }
        return cash;
    }

    public int[] getShares() {
        return stocks;
    }

    public int getShare(int n) {
        return stocks[n];
    }

    public Player(int id, int initialCash) {
        this.votecheck = new int[]{0,0 , 0, 0, 0};
        this.id = id;
        cash = initialCash;
    }

    @Override
    public String toString() {
        return String.format("Player [%s: Cash %d]", id, (int) cash);
    }

    public String getCash() {
        return Integer.toString(cash);
    }
}
