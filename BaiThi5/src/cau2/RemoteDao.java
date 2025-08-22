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

public class RemoteDao extends UnicastRemoteObject implements IProduct {
	private final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private final String DB_PATH = "E:\\Thi\\BaiThi5\\src\\Database1.accdb";
	private final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;
	private final Set<Integer> sessions = new HashSet<>();
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
	public String getBanner() throws RemoteException {
		return "Xin chào mừng...";
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
			throw new RemoteException("ERR while checking username in DB " + e.getMessage());
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
					sessions.add(sessionId);
					return sessionId;
				} else
					return -1;
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while login in DB " + e.getMessage());
		}
	}

	public boolean loggedIn(int sessionId) {
		return sessions.contains(sessionId);
	}

	@Override
	public List<Product> findById(int sessionId, int idsp) throws RemoteException {
		if (!loggedIn(sessionId))
			throw new RemoteException("ERR sessionId invalid!");
		String sql = "SELECT * FROM sanpham WHERE idsp = ?";
		List<Product> list = new ArrayList<Product>();
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, idsp);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int id = rs.getInt("idsp");
					String name = rs.getString("name");
					int count = rs.getInt("count");
					double price = rs.getDouble("price");
					list.add(new Product(id, name, count, price));
				}
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while find product by id in DB " + e.getMessage());
		}
		return list;
	}

	@Override
	public List<Product> findByName(int sessionId, String name) throws RemoteException {
		if (!loggedIn(sessionId))
			throw new RemoteException("ERR sessionId invalid!");
		String sql = "SELECT * FROM sanpham WHERE name LIKE ?";
		List<Product> list = new ArrayList<Product>();
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, "%" + name + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int id = rs.getInt("idsp");
					String productName = rs.getString("name");
					int count = rs.getInt("count");
					double price = rs.getDouble("price");
					list.add(new Product(id, productName, count, price));
				}
			}
		} catch (SQLException e) {
			throw new RemoteException("ERR while find product by id in DB " + e.getMessage());
		}
		return list;
	}

//	@Override
//	public boolean buy(int sessionId, List<Integer> idsp) throws RemoteException {
//		if (!loggedIn(sessionId))
//			throw new RemoteException("ERR sessionId invalid!");
//		for (Integer id : idsp) {
//			if (productIdExist(id)) {
//				String sql = "SELECT 1 FROM  sanpham where idsp = ? ";
//				try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//					stmt.setInt(1, idsp);
//					try (ResultSet rs = stmt.executeQuery()) {
//						return rs.next();
//					}
//				} catch (SQLException e) {
//					throw new RemoteException("ERR while find product by id in DB " + e.getMessage());
//				}
//			}else return "Id product not exist!"+id;
//		}
//	}
//
//	private boolean productIdExist(int idsp) throws RemoteException {
//		String sql = "SELECT 1 FROM  sanpham where idsp = ? ";
//		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//			stmt.setInt(1, idsp);
//			try (ResultSet rs = stmt.executeQuery()) {
//				return rs.next();
//			}
//		} catch (SQLException e) {
//			throw new RemoteException("ERR while find product by id in DB " + e.getMessage());
//		}
//	}

}
