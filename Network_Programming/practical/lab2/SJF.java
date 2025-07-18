package lab2;

import java.io.*;

public class SJF {
	private void split(String src, int pSize) throws IOException {
		String dest;
		FileInputStream fis;
		FileOutputStream fos;
		File source = new File(src);
		if (!source.exists() || !source.isFile())
			return;
		long fileSize = source.length();
		int numOfFile = (int) (fileSize / pSize);
		boolean isRemain = fileSize % pSize > 0;
		fis = new FileInputStream(source);
		int i = 0;
		for (; i < numOfFile; i++) {
			dest = src + suffix(i);
			fos = new FileOutputStream(dest);
			copyFrom(fis, fos, pSize);
			fos.close();
		}
		if (isRemain) {
			dest = src + suffix(i);
			fos = new FileOutputStream(dest);
			int bytesRead;
			byte[] buffer = new byte[102400];
			while ((bytesRead = fis.read(buffer)) != -1)
				fos.write(buffer, 0, bytesRead);
			fos.close();
		}
		fis.close();
		source.delete();
	}

	private void copyFrom(FileInputStream fis, FileOutputStream fos, int pSize) throws IOException {
		byte[] buffer = new byte[102400];
		int remain = pSize;
		while (remain > 0) {
			int byteToRead = remain > buffer.length ? buffer.length : remain;
			int bytesRead = fis.read(buffer, 0, byteToRead);
			if (bytesRead == -1)
				break;
			fos.write(buffer, 0, bytesRead);
			remain -= bytesRead;
		}
	}

	private void join(String partFilename) throws IOException {
		String dest, source;
		FileInputStream fis;
		FileOutputStream fos;
		dest = partFilename.substring(0, partFilename.lastIndexOf('.'));
		fos = new FileOutputStream(dest);
		int i = 0;
		while (true) {
			source = dest + suffix(i);
			File temp = new File(source);
			if (!temp.exists())
				break;
			fis = new FileInputStream(temp);
			int bytesRead;
			byte[] buffer = new byte[102400];
			while ((bytesRead = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
			fis.close();
			i++;
			temp.delete();
		}
		fos.close();
	}

	private String suffix(int count) {
		count++;
		if (count < 10)
			return ".00" + count;
		if (count < 100)
			return ".0" + count;
		return "." + count;
	}

	public static void main(String[] args) throws IOException {
		String src = "E:\\SJF\\abc.png";
		int pSize = 1000000;
		String partFilename = "E:\\SJF\\abc.png.005";
		SJF sjf = new SJF();
//		sjf.split(src, pSize);
		sjf.join(partFilename);

	}

}
