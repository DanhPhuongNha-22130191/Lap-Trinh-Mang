package tcp_shop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Dao {
	private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private static final String DB_PATH = "D:\\Programmings\\Eclipse\\Lap-Trinh-Mang\\Review\\Product.accdb";
	private static final String URL = "jdbc:ucanaccess://" + DB_PATH;

	private Connection connection;

	public Dao() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		connection = DriverManager.getConnection(URL);
	}

	public boolean checkUserName(String username) throws SQLException {
		String sql = "SELECT * FROM	User WHERE username=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, username);
		try (ResultSet rs = stmt.executeQuery()) {
			return rs.next();
		}
	}

	public boolean login(String username, String password) throws SQLException {
		String sql = "SELECT * FROM	User WHERE username=?AND password=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	public List<Product> findById(int id) throws SQLException {
		String sql = "SELECT * FROM	Product WHERE id=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, id);
			List<Product> list = new ArrayList<>();
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToProduct(rs));
				}
				return list;
			}

		}
	}

	public List<Product> findByName(String partOfName) throws SQLException {
		String sql = "SELECT * FROM	Product WHERE name LIKE?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, "%" + partOfName + "%");
			List<Product> list = new ArrayList<>();
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToProduct(rs));
				}
				return list;
			}

		}
	}

	private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
		int productId = rs.getInt("id");
		String name = rs.getString("name");
		int count = rs.getInt("count");
		double price = rs.getDouble("price");
		return new Product(productId, name, count, price);
	}

	public void dbClose() throws SQLException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

}
