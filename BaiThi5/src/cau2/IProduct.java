package cau2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IProduct extends Remote {
	String getBanner() throws RemoteException;

	boolean checkUserName(String username) throws RemoteException;

	int login(String username, String password) throws RemoteException;

	List<Product> findById(int sessionId, int idsp) throws RemoteException;

	List<Product> findByName(int sessionId, String name) throws RemoteException;

//	String buy(int sessionId, List<Integer> idsp) throws RemoteException;
}
