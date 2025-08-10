package tcp_shop_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Dao {
	private final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private final String DB_PATH = "D:\\Programmings\\Eclipse\\Lap-Trinh-Mang\\Review\\src\\SanPham.accdb";
	private final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;

	public Dao() throws SQLException, ClassNotFoundException {
		Class.forName(DRIVER);
		connection = DriverManager.getConnection(URL);
	}

	public boolean checkUserName(String username) {
		String sql = "SELECT * FROM User WHERE username=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean login(String username, String password) {
		String sql = "SELECT * FROM User WHERE username=? AND password=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkProductIdExists(int productId) {
		String sql = "SELECT 1 FROM SanPham WHERE idsp=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, productId);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addProduct(int productId, String name, int count, double price) {
		if (checkProductIdExists(productId))
			return false;
		String sql = "INSERT INTO SanPham (idsp, ten_san_pham, so_luong, gia_ban) VALUES(?,?,?,?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, productId);
			stmt.setString(2, name);
			stmt.setInt(3, count);
			stmt.setDouble(4, price);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int removeProducts(List<Integer> listProductId) {
		int totalDeleted = 0;
		for (Integer productId : listProductId) {
			if (checkProductIdExists(productId)) {
				String sql = "DELETE FROM SanPham WHERE idsp =?";
				try (PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.setInt(1, productId);
					totalDeleted += stmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return totalDeleted;
	}

	public boolean editProduct(int productId, String name, int count, double price) {
		String sql = "UPDATE SanPham SET ten_san_pham=?, so_luong=?, gia_ban=? WHERE idsp=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(4, productId);
			stmt.setString(1, name);
			stmt.setInt(2, count);
			stmt.setDouble(3, price);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Product> viewProducts(String partOfName) {
		String sql = "SELECT * FROM SanPham WHERE ten_san_pham LIKE ?";
		List<Product> list = new ArrayList<Product>();
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, "%" + partOfName + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int productId = rs.getInt("idsp");
					String name = rs.getString("ten_san_pham");
					int count = rs.getInt("so_luong");
					double price = rs.getDouble("gia_ban");
					list.add(new Product(productId, name, count, price));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
