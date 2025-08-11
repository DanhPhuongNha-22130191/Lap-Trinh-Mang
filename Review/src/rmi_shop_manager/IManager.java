package rmi_shop_manager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IManager extends Remote{
	String getGreeting() throws RemoteException;
	boolean checkUserName(String username) throws RemoteException;
	int login(String username, String password) throws RemoteException;
	boolean add(int sessionId, Product product) throws RemoteException;
	int remove(int sessionId, List<Integer> ids) throws RemoteException;
	boolean update(int sessionId, Product product) throws RemoteException;
	List<Product> view(int sessionId, String partName) throws RemoteException;
}
