package cau2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dao {
    private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String DB_PATH = "E:\\Thi\\Database1.accdb";
    private static final String URL = "jdbc:ucanaccess://" + DB_PATH;

    private static final String TABLE_USER = "user";
    private static final String TABLE_PRODUCT = "sanpham";

    private Connection connection;

    public Dao() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }
    }

    public boolean checkUserName(String username) {
        String sql = "SELECT 1 FROM " + TABLE_USER + " WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking username:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        String sql = "SELECT 1 FROM " + TABLE_USER + " WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Login failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkIdExist(int id) {
        String sql = "SELECT 1 FROM " + TABLE_PRODUCT + " WHERE idsp = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking product ID:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean add(Product product) {
        if (checkIdExist(product.getId())) return false;

        String sql = "INSERT INTO " + TABLE_PRODUCT + " (idsp, name, [count], price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setInt(3, product.getCount());
            stmt.setDouble(4, product.getPrice());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Product product) {
        if (!checkIdExist(product.getId())) return false;

        String sql = "UPDATE " + TABLE_PRODUCT + " SET name = ?, [count] = ?, price = ? WHERE idsp = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCount());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product:");
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> view(String partName) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_PRODUCT + " WHERE name LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + partName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                        rs.getInt("idsp"),
                        rs.getString("name"),
                        rs.getInt("count"),
                        rs.getDouble("price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing products:");
            e.printStackTrace();
        }
        return products;
    }

    public int remove(List<Integer> ids) {
        int totalRemoved = 0;
        String sql = "DELETE FROM " + TABLE_PRODUCT + " WHERE idsp = ?";
        for (int id : ids) {
            if (checkIdExist(id)) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    totalRemoved += stmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Error removing product with ID " + id + ":");
                    e.printStackTrace();
                }
            }
        }
        return totalRemoved;
    }

    public void debugSanpham() {
        String sql = "SELECT * FROM " + TABLE_PRODUCT;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    System.out.print(meta.getColumnName(i) + "=" + rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Error debugging product table:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Dao dao = new Dao();
        for (Product p : dao.view("e")) {
            System.out.println(p);
        }
    }
}
