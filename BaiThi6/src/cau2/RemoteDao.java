package cau2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class RemoteDao extends UnicastRemoteObject implements IBanking {
	private final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private final String DB_PATH = "E:\\Thi\\BaiThi6\\src\\Database1.accdb";
	private final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;
	private final HashMap<Integer, Integer> sessions = new HashMap<Integer, Integer>();
	private int sessionId_auto = 0;

	public RemoteDao() throws RemoteException {
		super();
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL);
		} catch (ClassNotFoundException | SQLException e) {
			throw new RemoteException("Server error while connecting to DB " + e.getMessage());
		}

	}

	@Override
	public String getBanner() throws RemoteException {
		return "WELCOME TO NLU E-BANKING..";
	}

	@Override
	public boolean checkUserName(String username) throws RemoteException {
		String sql = "SELECT * FROM account where username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RemoteException("Server error while checking username in DB " + e.getMessage());
		}
	}

	@Override
	public int login(String username, String password) throws RemoteException {
		String sql = "SELECT * FROM account where username = ? AND password = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int sessionId = sessionId_auto++;
					int accountId = rs.getInt("accountId");
					sessions.put(sessionId, accountId);
					return sessionId;
				} else
					return -1;
			}
		} catch (SQLException e) {
			throw new RemoteException("Server error while login in DB " + e.getMessage());
		}
	}

	public boolean loggedIn(int sessionId) {
		return sessions.containsKey(sessionId);
	}

	@Override
	public boolean deposit(int sessionId, double amount) throws RemoteException {
		if (!loggedIn(sessionId) || amount < 0)
			return false;
		int accountId = sessions.get(sessionId);
		String sql = "UPDATE account SET balance = balance + ? WHERE accountId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setDouble(1, amount);
			stmt.setInt(2, accountId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RemoteException("Server error while login in DB " + e.getMessage());
		}
	}

	@Override
	public boolean withDraw(int sessionId, double amount) throws RemoteException {
		if (!loggedIn(sessionId) || amount < 0)
			return false;
		int accountId = sessions.get(sessionId);
		String sql = "UPDATE account SET balance = balance - ? WHERE accountId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setDouble(1, amount);
			stmt.setInt(2, accountId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RemoteException("Server error while login in DB " + e.getMessage());
		}
	}

	@Override
	public double balance(int sessionId) throws RemoteException {
		if (!loggedIn(sessionId))
			throw new RemoteException("Session ID invalid");
		int accountId = sessions.get(sessionId);
		String sql = "SELECT * FROM account WHERE accountId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, accountId);
			try(ResultSet rs = stmt.executeQuery()){
				if(rs.next()) {
					double balance = rs.getDouble("balance");
					return balance;
				}else {
					throw new RemoteException("Account not found");
				}
			}
		} catch (SQLException e) {
			throw new RemoteException("Server error while login in DB " + e.getMessage());
		}
	}

	@Override
	public List<Note> note(int sessionId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
