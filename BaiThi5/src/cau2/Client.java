package cau2;

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
	private IProduct server;
	private String command, lastUserName = null;
	private List<String> parameters;
	private boolean isLogin = false;
	private int sessionId = 0;

	public Client() throws RemoteException, NotBoundException {
		userIn = new BufferedReader(new InputStreamReader(System.in));
		Registry reg = LocateRegistry.getRegistry("127.0.0.1", 8989);
		server = (IProduct) reg.lookup("PRODUCT");
		parameters = new ArrayList<String>();
	}

	public void execute() {
		try {
			String line, res;
			System.out.println(server.getBanner());
			while (true) {
				line = userIn.readLine();
				if ("EXIT".equalsIgnoreCase(line))
					break;
				analyze(line);
				res = processCommad();
				System.out.println(res);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String processCommad() throws RemoteException {
		return isLogin ? executeCommad() : handleLogin();
	}

	private String executeCommad() throws NumberFormatException, RemoteException {
		switch (command) {
		case "MA":
			return handleFindById();
		case "TEN":
			return handleFindByName();
		case "MUA":
			return handleFindByBuy();
		case "QUIT":
			return handleFindByQuit();
		default:
			return "Unvalid command!";
		}
	}

	private String handleFindByQuit() {
		// TODO Auto-generated method stub
		return null;
	}

	private String handleFindByBuy() {
		if (parameters.isEmpty())
			return "Invalid format! Expected: BUY <tab> <idsp> [<tab> <idsp>..]";
		List<Integer> listId = new ArrayList<Integer>();
		for (String id : parameters) {
			listId.add(Integer.parseInt(id));
		}
		return handleFindByBuyHelper(listId);
	}

	private String handleFindByBuyHelper(List<Integer> listId) {
		
		return null;
	}

	private String handleFindByName() throws NumberFormatException, RemoteException {
		if (parameters.size() != 1)
			return "Invalid format! Expected: TEN <tab> <name>";
		return handleFindByNameHelper(parameters.get(0));
	}

	private String handleFindByNameHelper(String name) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		List<Product> list = server.findByName(sessionId, name);
		for (Product product : list) {
			sb.append(product + "\n");
		}
		return sb.toString().trim();
	}

	private String handleFindById() throws NumberFormatException, RemoteException {
		if (parameters.size() != 1)
			return "Invalid format! Expected: TEN <tab> <idsp>";
		return handleFindByIdHelper(Integer.parseInt(parameters.get(0)));
	}

	private String handleFindByIdHelper(int idsp) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		List<Product> list = server.findById(sessionId, idsp);
		for (Product product : list) {
			sb.append(product + "\n");
		}
		return sb.toString().trim();

	}

	private String handleLogin() throws RemoteException {
		switch (command) {
		case "TEN":
			return validateUserName(parameters.get(0));
		case "MATKHAU":
			return login(lastUserName, parameters.get(0));
		default:
			return "ERR you have to login frist or command invalid!";
		}
	}

	private String login(String lastUserName, String password) throws RemoteException {
		if (lastUserName == null)
			return "ERR username cannot nul!";
		try {
			sessionId = server.login(lastUserName, password);
			if (sessionId != -1) {
				isLogin = true;
				return "OK loggin successfully!";
			} else
				return "ERR incorrect password!";
		} catch (RemoteException e) {
			throw new RemoteException("ERR server error while login Client " + e.getMessage());
		}

	}

	private String validateUserName(String username) throws RemoteException {
		if (username == null || username.isEmpty())
			return "ERR username cannot null or empty!";
		try {
			if (server.checkUserName(username)) {
				lastUserName = username;
				return "OK valid username!";
			} else
				return "ERR invalid username!";
		} catch (RemoteException e) {
			throw new RemoteException("ERR server error while check username Client " + e.getMessage());
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
