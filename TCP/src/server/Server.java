package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
	public static void main(String[] args) {
		ServerSocket sc;
		try {
			sc = new ServerSocket(54321);
			while (true) {
				Socket socket = sc.accept();
				OneThread one = new OneThread(socket);
				one.start();
				System.out.println(socket.getInetAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
