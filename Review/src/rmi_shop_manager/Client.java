package rmi_shop_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Client {
	private BufferedReader userIn;
	private IManager server;
	private String command;
	private List<String> parameters;
	private int sessionId = -1;
	private boolean isLoggedIn = false;
	private String lastUserName = null;

	public Client() throws RemoteException, NotBoundException {
		userIn = new BufferedReader(new InputStreamReader(System.in));
		Registry reg = LocateRegistry.getRegistry("127.0.0.1", 8769);
		server = (IManager) reg.lookup("MANAGER");
		parameters = new ArrayList<String>();
	}

	public void execute() throws RemoteException {
		System.out.println(server.getGreeting());
		String line, res;
		while (true) {
			try {
				line = userIn.readLine();
				if ("QUIT".equalsIgnoreCase(line))
					break;
				analyze(line);
				res = processCommand();
				System.out.println(res);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String processCommand() {
		return isLoggedIn ? executeCommand() : handleLogin();
	}

	private String handleLogin() {
		switch (command) {
		case "USER":
			return validateUserName();
		case "PASS":
			return login();
		default:
			return "Invalid command!";
		}
	}

	private String login() {
		if (parameters.isEmpty()) {
			return "Invalid format. Expected: PASS <tab> <password>";
		}
		return loginHelper(lastUserName, parameters.get(0));
	}

	private String validateUserName() {
		if (parameters.isEmpty()) {
			return "Invalid format. Expected: USER <tab> <username>";
		}
		return validateUserNameHelper(parameters.get(0));
	}

	private String executeCommand() {
		switch (command) {
		case "ADD":
			return hanldeAdd();
		case "EDIT":
			return hanldeUpdate();
		case "REMOVE":
			return hanldeRemove();
		case "VIEW":
			return hanldeView();
		default:
			return "Invalid command!";
		}
	}

	private String hanldeUpdate() {
		if (parameters.size() != 4)
			return "Invalid format. Expected: EDIT <tab> <idsp> <tab> <name> <tab> <count> <tab> <price>";
		int idsp = Integer.parseInt(parameters.get(0));
		String name = parameters.get(1);
		int count = Integer.parseInt(parameters.get(2));
		double price = Double.parseDouble(parameters.get(3));
		return handleUpdateHelper(new Product(idsp, name, count, price));
	}

	private String handleUpdateHelper(Product product) {
		try {
			if (server.update(sessionId, product))
				return "OK";
			else
				return "CAN NOT UPDATE";
		} catch (RemoteException e) {
			return "ERR server error while update product " + e.getMessage();
		}
	}

	private String hanldeView() {
		if (parameters.size() != 1)
			return "Invalid format. Expected: VIEW <tab> <partName>";
		return hanldeViewHelper(parameters.get(0));
	}

	private String hanldeViewHelper(String partName) {
		StringBuilder sb = new StringBuilder();
		try {
			for (Product p : server.view(sessionId, partName)) {
				sb.append(p + "\n");
			}
		} catch (RemoteException e) {
			return "ERR server error while view product " + e.getMessage();
		}
		return sb.toString().trim();
	}

	private String hanldeRemove() {
		if (parameters.isEmpty())
			return "Invalid format. Expected: REMOVE <tab> <idsp> [<tab> <idsp>...]";
		return hanldeRemoveHelper(parameters);
	}

	private String hanldeRemoveHelper(List<String> listId) {
		List<Integer> ids = new ArrayList<Integer>();
		int totalRowRemoved;
		for (String s : listId) {
			ids.add(Integer.parseInt(s));
		}
		try {
			totalRowRemoved = server.remove(sessionId, ids);
		} catch (RemoteException e) {
			return "ERR server error while remove product " + e.getMessage();
		}
		return Integer.toString(totalRowRemoved);
	}

	private String hanldeAdd() {
		if (parameters.size() != 4)
			return "Invalid format. Expected: ADD <tab> <idsp> <tab> <name> <tab> <count> <tab> <price>";
		int idsp = Integer.parseInt(parameters.get(0));
		String name = parameters.get(1);
		int count = Integer.parseInt(parameters.get(2));
		double price = Double.parseDouble(parameters.get(3));
		return handleAddHelper(new Product(idsp, name, count, price));
	}

	private String handleAddHelper(Product product) {
		try {
			if (server.add(sessionId, product))
				return "OK";
			else
				return "ERROR";
		} catch (RemoteException e) {
			return "ERR server error while add product " + e.getMessage();
		}
	}

	private String loginHelper(String username, String password) {
		if (username == null)
			return "ERR username cannot null!";
		try {
			sessionId = server.login(username, password);
			if (sessionId != -1) {
				isLoggedIn = true;
				return "OK";
			} else
				return "FALSE";
		} catch (RemoteException e) {
			return "ERR server error while login " + e.getMessage();
		}
	}

	private String validateUserNameHelper(String username) {
		if (username == null || username.isEmpty())
			return "ERR username cannot null or empty!";
		try {
			if (server.checkUserName(username)) {
				lastUserName = username;
				return "OK valid username!";
			} else
				return "ERR invalid username!";
		} catch (RemoteException e) {
			return "ERR server error while checking username " + e.getMessage();
		}
	}

	private void analyze(String line) {
		parameters.clear();
		StringTokenizer st = new StringTokenizer(line, "\t");
		command = st.hasMoreTokens() ? st.nextToken().toUpperCase() : "";
		while (st.hasMoreTokens()) {
			parameters.add(st.nextToken());
		}

	}

	public static void main(String[] args) throws RemoteException, NotBoundException {
		new Client().execute();
	}

}
