package web;
import com.sun.net.httpserver.HttpExchange; // 👈 Add this
import com.sun.net.httpserver.HttpHandler;  // 👈 And this
import service.BankService;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class RequestHandler implements HttpHandler {
    private final BankService service;

    public RequestHandler(BankService service) {
        this.service = service;
    }
    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                map.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
        String response = "";

        try {
            switch (path) {
                case "/api/open" -> {
                    String name = params.get("name");
                    String email = params.get("email");
                    String type = params.get("type");
                    double initial = Double.parseDouble(params.getOrDefault("initial", "0"));
                    String accNo = service.openAccount(name, email, type);
                    if (initial > 0) service.deposit(accNo, initial, "Initial Deposit");
                    response = "Account opened successfully: " + accNo;
                }

                case "/api/deposit" -> {
                    String acc = params.get("account");
                    double amt = Double.parseDouble(params.get("amount"));
                    service.deposit(acc, amt, "Deposit via UI");
                    response = "Deposited ₹" + amt + " to " + acc;
                }

                case "/api/withdraw" -> {
                    String acc = params.get("account");
                    double amt = Double.parseDouble(params.get("amount"));
                    service.withdraw(acc, amt, "Withdraw via UI");
                    response = "Withdrawn ₹" + amt + " from " + acc;
                }

                case "/api/transfer" -> {
                    String from = params.get("from");
                    String to = params.get("to");
                    double amt = Double.parseDouble(params.get("amount"));
                    service.transfer(from, to, amt, "Transfer via UI");
                    response = "Transferred ₹" + amt + " from " + from + " to " + to;
                }

                case "/api/statement" -> {
                    String acc = params.get("account");
                    var txns = service.getStatement(acc);
                    StringBuilder sb = new StringBuilder("Statement for " + acc + ":\n");
                    txns.forEach(t -> sb.append(t.getTimestamp())
                            .append(" | ").append(t.getType())
                            .append(" | ₹").append(t.getAmount())
                            .append(" | ").append(t.getNote()).append("\n"));
                    response = sb.toString();
                }

                case "/api/list" -> {
                    var list = service.listAccounts();
                    StringBuilder sb = new StringBuilder();
                    list.forEach(a -> sb.append(a.getAccountNumber())
                            .append(" | ").append(a.getAccountType())
                            .append(" | ₹").append(a.getBalance()).append("\n"));
                    response = sb.toString();
                }

                case "/api/search" -> {
                    String q = params.get("name");
                    var list = service.searchAccountsByCustomerName(q);
                    StringBuilder sb = new StringBuilder("Results:\n");
                    list.forEach(a -> sb.append(a.getAccountNumber())
                            .append(" | ").append(a.getAccountType())
                            .append(" | ₹").append(a.getBalance()).append("\n"));
                    response = sb.toString();
                }

                default -> response = "Unknown API: " + path;
            }
        } catch (Exception e) {
            response = "Error: " + e.getMessage();
        }

        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
