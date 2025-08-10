package tcp_shop_manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException {
		try(ServerSocket sc = new ServerSocket(6969)){
			while(true) {
				Socket socket = sc.accept();
				OneThread one = new OneThread(socket);
				one.start();
			}
		}
	}

}
