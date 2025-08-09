package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Student;

public interface IAuthorizedSession extends Remote {
	List<Student> findById(int sid) throws RemoteException;

	List<Student> findByName(String partOfName) throws RemoteException;

	void closeDB() throws RemoteException;
}
