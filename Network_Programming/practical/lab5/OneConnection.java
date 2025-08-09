package lab5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class OneConnection extends Thread {
	private BufferedReader netIn;
	private PrintWriter netOut;
	private Socket socket;
	private double num1, num2;
	private char operator;

	public OneConnection(Socket socket) throws IOException {
		this.socket = socket;
		netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
	}

	public void run() {
	    try {
	        netOut.println("Welcome..");
	        String line;
	        while (true) {
	            line = netIn.readLine();
	            if (line == null) break;

	            if ("EXIT".equalsIgnoreCase(line)) {
	                netOut.println("Good bye!");
	                break;
	            }

	            try {
	                requestAnalyze(line);
	                double res = calculator();
	                netOut.println(line + " = " + res);
	            } catch (GlobalException e) {
	                netOut.println("Error: " + e.getMessage());
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


	private double calculator() throws GlobalException {
		double res = 0;
		switch (operator) {
		case '+':
			res = num1 + num2;
			break;
		case '-':
			res = num1 - num2;
			break;
		case '*':
			res = num1 * num2;
			break;
		case '/':
			res = num1 / num2;
			if (Double.isInfinite(res)) {
				throw new GlobalException("Error: Division by zero!");
			}
			break;
		default:
			throw new GlobalException("Unsupported operator!");
		}
		return res;
	}

	private void requestAnalyze(String line) throws GlobalException {
		StringTokenizer st = new StringTokenizer(line, "+-*/");
		String str1, str2;
		str1 = st.nextToken();
		try {
			str2 = st.nextToken();
		} catch (NoSuchElementException e) {
			throw new GlobalException("Input error: missing operand!");
		}
		this.operator = line.charAt(str1.length());
		this.num1 = Double.parseDouble(str1.trim());
		this.num2 = Double.parseDouble(str2.trim());
	}

}
