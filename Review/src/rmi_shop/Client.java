package rmi_shop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.StringTokenizer;

public class Client {
	private IProduct server;
	private BufferedReader userInput;
	private boolean isLoggedIn = false;
	private String command;
	private String parameter;
	private String pendingUsername = null;
	private int sessionId = -1;

	public Client() throws RemoteException, NotBoundException {
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
				String response = processCommand();
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

	private String processCommand() throws RemoteException {
		return isLoggedIn ? executeCommand() : handleLogin();
	}

	private String handleLogin() throws RemoteException {
		switch (command) {
		case "TEN":
			pendingUsername = parameter;
			return validateUsername(pendingUsername);
		case "MATKHAU":
			return authenticateUser(pendingUsername, parameter);
		default:
			return "ERR: You must log in before issuing commands.";
		}
	}

	private String validateUsername(String username) throws RemoteException {
		if (server.checkUserName(username)) {
			return "OK: Username is valid.";
		} else {
			return "ERR: Username is invalid.";
		}
	}

	private String authenticateUser(String username, String password) throws RemoteException {
		if (username == null || username.isEmpty()) {
			return "ERR: Username cannot be null or empty.";
		}
		int sid = server.login(username, password);
		if (sid != -1) {
			isLoggedIn = true;
			sessionId = sid;
			return "OK: Login successful.";
		} else {
			return "ERR: Incorrect password.";
		}
	}

	private String executeCommand() throws NumberFormatException, RemoteException {
		switch (command) {
		case "MA":
			return findProductById(sessionId, Integer.parseInt(parameter));
		case "TEN":
			return findProductByName(sessionId, parameter);
		case "QUIT":
			return logout(sessionId);
		default:
			return "ERR: Command invalid!";
		}
	}

	private String logout(int sessionId) throws RemoteException {
		server.logout(sessionId);
		return "Logout successfully!";
	}

	private String findProductByName(int sessionId, String partOfName) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		List<Product> list = server.findByName(sessionId, partOfName);
		if (list == null || list.isEmpty())
			return "ERR: not found!";
		for (Product product : list) {
			sb.append(product + "\t");
		}
		return sb.toString();
	}

	private String findProductById(int sessionId, int productId) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		List<Product> list = server.findById(sessionId, productId);
		if (list == null || list.isEmpty())
			return "ERR: not found!";
		for (Product product : list) {
			sb.append(product + "\t");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		try {
			new Client().exec();
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Failed to start client: " + e.getMessage());
		}
	}
}
