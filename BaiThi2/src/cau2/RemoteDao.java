package cau2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoteDao extends UnicastRemoteObject implements IManager {
	private final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private final String DB_PATH = "E:\\Thi\\BaiThi2\\Database1.accdb";
	private final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;
	private int sessionId_auto = 0;
	private final Set<Integer> sessionIds = new HashSet<Integer>();

	public RemoteDao() throws RemoteException {
		super();
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL);
		} catch (ClassNotFoundException | SQLException e) {
			throw new RemoteException("Failed while connect DB " + e.getMessage());
		}
	}

	@Override
	public String getGreeting() throws RemoteException {
		return "WELCOME TO MANAGER PRODUCT SYSTEM";
	}

	@Override
	public boolean checkUserName(String username) throws RemoteException {
		String sql = "SELECT * FROM user WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while checking username DB " + e.getMessage());
		}
	}

	@Override
	public int login(String username, String password) throws RemoteException {
		String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int sessionId = sessionId_auto++;
					sessionIds.add(sessionId);
					return sessionId;
				} else
					return -1;
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while login DB " + e.getMessage());
		}
	}

	public boolean isLogin(int sessionId) {
		return sessionIds.contains(sessionId);
	}

	public boolean isExistProduct(int idsp) throws RemoteException {
		String sql = "SELECT 1 FROM sanpham WHERE idsp = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, idsp);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while check product exist DB " + e.getMessage());
		}
	}

	@Override
	public boolean add(int sessionId, Product product) throws RemoteException {
		if (!isLogin(sessionId))
			return false;
		if (isExistProduct(product.getIdsp()))
			return false;
		String sql = "INSERT INTO sanpham (idsp,name,price,count) VALUES 	(?,?,?,?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, product.getIdsp());
			stmt.setString(2, product.getName());
			stmt.setInt(3, product.getCount());
			stmt.setDouble(4, product.getPrice());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RemoteException("ERR while add product DB " + e.getMessage());
		}
	}

	@Override
	public boolean update(int sessionId, Product product) throws RemoteException {
		if (!isLogin(sessionId))
			return false;
		if (!isExistProduct(product.getIdsp()))
			return false;
		String sql = "UPDATE sanpham SET name = ?, count = ?, price = ? WHERE idsp = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(4, product.getIdsp());
			stmt.setString(1, product.getName());
			stmt.setInt(2, product.getCount());
			stmt.setDouble(3, product.getPrice());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RemoteException("ERR while update product DB " + e.getMessage());
		}
	}

	@Override
	public int remove(int sessionId, List<Integer> listId) throws RemoteException {
		int totalRowRemoved = 0;
		if (!isLogin(sessionId))
			throw new RemoteException("ERR sessionId invalid!");
		for (Integer id : listId) {
			if (isExistProduct(id)) {
				String sql = "DELETE FROM sanpham where idsp = ?";
				try (PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.setInt(1, id);
					totalRowRemoved += stmt.executeUpdate();
				} catch (SQLException e) {
					throw new RemoteException("ERR while update product DB " + e.getMessage());
				}
			}
		}
		return totalRowRemoved;
	}

	@Override
	public List<Product> view(int sessionId, String partName) throws RemoteException {
		List<Product> list = new ArrayList<Product>();
		if (!isLogin(sessionId))
			throw new RemoteException("ERR sessionId invalid!");
		String sql = "SELECT * FROM sanpham WHERE name LIKE ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, "%" + partName + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int idsp = rs.getInt("idsp");
					String name = rs.getString("name");
					int count = rs.getInt("count");
					double price = rs.getDouble("price");
					list.add(new Product(idsp, name, count, price));
				}
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while login DB " + e.getMessage());
		}
		return list;
	}
}
