package cau2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Client {
	private BufferedReader userIn;
	private String command;
	private List<String> parameters;
	private String lastUserName = null;
	private boolean isLogin = false;
	private IManager server;
	private int sessionId = -1;

	public Client() throws UnsupportedEncodingException, RemoteException, NotBoundException {
		userIn = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		Registry reg = LocateRegistry.getRegistry("127.0.0.1", 8989);
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
				throw new RemoteException("ERR while execute method " + e.getMessage());
			}
		}

	}

	private String processCommand() {
		return isLogin ? executeCommand() : hanldeLogin();
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

	private String hanldeView() {
		if (parameters.size() != 1)
			return "Invalid format. Expected: VIEW <tab> <partName>";
		return hanldeViewHelper(parameters.get(0));
	}

	private String hanldeViewHelper(String partName) {
		StringBuilder sb = new StringBuilder();
		List<Product> list;
		try {
			list = server.view(sessionId, partName);
			if (list != null) {
				if (!list.isEmpty()) {
					for (Product product : list) {
						sb.append(product + "\n");
					}
				} else
					sb.append("Not found!\n");
			}
		} catch (RemoteException e) {
			return "ERR server error while get products " + e.getMessage();
		}
		return sb.toString().trim();

	}

	private String hanldeRemove() {
		if (parameters.size() < 1)
			return "Invalid format. Expected: REMOVE <tab> <idsp> [<tab> <idsp>...]";
		List<Integer> listId = new ArrayList<Integer>();
		for (String id : parameters) {
			listId.add(Integer.parseInt(id));
		}
		return hanldeRemoveHelper(listId);
	}

	private String hanldeRemoveHelper(List<Integer> listId) {
		try {
			return Integer.toString(server.remove(sessionId, listId));
		} catch (RemoteException e) {
			return "ERR server error while remove product " + e.getMessage();
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
				return "CANNOT UPDATE";
		} catch (RemoteException e) {
			return "ERR server error while add product " + e.getMessage();
		}
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

	private String hanldeLogin() {
		switch (command) {
		case "USER":
			return validateUserName(parameters.get(0));
		case "PASS":
			return login(lastUserName, parameters.get(0));
		default:
			return "ERR you have to login first or command invalid!";
		}
	}

	private String login(String lastUserName, String password) {
		if (lastUserName == null)
			return "ERR username cannot null ";
		try {
			sessionId = server.login(lastUserName, password);
			if (sessionId != -1) {
				isLogin = true;
				return "OK login successfully!";
			} else
				return "ERR incorrect password!";
		} catch (RemoteException e) {
			return "ERR server error while check username" + e.getMessage();
		}
	}

	private String validateUserName(String username) {
		if (username == null || username.isEmpty())
			return "ERR username cannot null or empty";
		try {
			if (server.checkUserName(username)) {
				lastUserName = username;
				return "OK username";
			} else
				return "ERR invalid username";
		} catch (RemoteException e) {
			return "ERR server error while check username" + e.getMessage();
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

	public static void main(String[] args) throws UnsupportedEncodingException, RemoteException, NotBoundException {
		new Client().execute();
	}
}
