package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import model.Student;
import remote.IAuthorizedSession;
import remote.ISearch;

public class Client {
	private BufferedReader userIn;
	private ISearch server;
	private String com, param;
	private String lastUserName = null;
	private IAuthorizedSession session = null;

	public Client() throws RemoteException, NotBoundException {
		Registry reg = LocateRegistry.getRegistry("127.0.0.1", 12345);
		server = (ISearch) reg.lookup("SEARCH");
		userIn = new BufferedReader(new InputStreamReader(System.in));
	}

	public void exec() throws RemoteException {
		try {
			System.out.println(server.getGreeting());
			String line, res;
			while (true) {
				res = "";
				line = userIn.readLine();
				if ("EXIT".equalsIgnoreCase(line))
					break;
				analyze(line);
				res = handleCommand();
				System.out.println(res);
			}
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	private String handleCommand() throws RemoteException {
		switch (com) {
		case "USER":
			return handleUserName(param);
		case "PASS":
			return handleLogin(lastUserName, param);
		case "FID":
			return findById(Integer.parseInt(param));
		case "FBN":
			return findByName(param);
		default:
			return "Unknown command!";
		}

	}

	private String findByName(String partOfName) throws RemoteException {
		if (session == null)
			return "You have to login first!";
		List<Student> list = session.findByName(partOfName);
		if (list == null || list.isEmpty())
			return "Not found!";
		StringBuilder sb = new StringBuilder();
		for (Student s : list) {
			sb.append(s + "\n");
		}
		sb.append(".\r\n");
		return sb.toString().trim();
	}

	private String findById(int sid) throws RemoteException {
		if (session == null)
			return "You have to login first!";
		List<Student> list = session.findById(sid);
		if (list == null || list.isEmpty())
			return "Not found!";
		StringBuilder sb = new StringBuilder();
		for (Student s : list) {
			sb.append(s + "\n");
		}
		return sb.toString().trim();
	}

	private String handleLogin(String username, String password) throws RemoteException {
		if (username == null)
			return "Username is invalid!";
		session = server.login(username, password);
		if (session != null)
			return "OK login successfully!";
		return "ERR password is incorrect!";
	}

	private String handleUserName(String username) throws RemoteException {
		if (server.checkUserName(username)) {
			lastUserName = param;
			return "OK username valid!";
		}
		return "Username is invalid!";
	}

	private void analyze(String line) {
		StringTokenizer st = new StringTokenizer(line);
		com = st.nextToken().toUpperCase();
		param = line.substring(com.length()).trim();
	}

	public static void main(String[] args)
			throws RemoteException, NotBoundException, ClassNotFoundException, SQLException {
		new Client().exec();
	}
}
