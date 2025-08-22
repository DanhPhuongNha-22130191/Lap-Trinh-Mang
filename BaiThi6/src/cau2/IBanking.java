package cau2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IBanking extends Remote {
	String getBanner() throws RemoteException;

	boolean checkUserName(String username) throws RemoteException;

	int login(String username, String password) throws RemoteException;

	boolean deposit(int sessionId, double amount)throws RemoteException;

	boolean withDraw(int sessionId, double amount)throws RemoteException;

	double balance(int sessionId)throws RemoteException;

	List<Note> note(int sessionId)throws RemoteException;
}
