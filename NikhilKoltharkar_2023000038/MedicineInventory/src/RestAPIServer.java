import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RestAPIServer {
    
    private static MedicineDAO dao = new MedicineDAO();
    private static Gson gson = new Gson();
    
    public static void main(String[] args) throws IOException {
        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // API endpoints
        server.createContext("/api/medicines", new GetAllMedicinesHandler());
        server.createContext("/api/medicine/add", new AddMedicineHandler());
        server.createContext("/api/medicine/update", new UpdateMedicineHandler());
        server.createContext("/api/medicine/delete", new DeleteMedicineHandler());
        server.createContext("/api/medicine/search", new SearchMedicineHandler());
        
        // Serve static files (HTML, CSS, JS)
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null); // Use default executor
        server.start();
        
        System.out.println("========================================");
        System.out.println("  Server started successfully!");
        System.out.println("  Open browser and go to:");
        System.out.println("  http://localhost:8081");
        System.out.println("========================================");
    }
    
    // Handler to get all medicines
    static class GetAllMedicinesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Medicine> medicines = dao.getAllMedicines();
                String jsonResponse = gson.toJson(medicines);
                
                sendResponse(exchange, 200, jsonResponse);
            }
        }
    }
    
    // Handler to add medicine
    static class AddMedicineHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read request body
                String requestBody = readRequestBody(exchange);
                
                // Parse JSON to Map
                Map<String, String> medicineData = gson.fromJson(requestBody, Map.class);
                
                // Create Medicine object
                Medicine medicine = new Medicine(
                    medicineData.get("medicineName"),
                    medicineData.get("manufacturer"),
                    Integer.parseInt(medicineData.get("quantity")),
                    Double.parseDouble(medicineData.get("price")),
                    Date.valueOf(medicineData.get("expiryDate")),
                    medicineData.get("category")
                );
                
                boolean success = dao.addMedicine(medicine);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", success);
                response.put("message", success ? "Medicine added successfully!" : "Failed to add medicine");
                
                sendResponse(exchange, 200, gson.toJson(response));
            }
        }
    }
    
    // Handler to update medicine
    static class UpdateMedicineHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);
            
            if ("PUT".equals(exchange.getRequestMethod())) {
                String requestBody = readRequestBody(exchange);
                Map<String, String> medicineData = gson.fromJson(requestBody, Map.class);
                
                Medicine medicine = new Medicine(
                    Integer.parseInt(medicineData.get("medicineId")),
                    medicineData.get("medicineName"),
                    medicineData.get("manufacturer"),
                    Integer.parseInt(medicineData.get("quantity")),
                    Double.parseDouble(medicineData.get("price")),
                    Date.valueOf(medicineData.get("expiryDate")),
                    medicineData.get("category")
                );
                
                boolean success = dao.updateMedicine(medicine);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", success);
                response.put("message", success ? "Medicine updated successfully!" : "Failed to update medicine");
                
                sendResponse(exchange, 200, gson.toJson(response));
            }
        }
    }
    
    // Handler to delete medicine
    static class DeleteMedicineHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);
            
            if ("DELETE".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int medicineId = Integer.parseInt(query.split("=")[1]);
                
                boolean success = dao.deleteMedicine(medicineId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", success);
                response.put("message", success ? "Medicine deleted successfully!" : "Failed to delete medicine");
                
                sendResponse(exchange, 200, gson.toJson(response));
            }
        }
    }
    
    // Handler to search medicine
    static class SearchMedicineHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String searchTerm = query.split("=")[1];
                
                List<Medicine> medicines = dao.searchMedicineByName(searchTerm);
                String jsonResponse = gson.toJson(medicines);
                
                sendResponse(exchange, 200, jsonResponse);
            }
        }
    }
    
    // Handler to serve static files (HTML, CSS, JS)
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            File file = new File("frontend" + path);
            
            if (file.exists() && !file.isDirectory()) {
                String contentType = getContentType(path);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                
                byte[] bytes = readFileBytes(file);
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                String response = "404 - File Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    
    // Helper method to read request body
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    // Helper method to send response
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    // Helper method to set CORS headers
    private static void setCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
    
    // Helper method to get content type
    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        return "text/plain";
    }
    
    // Helper method to read file bytes
    private static byte[] readFileBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }
}