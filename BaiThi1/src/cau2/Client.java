package cau2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private BufferedReader netIn, userIn;
	private PrintWriter netOut;
	private Socket socket;

	public Client() throws UnknownHostException, IOException {
		socket = new Socket("127.0.0.1", 8989);
		String charset = "UTF-8";
		netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
		userIn = new BufferedReader(new InputStreamReader(System.in, charset));
	}

	public void execute() throws IOException {
		System.out.println(netIn.readLine());
		String line, res;
		while (true) {
			line = userIn.readLine();
			if ("QUIT".equalsIgnoreCase(line))
				break;
			netOut.println(line);
			System.out.println(netIn.readLine());
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Client().execute();
	}
}
