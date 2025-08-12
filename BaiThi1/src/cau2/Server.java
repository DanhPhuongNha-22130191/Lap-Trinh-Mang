package cau2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		try(ServerSocket sc = new ServerSocket(8989)) {
			while (true) {
				Socket socket = sc.accept();
				OneThread one = new OneThread(socket);
				one.start();
				System.out.println(socket.getInetAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
