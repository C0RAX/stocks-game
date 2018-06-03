package stocksgame;

public class Request {

    public RequestType type;
    public String[] params;

    public Request(RequestType type, String... params) {
        this.type = type;
        this.params = params;
    }

    public enum RequestType {
        CARDS, VOTE, BUY, SELL, PRICES, CASH, SHARES, INVALID, LOGOUT;
    }

    public static Request parse(String line) {
        try {
            String[] items = line.trim().split("\\s+");
            switch (items[0].toUpperCase()) {
                case "CARDS":
                    return new Request(RequestType.CARDS);
                case "VOTE":
                    return new Request(RequestType.VOTE, items[1],
                            items[2]);
                case "PRICES":
                    return new Request(RequestType.PRICES);
                case "CASH":
                    return new Request(RequestType.CASH);
                case "BUY":
                    return new Request(RequestType.BUY, items[1],
                            items[2]);
                 case "SELL":
                    return new Request(RequestType.SELL, items[1],
                            items[2]);
                case "SHARES":
                    return new Request(RequestType.SHARES);
                case "LOGOUT":
                    return new Request(RequestType.LOGOUT);
                default:
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return new Request(RequestType.INVALID, line);
    }
}
