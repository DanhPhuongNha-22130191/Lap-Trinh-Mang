package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		String host = "localhost";
		int port = 54321;
		try (Socket socket = new Socket(host, port)) {
			BufferedReader netIn = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			PrintWriter netOut = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

			String line, res;
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(netIn.readLine());
			while (true) {
				line = userIn.readLine();
				netOut.println(line);
				if ("EXIT".equalsIgnoreCase(line))
					break;
				StringTokenizer st = new StringTokenizer(line);
				String com = st.nextToken().toUpperCase();
				if (com.equalsIgnoreCase("FID") || com.equalsIgnoreCase("FBN")) {
					String s;
					res = "";
					while (!(s = netIn.readLine()).equalsIgnoreCase("."))
						res += s + "\n";
				}
				res = netIn.readLine();
				System.out.println(res);
			}
//			socket.close();
		}
	}

}
