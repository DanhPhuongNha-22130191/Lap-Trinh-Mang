package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISearch extends Remote {
	String getGreeting() throws RemoteException;

	boolean checkUserName(String username) throws RemoteException;

	IAuthorizedSession login(String username, String password) throws RemoteException;
}
