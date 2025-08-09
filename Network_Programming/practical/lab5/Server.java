package lab5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException {
		int port = 2000;
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while(true) {
				Socket socket = serverSocket.accept();
				OneConnection one = new OneConnection(socket);
				one.start();
				System.out.println(socket.getInetAddress());
			}
		}
		
	}
}
