package tcp_shop;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

public class OneThread extends Thread {
    private final Socket socket;
    private final BufferedReader netIn;
    private final PrintWriter netOut;
    private final Dao dao;
    private String command;
    private String parameter;
    private String lastUserName = null;
    private boolean isLogin = false;

    public OneThread(Socket socket) throws IOException, SQLException, ClassNotFoundException {
        this.socket = socket;
        String charset = "UTF-8";
        netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
        netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
        dao = new Dao();
    }

    @Override
    public void run() {
        try {
            netOut.println("Xin chào mừng..");
            String line;
            while ((line = netIn.readLine()) != null) {
                if ("QUIT".equalsIgnoreCase(line)) break;
                analyze(line);
                String response = processCommand();
                netOut.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void analyze(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, "\t");
        command = tokenizer.hasMoreTokens() ? tokenizer.nextToken().toUpperCase() : "";
        parameter = line.length() > command.length() ? line.substring(command.length()).trim() : "";
    }

    private String processCommand() throws SQLException {
        return isLogin ? executeCommand() : handleLogin();
    }

    private String handleLogin() throws SQLException {
        switch (command) {
            case "TEN":
                if (parameter.contains(" ")) {
                    return "ERR: You must login first before using TEN to search products.";
                }
                return validateUserName(parameter);
            case "MATKHAU":
                return authenticateUser(lastUserName, parameter);
            default:
                return "ERR: You have to login first (TEN <username> and MATKHAU <password>)";
        }
    }

    private String validateUserName(String username) throws SQLException {
        if (username == null || username.isEmpty()) {
            return "ERR: Username cannot be empty!";
        }
        if (dao.checkUserName(username)) {
            lastUserName = username;
            return "OK valid username!";
        }
        return "ERR invalid username!";
    }

    private String authenticateUser(String username, String password) throws SQLException {
        if (username == null) return "ERR username cannot be null!";
        if (dao.login(username, password)) {
            isLogin = true;
            return "OK login successfully!";
        }
        return "ERR incorrect password!";
    }

    private String executeCommand() throws SQLException {
        switch (command) {
            case "MA":
                return findProductById(Integer.parseInt(parameter));
            case "TEN":
                return findProductByName(parameter);
            case "MUA":
                return buyProducts();
            default:
                return "Invalid command!";
        }
    }

    private String findProductById(int productId) throws SQLException {
        List<Product> products = dao.findById(productId);
        return formatProductList(products);
    }

    private String findProductByName(String namePart) throws SQLException {
        List<Product> products = dao.findByName(namePart);
        return formatProductList(products);
    }

    private String buyProducts() {
        // Placeholder for future implementation
        return "Purchase feature not implemented yet.\n.";
    }

    private String formatProductList(List<Product> products) {
        if (products == null && products.isEmpty()) return "Product not found!";
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append(product).append("\n");
        }
        sb.append(".\r\n");
        return sb.toString();
    }
}
