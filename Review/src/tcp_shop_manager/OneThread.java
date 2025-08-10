package tcp_shop_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class OneThread extends Thread {
	private Socket socket;
	private BufferedReader netIn;
	private PrintWriter netOut;
	private boolean isLogin = false;
	private String lastUsername = null, command;
	private List<String> parameters;
	private Dao dao;

	public OneThread(Socket socket) throws UnsupportedEncodingException, IOException {
		this.socket = socket;
		String charset = "UTF-8";
		netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
		parameters = new ArrayList<String>();
		try {
			dao = new Dao();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String line, res;
		try {
			netOut.println("WELCOME TO MANAGER PRODUCT SYSTEM..");
			while (true) {
				line = netIn.readLine();
				if ("QUIT".equalsIgnoreCase(line)) {netOut.println("Good bye");
					break;}
				analyze(line);
				res = processCommand();
				netOut.println(res);
			}
		} catch (IOException | SQLException e) {
			netOut.println("ERR" + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String processCommand() throws SQLException {
	    if (!isLogin) {
	        // Nếu chưa đăng nhập mà lệnh không phải USER hoặc PASS thì báo lỗi
	        if (!command.equals("USER") && !command.equals("PASS")) {
	            return "ERR you have to login first!";
	        } else {
	            return handleLogin();
	        }
	    } else {
	        return executeCommand();
	    }
	}

	private String executeCommand() {
		switch (command) {
		case "ADD":
			if (parameters.size() < 4)
				return "ERR command must be: ADD <idsp> <tab> <ten_san_pham> <tab> <so_luong> <tab> <gia_ban>";
			return addProduct(Integer.parseInt(parameters.get(0)), parameters.get(1),
					Integer.parseInt(parameters.get(2)), Double.parseDouble(parameters.get(3)));
		case "REMOVE":
			if (parameters.size() < 1)
				return "ERR dont have isdp to remove!";
			List<Integer> listProductId = new ArrayList<Integer>();
			for (String productId : parameters) {
				listProductId.add(Integer.parseInt(productId));
			}
			return removeProducts(listProductId);
		case "UPDATE":
			if (parameters.size() < 4)
				return "ERR command must be: UPDATE <idsp> <tab> <ten_san_pham> <tab> <so_luong> <tab> <gia_ban>";
			return updateProduct(Integer.parseInt(parameters.get(0)), parameters.get(1),
					Integer.parseInt(parameters.get(2)), Double.parseDouble(parameters.get(3)));
		case "VIEW":
			return getProductsByName(parameters.get(0));
		default:
			return "Command invalid!";
		}
	}


	private String getProductsByName(String partOfName) {
		StringBuilder sb = new StringBuilder();
		List<Product> list = dao.viewProducts(partOfName);
		if (list == null || list.isEmpty())
			return "NOT FOUND!";
		for (Product product : list) {
			sb.append(product + "\n");
		}
		sb.append("THE END\r\n");
		return sb.toString().trim();
	}

	private String updateProduct(int productId, String name, int count, double price) {
		if (dao.editProduct(productId, name, count, price))
			return "OK";
		else
			return "CAN NOT UPDATE";
	}

	private String removeProducts(List<Integer> listProductId) {
		if (listProductId == null || listProductId.isEmpty())
			return "Not found product to remove";
		int result = dao.removeProducts(listProductId);
		return Integer.toString(result);
	}

	private String addProduct(int productId, String name, int count, double price) {
		if (dao.addProduct(productId, name, count, price))
			return "OK";
		else
			return "ERROR";
	}

	private String handleLogin() throws SQLException {
		switch (command) {
		case "USER":
			return validUserName(parameters.get(0));
		case "PASS":
			return validPassword(lastUsername, parameters.get(0));
		default:
			return "ERR you have to login first!";
		}
	}

	private String validPassword(String lastUsername, String password) {
		if (lastUsername == null)
			return "ERR username cannot null!";
		else if (dao.login(lastUsername, password)) {
			isLogin = true;
			return "OK login successfully!";
		} else
			return "ERR incorrect password!";
	}

	private String validUserName(String username) throws SQLException {
		if (username == null || username.isEmpty())
			return "ERR username cannot null or empty!";
		if (dao.checkUserName(username)) {
			lastUsername = username;
			isLogin = false;
			return "OK valid username!";
		} else
			return "ERR invalid username!";
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
