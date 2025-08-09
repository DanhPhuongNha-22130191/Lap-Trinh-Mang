package rmi_shop;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] args) throws RemoteException  {
		Registry reg = LocateRegistry.createRegistry(5918);
		reg.rebind("SEARCH", new RemoteDao());
	}

}
