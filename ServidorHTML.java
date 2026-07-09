
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServidorHTML {

    public static void main(String[] args) throws IOException {
        int puerto = 8080;

        HttpServer servidor = HttpServer.create(new InetSocketAddress(puerto), 0);

        servidor.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String ruta = exchange.getRequestURI().getPath();

                // Si piden la raíz "/", servimos index.html
                if (ruta.equals("/")) {
                    ruta = "/index.html";
                }

                // Quitamos la barra inicial para buscar el archivo local
                Path archivo = Path.of("." + ruta);

                if (Files.exists(archivo) && !Files.isDirectory(archivo)) {
                    byte[] contenido = Files.readAllBytes(archivo);

                    String contentType = obtenerTipo(ruta);
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, contenido.length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(contenido);
                    os.close();
                } else {
                    // Archivo no encontrado -> error 404
                    String mensaje = "<h1>404 - Página no encontrada</h1><p>" + ruta + "</p>";
                    byte[] contenido = mensaje.getBytes();
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(404, contenido.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(contenido);
                    os.close();
                }
            }
        });

        servidor.setExecutor(null);
        servidor.start();

        System.out.println("Servidor corriendo en http://localhost:" + puerto);
    }

    // Determina el tipo de contenido según la extensión del archivo
    private static String obtenerTipo(String ruta) {
        if (ruta.endsWith(".html")) return "text/html; charset=UTF-8";
        if (ruta.endsWith(".css")) return "text/css; charset=UTF-8";
        if (ruta.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (ruta.endsWith(".png")) return "image/png";
        if (ruta.endsWith(".jpg") || ruta.endsWith(".jpeg")) return "image/jpeg";
        if (ruta.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}