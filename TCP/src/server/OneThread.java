package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import dao.Dao;
import model.Student;

public class OneThread extends Thread {
	private BufferedReader netIn;
	private PrintWriter netOut;
	private Socket socket;
	private boolean isLogin = false;
	private String com, param, lastUserName = null;
	private Dao dao;

	public OneThread(Socket socket) throws IOException, ClassNotFoundException, SQLException {
		this.socket = socket;
		String charset = "UTF-8";
		netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
		dao = new Dao();
	}

	@Override
	public void run() {
		try {
			netOut.println("Welcome to TCP Server!");
			String line;

			while ((line = netIn.readLine()) != null) {
				if ("EXIT".equalsIgnoreCase(line))
					break;

				analyze(line);
				String response = processCommand();
				netOut.println(response);
			}
		} catch (Exception e) {
			e.printStackTrace(); // hoặc log ra file
		}
	}

	private String processCommand() {
		try {
			if (!isLogin) {
				return handleLoginCommands();
			} else {
				return handlePostLoginCommands();
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
			return "ERR SQL: " + e.getMessage();
		} catch (NumberFormatException e) {
			return "ERR Invalid number format!";
		}
	}

	private String handleLoginCommands() throws SQLException {
		switch (com) {
			case "USER":
				if (dao.checkUserName(param)) {
					lastUserName = param;
					return "OK valid username!";
				} else {
					return "ERR invalid username!";
				}
			case "PASS":
				if (lastUserName == null)
					return "ERR username cannot be null!";
				if (dao.login(lastUserName, param)) {
					isLogin = true;
					return "OK login successfully!";
				} else {
					return "ERR password is incorrect!";
				}
			default:
				return "ERR invalid login command!";
		}
	}

	private String handlePostLoginCommands() throws SQLException {
		List<Student> list;
		switch (com) {
			case "USER": 
				if (dao.checkUserName(param)) {
					lastUserName = param;
					return "OK valid username!";
				} else {
					return "ERR invalid username!";
				}
			case "FID":
				int id = Integer.parseInt(param);
				list = dao.findById(id);
				return formatStudents(list);
			case "FBN":
				list = dao.findByName(param);
				return formatStudents(list);
			default:
				return "The command is invalid!";
		}
	}

	
	private String formatStudents(List<Student> list) {
		if (list == null || list.isEmpty())
			return "Not found";

		StringBuilder sb = new StringBuilder();
		for (Student st : list) {
			sb.append(st).append("\n");
		}
		sb.append(".\r\n");
		return sb.toString().trim();
	}

	private void analyze(String line) {
		StringTokenizer st = new StringTokenizer(line);
		com = st.nextToken().toUpperCase();
		param = line.substring(com.length()).trim();
	}
}
