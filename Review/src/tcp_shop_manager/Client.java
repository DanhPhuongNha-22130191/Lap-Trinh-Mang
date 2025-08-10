package tcp_shop_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.StringTokenizer;

public class Client {
	private Socket socket;
	private BufferedReader netIn, userIn;
	private PrintWriter netOut;
	private String com;

	public Client() throws UnsupportedEncodingException, IOException {
		socket = new Socket("127.0.0.1", 6969);
		String charset = "UTF-8";
		netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
		netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
		userIn = new BufferedReader(new InputStreamReader(System.in));
	}

	public void exec() {
		try {
			System.out.println(netIn.readLine());
			String line, res;
			while (true) {
				line = userIn.readLine();
				netOut.println(line);
				analyze(line);
				if ("QUIT".equalsIgnoreCase(line)) {
					System.out.println(netIn.readLine());
					break;
				}
				res = processResponse();
				System.out.println(res);
			}
		} catch (Exception e) {
			netOut.println("Client close connect ");
		}
	}

	private String processResponse() throws IOException {
		StringBuilder sb = new StringBuilder();
		if ("VIEW".equalsIgnoreCase(com)) {
			String s;
			while (!"THE END".equalsIgnoreCase((s = netIn.readLine()))) {
				sb.append(s).append("\n");
			}
		} else {
			sb.append(netIn.readLine());
		}
		return sb.toString().trim();
	}

	private void analyze(String line) {
		StringTokenizer st = new StringTokenizer(line, "\t");
		com = st.hasMoreTokens() ? st.nextToken().toUpperCase() : "";
	}

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		new Client().exec();
	}
}
