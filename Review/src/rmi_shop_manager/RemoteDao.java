package rmi_shop_manager;

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
	private final String DB_PATH = "D:\\Programmings\\Eclipse\\Lap-Trinh-Mang\\Review\\src\\Databases.accdb";
	private final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;
	private final Set<Integer> activeSessions = new HashSet();
	private int sessionId_auto = 0;

	public RemoteDao() throws RemoteException {
		super();
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getGreeting() throws RemoteException {
		return "WELCOME TO MANAGER PRODUCT SYSTEM";
	}

	@Override
	public boolean checkUserName(String username) throws RemoteException {
		String sql = "SELECT * FROM user WHERE username=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public int login(String username, String password) throws RemoteException {
		String sql = "SELECT * FROM user WHERE username=? AND password=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int sessionId = ++sessionId_auto;
					activeSessions.add(sessionId);
					return sessionId;
				} else {
					return -1;
				}
			}
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	public boolean isValidSessionId(int sessionId) {
		return activeSessions.contains(sessionId);
	}

	@Override
	public boolean add(int sessionId, Product product) throws RemoteException {
		if (!isValidSessionId(sessionId))
			throw new RemoteException("Invalid session ID!");
		String sql = "INSERT INTO sanpham (idsp,name,count,price) VALUES (?,?,?,?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, product.getProductId());
			stmt.setString(2, product.getName());
			stmt.setInt(3, product.getCount());
			stmt.setDouble(4, product.getPrice());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public int remove(int sessionId, List<Integer> ids) throws RemoteException {
		if (!isValidSessionId(sessionId))
			throw new RemoteException("Invalid session ID!");
		int totalRowRemoved = 0;
		for (Integer id : ids) {
			if (checkIdProductExist(id)) {
				String sql = "DELETE FROM sanpham WHERE idsp=?";
				try (PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.setInt(1, id);
					totalRowRemoved += stmt.executeUpdate();
				} catch (SQLException e) {
					throw new RemoteException(e.getMessage());
				}
			}
		}
		return totalRowRemoved;
	}

	private boolean checkIdProductExist(int id) throws RemoteException {
		String sql = "SELECT 1 FROM sanpham WHERE idsp=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public boolean update(int sessionId, Product product) throws RemoteException {
		if (!isValidSessionId(sessionId))
			throw new RemoteException("Invalid session ID!");
		String sql = "UPDATE sanpham SET name=?, count=?, price=? WHERE idsp=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, product.getName());
			stmt.setInt(2, product.getCount());
			stmt.setDouble(3, product.getPrice());
			stmt.setInt(4, product.getProductId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public List<Product> view(int sessionId, String partName) throws RemoteException {
		if (!isValidSessionId(sessionId))
			throw new RemoteException("Invalid session ID!");
		List<Product> list = new ArrayList<Product>();
		String sql = "SELECT * FROM sanpham WHERE name LIKE ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, "%"+partName+"%");
			try(ResultSet rs = stmt.executeQuery()){
				while(rs.next()) {
					int id = rs.getInt("idsp");
					String name = rs.getString("name");
					int count = rs.getInt("count");
					double price = rs.getDouble("price");
					list.add(new Product(id, name, count, price));
				}
			}
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
		return list;
	}

}
