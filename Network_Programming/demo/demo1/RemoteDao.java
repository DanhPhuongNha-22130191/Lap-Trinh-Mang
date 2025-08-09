package demo1;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class RemoteDao extends UnicastRemoteObject implements ISearch{
	private Dao dao;
	public RemoteDao() throws RemoteException {
		try {
			dao = new Dao();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkUserName(String username) throws RemoteException {
		try {
			return dao.checkUserName(username);
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public boolean login(String username, String password) throws RemoteException {
		try {
			return dao.login(username,password);
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public List<Student> findById(int sid) throws RemoteException {
		try {
			return dao.findById(sid);
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public List<Student> findByName(String partOfName) throws RemoteException {
		try {
			return dao.findByName(partOfName);
		} catch (SQLException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public String getGreeting() {
		return "Welcome...";
	}
}
