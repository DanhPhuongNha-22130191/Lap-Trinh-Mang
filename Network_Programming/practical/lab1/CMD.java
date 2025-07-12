package lab1;

import java.io.*;
import java.util.*;

public class CMD {
	private File defaultDir;
	private BufferedReader userIn;

	public CMD(String path) {
		defaultDir = new File(path);
		userIn = new BufferedReader(new InputStreamReader(System.in));
	}

	private String getPrompt() throws IOException {
		return defaultDir.getCanonicalPath() + ">";
	}

	private void run() throws IOException {
		System.out.print(getPrompt());
		String line, com, res;
		QUIT: while (true) {
			com = "";
			line = userIn.readLine();
			StringTokenizer tokenizer = new StringTokenizer(line);
			List<String> params = new ArrayList<>();
			if (!tokenizer.hasMoreTokens()) {
				System.out.print(getPrompt());
				continue;
			}
			com = tokenizer.nextToken().toUpperCase();
			while (tokenizer.hasMoreTokens()) {
				params.add(tokenizer.nextToken());
			}
			switch (com) {
			case "EXIT":
				break QUIT;
			case "CD":
				res = changeDir(params.get(0));
				break;
			case "DIR":
				res = listDir();
				break;
			case "DEL":
				res = delDir(params.get(0));
				break;
			case "CP":
				if (params.size() < 2) {
					res = "Error: Missing source or destination file name!\n";
				} else {
					res = copyDir(params.get(0), params.get(1));
				}
				break;
			case "MV":
				if (params.size() < 2) {
					res = "Error: Missing source or destination file name!\n";
				} else {
					res = moveDir(params.get(0), params.get(1));
				}
				break;
			default:
				res = "Unknown command!";
				break;
			}
			System.out.println(res);
			System.out.print(getPrompt());
		}
	}

	private String moveDir(String src, String dest) throws IOException {
		if (check(src, dest)) {
			helper(src, dest, true);
			return "";
		} else {
			return "Faild to move!\n";
		}
	}

	private String copyDir(String src, String dest) throws IOException {
		if (check(src, dest)) {
			helper(src, dest, false);
			return "";
		} else {
			return "Faild to copy!\n";
		}
	}

	private void helper(String src, String dest, boolean isMove) throws IOException {
		File source = new File(defaultDir, src);
		File target = new File(defaultDir, dest);
		try (FileInputStream fis = new FileInputStream(source); FileOutputStream fos = new FileOutputStream(target)) {
			byte[] buffer = new byte[1024000];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
		}
		if (isMove)
			source.delete();
	}

	private boolean check(String src, String dest) {
		File srcFile = new File(defaultDir, src);
		if (!srcFile.exists())
			return false;
		if (srcFile.isDirectory())
			return false;
		return true;
	}

	private String delDir(String param) throws IOException {
		File temp = new File(defaultDir, param);
		if (!temp.exists())
			return "File name " + param + " not exists!\n";
		boolean res = delHelper(temp.getCanonicalPath());
		return res ? "" : "Error! Some file cannot delete.\n";
	}

	private boolean delHelper(String canonicalPath) throws IOException {
		File file = new File(canonicalPath);
		File[] list = file.listFiles();
		if (list != null)
			for (File f : list)
				delHelper(f.getCanonicalPath());
		return file.delete();
	}

	private String listDir() {
		StringBuilder sb = new StringBuilder();
		File[] list = defaultDir.listFiles();
		if (list != null) {
			for (File f : list) {
				if (f.isDirectory())
					sb.append(f.getName().toUpperCase() + "\n");
			}
			for (File f : list) {
				if (!f.isDirectory())
					sb.append(f.getName().toLowerCase() + "\n");
			}
		}
		return sb.toString();
	}

	private String changeDir(String param) throws IOException {
		if (param.isBlank())
			return defaultDir.getCanonicalPath() + "\n";
		if ("..".equals(param)) {
			File temp = defaultDir.getParentFile();
			if (temp != null)
				defaultDir = temp;
			return "";
		}
		File temp = new File(defaultDir, param);
		if (!temp.exists())
			return "File name " + param + " not exists!\n";
		defaultDir = temp;
		return "";
	}

	public static void main(String[] args) throws IOException {
		String path = "E:\\TEMP";
		CMD cmd = new CMD(path);
		cmd.run();

	}

}
