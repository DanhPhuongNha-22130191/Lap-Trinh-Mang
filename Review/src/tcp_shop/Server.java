package tcp_shop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		try (ServerSocket sc = new ServerSocket(5918)) {
			while(true) {
			Socket socket = sc.accept();
			OneThread one = new OneThread(socket);
			one.start();
			}
		}
	}
}
