package rmi_shop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.StringTokenizer;

public class ClientBypassLogin {
	private IProduct server;
	private BufferedReader userInput;
	private String command;
	private String parameter;
	private final int sessionId = 12345; // Gán sẵn token/sessionId để test trực tiếp

	public ClientBypassLogin() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", 5918);
		server = (IProduct) registry.lookup("SEARCH");
		userInput = new BufferedReader(new InputStreamReader(System.in));
	}

	public void exec() {
		try {
			System.out.println(server.getBanner());
			String inputLine;
			while (true) {
				inputLine = userInput.readLine();
				if ("QUIT".equalsIgnoreCase(inputLine))
					break;

				parseInput(inputLine);
				String response = executeCommand();
				System.out.println(response);
			}
		} catch (Exception e) {
			System.err.println("Client encountered an error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void parseInput(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		command = tokenizer.hasMoreTokens() ? tokenizer.nextToken().toUpperCase() : "";
		parameter = line.length() > command.length() ? line.substring(command.length()).trim() : "";
	}

	private String executeCommand() throws RemoteException {
		switch (command) {
		case "MA":
			return findProductById(Integer.parseInt(parameter));
		case "TEN":
			return findProductByName(parameter);
		case "QUIT":return
			logout();
		default:
			return "ERR: Command invalid!";
		}
	}

	private String logout() throws RemoteException {
		server.logout(sessionId);
		server.dbClose();
		return "OK: logout successfully!";

	}

	private String findProductByName(String partOfName) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		List<Product> list = server.findByName(sessionId, partOfName);
		if (list == null || list.isEmpty())
			return "ERR: not found!";
		for (Product product : list) {
			sb.append(product).append("\t");
		}
		return sb.toString();
	}

	private String findProductById(int productId) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		List<Product> list = server.findById(sessionId, productId);
		if (list == null || list.isEmpty())
			return "ERR: not found!";
		for (Product product : list) {
			sb.append(product).append("\t");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		try {
			new ClientBypassLogin().exec();
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Failed to start client: " + e.getMessage());
		}
	}
}
