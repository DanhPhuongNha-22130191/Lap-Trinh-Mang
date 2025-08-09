package rmi_shop;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IProduct extends Remote{
	String getBanner() throws RemoteException;
	boolean checkUserName(String username) throws RemoteException;
	int login(String username,String password) throws RemoteException;
	List<Product> findById(int session,int id) throws RemoteException;
	List<Product> findByName(int session,String partOfName) throws RemoteException;
	void logout(int session) throws RemoteException;
	void dbClose() throws RemoteException;
}
