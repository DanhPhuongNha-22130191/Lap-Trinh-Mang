package rmi_shop_manager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] args) throws RemoteException {
		Registry reg = LocateRegistry.createRegistry(8769);
		reg.rebind("MANAGER", new RemoteDao());
	}

}
