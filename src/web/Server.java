package web;

import com.sun.net.httpserver.HttpServer; // 👈 Add this
import java.awt.Desktop;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import service.BankService;
import service.impl.BankServiceImpl;


public class Server {

    public static void start() throws Exception {
        BankService bankService = new BankServiceImpl();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            URI uri = exchange.getRequestURI();
            File file = new File("webui" + (uri.getPath().equals("/") ? "/index.html" : uri.getPath()));
            if (!file.exists()) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            String mime = file.getName().endsWith(".css") ? "text/css" :
                          file.getName().endsWith(".js") ? "application/javascript" : "text/html";
            exchange.getResponseHeaders().set("Content-Type", mime);
            byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().close();
        });

        server.createContext("/api", new RequestHandler(bankService));
        server.start();
        System.out.println("Server started on http://localhost:8080");
        Desktop.getDesktop().browse(new URI("http://localhost:8080"));
    }
}
