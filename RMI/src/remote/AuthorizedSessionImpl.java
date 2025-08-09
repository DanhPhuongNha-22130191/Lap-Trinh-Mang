package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;

public class AuthorizedSessionImpl extends UnicastRemoteObject implements IAuthorizedSession {
	private Connection connection;

	public AuthorizedSessionImpl(Connection connection) throws RemoteException {
		super();
		this.connection = connection;
	}

	@Override
	public List<Student> findById(int sid) throws RemoteException {
		try {
			String sql = "SELECT * FROM Student WHERE sid=?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, sid);
			ResultSet rs = stmt.executeQuery();
			List<Student> list = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("sid");
				String name = rs.getString("name");
				double grade = rs.getDouble("grade");
				list.add(new Student(id, name, grade));
			}
			return list;
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public List<Student> findByName(String partOfName) throws RemoteException {
		try {
			String sql = "SELECT * FROM Student WHERE name LIKE?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, "%" + partOfName);
			ResultSet rs = stmt.executeQuery();
			List<Student> list = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("sid");
				String name = rs.getString("name");
				double grade = rs.getDouble("grade");
				list.add(new Student(id, name, grade));
			}
			return list;
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public void closeDB() throws RemoteException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}

	}

}
