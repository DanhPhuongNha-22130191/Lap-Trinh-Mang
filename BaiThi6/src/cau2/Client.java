package cau2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.StringTokenizer;

public class Client {
	private BufferedReader userIn;
	private IBanking server;
	private String command, parameter, lastUsername = null;
	private boolean isLogin = false;
	private int sessionId = -1;

	public Client() throws RemoteException, NotBoundException {
		Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
		server = (IBanking) reg.lookup("BANKING");
		userIn = new BufferedReader(new InputStreamReader(System.in));
	}

	private void execute() {
		try {
			System.out.println(server.getBanner());
			String line, res;
			while (true) {
				line = userIn.readLine();
				if ("QUIT".equalsIgnoreCase(line))
					break;
				analyze(line);
				res = processCommand();
				System.out.println(res);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String processCommand() throws RemoteException {
		return isLogin ? executeCommand() : hanldeLogin();
	}

	private String hanldeLogin() throws RemoteException {
		switch (command) {
		case "USER":
			return validateUserName(parameter);
		case "PASSWORD":
			return login(lastUsername, parameter);
		default:
			return "ERR You have to login first!";
		}
	}

	private String executeCommand() throws NumberFormatException, RemoteException {
		switch (command) {
		case "DEPOSIT":
			return handleDeposit(Double.parseDouble(parameter));
		case "WITHDRAW":
			return handleWithDraw(Double.parseDouble(parameter));
		case "REPORT":
			return handleReport();
		case "BALANCE":
			return handleBalance();
		default:
			return "Unvalid command!";
		}
	}

	private String handleBalance() throws RemoteException {
		try {
			double balance = server.balance(sessionId);
			return Double.toString(balance);
		} catch (RemoteException e) {
			throw new RemoteException("Server error while get balance in client " + e.getMessage());

		}
	}

	private String handleReport() throws RemoteException {
		try {
			StringBuilder sb = new StringBuilder();
			List<Note> list = server.note(sessionId);
			if (list != null) {
				for (Note note : list) {
					sb.append(note + "\n");
				}
			}
			return sb.toString();
		} catch (RemoteException e) {
			throw new RemoteException("Server error while get notes in client " + e.getMessage());
		}
	}

	private String handleWithDraw(double amount) throws RemoteException {
		try {
			if (server.withDraw(sessionId, amount))
				return "OK";
			else
				return "FAILED";
		} catch (RemoteException e) {
			throw new RemoteException("Server error while handling with draw in client " + e.getMessage());

		}
	}

	private String handleDeposit(double amount) throws RemoteException {
		try {
			if (server.deposit(sessionId, amount))
				return "OK";
			else
				return "FAILED";
		} catch (RemoteException e) {
			throw new RemoteException("Server error while handling deposit in client " + e.getMessage());

		}
	}

	private String login(String lastUsername2, String parameter2) throws RemoteException {
		if (lastUsername == null)
			return "ERR";
		try {
			sessionId = server.login(lastUsername, parameter);
			if (sessionId != 1) {
				isLogin = true;
				return "OK";
			} else
				return "ERR";
		} catch (RemoteException e) {
			throw new RemoteException("Server error while login in client " + e.getMessage());

		}
	}

	private String validateUserName(String username) throws RemoteException {
		if (username == null || username.isEmpty())
			return "ERR";
		try {
			if (server.checkUserName(username)) {
				lastUsername = username;
				return "OK";
			} else
				return "ERR";
		} catch (RemoteException e) {
			throw new RemoteException("Server error while checking username in client " + e.getMessage());
		}
	}

	private void analyze(String line) {
		StringTokenizer st = new StringTokenizer(line);
		command = st.hasMoreTokens() ? st.nextToken().toUpperCase() : "";
		parameter = line.substring(command.length()).trim();

	}

	public static void main(String[] args) throws RemoteException, NotBoundException {
		new Client().execute();
	}

}
