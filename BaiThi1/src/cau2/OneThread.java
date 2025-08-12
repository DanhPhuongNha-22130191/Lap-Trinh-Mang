package cau2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class OneThread extends Thread {
	private BufferedReader netIn;
	private PrintWriter netOut;
	private Socket socket;
	private String command;
	private List<String> parameters;
	private boolean isLogIn = false;
	private String lastUserName = null;
	private Dao dao;

	public OneThread(Socket socket) throws UnsupportedEncodingException, IOException {
		String charset = "UTF-8";
		this.socket = socket;
		netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
		parameters = new ArrayList<>();
		dao = new Dao();
	}

	@Override
	public void run() {
		netOut.println("WELCOME TO MANAGER PRODUCT SYSTEM");
		String line, res;
		try {
			while (true) {
				line = netIn.readLine();
				if ("QUIT".equalsIgnoreCase(line))
					break;
				analyze(line);
				res = processCommand();
				netOut.println(res);
			}
		} catch (IOException e) {
			netOut.println("Client close connect " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String processCommand() {
		return isLogIn ? executeCommand() : handleLogin();
	}

	private String handleLogin() {
		switch (command) {
		case "USER":
			return validateUserName(parameters.get(0));
		case "PASS":
			return login(lastUserName, parameters.get(0));
		default:
			return "Invalid command or you have to login first!";
		}
	}

	private String login(String lastUserName, String password) {
		if (lastUserName == null)
			return "ERR username cannot null";
		if (dao.login(lastUserName, password)) {
			isLogIn = true;
			return "Ok login successfully!";
		} else
			return "ERR incorrect password";
	}

	private String validateUserName(String username) {
		if (username == null || username.isEmpty())
			return "ERR username cannot null or empty!";
		if (dao.checkUserName(username)) {
			lastUserName = username;
			return "OK username!";
		} else
			return "ERR invalid username!";
	}

	private String executeCommand() {
		switch (command) {
		case "ADD":
			return handleAdd();
		case "REMOVE":
			return handleRemove();
		case "VIEW":
			return handleView();
		case "EDIT":
			return handleUpdate();
		default:
			return "Invalid command!";
		}
	}

	private String handleUpdate() {
		if (parameters.size() != 4)
			return "Invalid format. Expected: EDIT <idsp> <tab> <name> <tab> <count> <tab> <price> <tab>";
		int idsp = Integer.parseInt(parameters.get(0));
		String name = parameters.get(1);
		int count = Integer.parseInt(parameters.get(2));
		double price = Double.parseDouble(parameters.get(3));
		return handleUpdateHelper(new Product(idsp, name, count, price));
	}

	private String handleUpdateHelper(Product product) {
		if (dao.update(product))
			return "OK";
		else
			return "CANNOT UPDATE";
	}

	private String handleView() {
		if (parameters.size() != 1)
			return "Invalid format. Expected: VIEW <partName>";
		return handleViewHelper(parameters.get(0));
	}

	private String handleViewHelper(String partName) {
		StringBuilder sb = new StringBuilder();
		List<Product> list = dao.view(partName);
		if (list != null) {
			for (Product product : list) {
				sb.append(product + "\n");
			}
		}
		sb.append("THE END\r\n");
		return sb.toString().trim();
	}

	private String handleRemove() {
		if (parameters.size() < 1)
			return "Invalid format. Expected: REMOVE <tab> <idsp> [<tab> <idsp>...]";
		List<Integer> list = new ArrayList<Integer>();
		for (String id : parameters) {
			list.add(Integer.parseInt(id));
		}
		return handleRemoveHelper(list);
	}

	private String handleRemoveHelper(List<Integer> list) {
		return Integer.toString(dao.remove(list));
	}

	private String handleAdd() {
		if (parameters.size() != 4)
			return "Invalid format. Expected: ADD <idsp> <tab> <name> <tab> <count> <tab> <price> <tab>";
		int idsp = Integer.parseInt(parameters.get(0));
		String name = parameters.get(1);
		int count = Integer.parseInt(parameters.get(2));
		double price = Double.parseDouble(parameters.get(3));
		return handleAddHelper(new Product(idsp, name, count, price));
	}

	private String handleAddHelper(Product product) {
		if (dao.add(product))
			return "OK";
		else
			return "ERROR";
	}

	private void analyze(String line) {
		parameters.clear();
		StringTokenizer st = new StringTokenizer(line, "\t");
		command = st.hasMoreTokens() ? st.nextToken().toUpperCase() : "";
		while (st.hasMoreTokens()) {
			parameters.add(st.nextToken());
		}
	}

}
