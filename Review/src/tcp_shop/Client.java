package tcp_shop;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Client {
    private final Socket socket;
    private final BufferedReader netIn;
    private final BufferedReader userIn;
    private final PrintWriter netOut;
    private String command;

    public Client() throws IOException {
        socket = new Socket("127.0.0.1", 5918);
        String charset = "UTF-8";
        netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
        netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset), true);
        userIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void exec() throws IOException {
        System.out.println(netIn.readLine());
        String line;
        while (true) {
            line = userIn.readLine();
            netOut.println(line);
            if ("QUIT".equalsIgnoreCase(line)) break;
            analyze(line);
            String response = processResponse();
            System.out.println(response);
        }
        socket.close();
    }

    private void analyze(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        command = tokenizer.hasMoreTokens() ? tokenizer.nextToken().toUpperCase() : "";
    }

    private String processResponse() throws IOException {
        StringBuilder sb = new StringBuilder();
        if ("MA".equalsIgnoreCase(command) || "TEN".equalsIgnoreCase(command) || "MUA".equalsIgnoreCase(command)) {
            String line;
            while (!(line = netIn.readLine()).equalsIgnoreCase(".")) {
                sb.append(line).append("\n");
            }
        } else {
            sb.append(netIn.readLine());
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) throws IOException {
        new Client().exec();
    }
}
