package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchImpl extends UnicastRemoteObject implements ISearch {
	private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private static final String DB_PATH = "D:\\Programmings\\Eclipse\\Lap-Trinh-Mang\\TCP\\TCP.accdb";;
	private static final String URL = "jdbc:ucanaccess://" + DB_PATH;

	public SearchImpl() throws RemoteException, ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
	}

	@Override
	public String getGreeting() throws RemoteException {
		return "Welcome to RMI Server!";
	}

	@Override
	public IAuthorizedSession login(String username, String password) throws RemoteException {
		try {
			Connection connection = DriverManager.getConnection(URL);
			String sql = "SELECT * FROM User WHERE username=? AND password=?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				return new AuthorizedSessionImpl(connection);
			else {
				connection.close();
				return null;
			}
		} catch (Exception e) {
			throw new RemoteException("Login failed " + e.getMessage());
		}
	}

	@Override
	public boolean checkUserName(String username) throws RemoteException {
		try {
			Connection connection = DriverManager.getConnection(URL);
			String sql = "SELECT * FROM User WHERE username=? ";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (Exception e) {
			throw new RemoteException("Username not exist " + e.getMessage());
		}
	}
}
