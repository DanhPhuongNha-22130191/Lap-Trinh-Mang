package demo1;

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
	private ISearch server;
	private String com, param;

	public Client() throws RemoteException, NotBoundException {
		userIn = new BufferedReader(new InputStreamReader(System.in));
		Registry reg = LocateRegistry.getRegistry("127.0.0.1", 12345);
		server = (ISearch) reg.lookup("SEARCH");
	}

	public void exec() throws IOException {
		System.out.println(server.getGreeting());
		boolean isLogin = false;
		String res, line;
		String lastUserName = null;
		while (!isLogin) {
			res = "";
			line = userIn.readLine();
			if ("EXIT".equalsIgnoreCase(line))
				break;
			analyze(line);
			switch (com) {
			case "USER":
				if (server.checkUserName(param)) {
					lastUserName = param;
					res = "OK username exist!";
				} else
					res = "ERR username not exists!";
				break;
			case "PASS":
				if (lastUserName == null) {
					res = "ERR chua nhan username";
				} else {
					if (server.login(lastUserName, param)) {
						isLogin = true;
						res = "OK login successfully!";
					} else
						res = "Failed to login!";
				}
				break;
			default:
				res = "Unvalid command!";
			}
			System.out.println(res);
		}
		List<Student> list;
		while (isLogin) {
			res = "";
			line = userIn.readLine();
			if ("EXIT".equalsIgnoreCase(line))
				break;
			analyze(line);
			switch (com) {
			case "FID":
				list = server.findById(Integer.parseInt(param));
				res = makeResponse(list);
				break;
			case "FBN":
				list = server.findByName(param);
				res = makeResponse(list);
				break;
			default:
				res = "Unvalid command!";
			}
			System.out.println(res);

		}

	}

	private String makeResponse(List<Student> list) {
		if(list.isEmpty()) return "Not found!";
		StringBuilder sb = new StringBuilder();
		for(Student s: list) {
			sb.append(s.toString()+"\n");
		}
		return sb.toString();
	}	

	private void analyze(String line) {
		StringTokenizer st = new StringTokenizer(line);
		com = st.nextToken().toUpperCase();
		param = line.substring(com.length()).trim();
	}

	public static void main(String[] args) throws NotBoundException, IOException {
		new Client().exec();
	}

}
