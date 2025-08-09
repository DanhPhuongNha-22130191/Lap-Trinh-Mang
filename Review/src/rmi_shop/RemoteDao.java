package rmi_shop;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

public class RemoteDao extends UnicastRemoteObject implements IProduct {

    private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String DB_PATH = "D:\\Programmings\\Eclipse\\Lap-Trinh-Mang\\Review\\Product.accdb";
    private static final String URL = "jdbc:ucanaccess://" + DB_PATH;

    private Connection connection;
    private final Map<Integer, String> sessionMap = new HashMap<>();
    private final Random random = new Random();

    protected RemoteDao() throws RemoteException {
        super();
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RemoteException("Database connection failed", e);
        }
    }

    @Override
    public String getBanner() throws RemoteException {
        return "Xin chào mừng..";
    }

    @Override
    public boolean checkUserName(String username) throws RemoteException {
        String sql = "SELECT 1 FROM User WHERE username=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RemoteException("Error checking username", e);
        }
    }

    @Override
    public int login(String username, String password) throws RemoteException {
        String sql = "SELECT 1 FROM User WHERE username=? AND password=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int sessionId;
                    do {
                        sessionId = random.nextInt(100000);
                    } while (sessionMap.containsKey(sessionId));
                    sessionMap.put(sessionId, username);
                    return sessionId;
                }
                return -1;
            }
        } catch (SQLException e) {
            throw new RemoteException("Login failed", e);
        }
    }

    @Override
    public List<Product> findById(int sessionId, int id) throws RemoteException {
        verifySession(sessionId);
        String sql = "SELECT * FROM Product WHERE id=?";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Error finding product by ID", e);
        }
        return products;
    }

    @Override
    public List<Product> findByName(int sessionId, String partOfName) throws RemoteException {
        verifySession(sessionId);
        String sql = "SELECT * FROM Product WHERE name LIKE ?";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + partOfName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Error finding product by name", e);
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        int productID = rs.getInt("id");
        String name = rs.getString("name");
        int count = rs.getInt("count");
        double price = rs.getDouble("price");
        return new Product(productID, name, count, price);
    }

    private void verifySession(int sessionId) throws RemoteException {
        if (!sessionMap.containsKey(sessionId)) {
            throw new RemoteException("ERR: Session is invalid or expired!");
        }
    }

    @Override
    public void dbClose() throws RemoteException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RemoteException("Error closing database", e);
        }
    }

    @Override
    public void logout(int sessionId) throws RemoteException {
        sessionMap.remove(sessionId);
    }
}
