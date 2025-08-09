package demo1;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] args) throws IOException {
		Registry reg = LocateRegistry.createRegistry(12345);
		reg.rebind("SEARCH", new RemoteDao());
	}
}
