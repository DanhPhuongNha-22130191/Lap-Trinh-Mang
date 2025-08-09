

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException {
		int port = 54321;
		while (true) {
			try (ServerSocket sc = new ServerSocket(port);) {
				Socket socket = sc.accept();
				ConnectionHandler connectionHandler = new ConnectionHandler(socket);
				connectionHandler.start();
				System.out.println(socket.getInetAddress());
			} catch (Exception e) {
			}
		}
	}
}
