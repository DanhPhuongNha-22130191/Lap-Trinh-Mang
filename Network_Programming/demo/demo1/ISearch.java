package demo1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ISearch extends Remote {
	String getGreeting() throws RemoteException;

	boolean checkUserName(String username) throws RemoteException;

	boolean login(String username, String password) throws RemoteException;

	List<Student> findById(int sid) throws RemoteException;

	List<Student> findByName(String partOfName) throws RemoteException;

}
