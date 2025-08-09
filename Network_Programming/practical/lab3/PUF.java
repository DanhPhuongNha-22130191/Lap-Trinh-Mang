package lab3;

import java.io.*;
import java.util.*;

public class PUF {
	private void pack(String folder, String packedFile) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
			raf.setLength(0);
			List<File> listFile = getListFile(folder);
			int numberOfFile = listFile.size();
			raf.writeInt(numberOfFile);
			int i = 0;
			for (File f : listFile) {
				raf.writeLong(0);
				i++;
			}
			i = 0;
			FileInputStream fis;
			for (File f : listFile) {
				long position = raf.getFilePointer();
				raf.writeUTF(f.getName());
				long size = f.length();
				raf.writeLong(size);
				fis = new FileInputStream(f);
				copyFrom(fis, raf, size);
				int hPos = 4 + 8 * i;
				raf.seek(hPos);
				raf.writeLong(position);
				raf.seek(raf.length());
				i++;
				fis.close();
			}
			raf.close();
		}
	}

	private void unPack(String packedFile, String extractFile, String dest) throws IOException {
		checkSource(packedFile);
		RandomAccessFile raf = new RandomAccessFile(packedFile, "r");
		int numberOfFile = raf.readInt();
		long[] offsets = new long[numberOfFile];
		for (int i = 0; i < numberOfFile; i++) {
			offsets[i] = raf.readLong();
		}
		FileOutputStream fos;
		for (int i = 0; i < numberOfFile; i++) {
			raf.seek(offsets[i]);
			String fileName = raf.readUTF();
			if (fileName.equalsIgnoreCase(extractFile)) {
				fos = new FileOutputStream(dest);
				long size = raf.readLong();
				copyFrom(raf, fos, size);
				fos.close();
			}
		}
		raf.close();
	}

	private void copyFrom(RandomAccessFile raf, FileOutputStream fos, long size) throws IOException {
		byte[] buffer = new byte[102400];
		int remain = (int) size;
		while (remain > 0) {
			int byteToRead = remain > buffer.length ? buffer.length : remain;
			int bytesRead = raf.read(buffer, 0, byteToRead);
			if (bytesRead == -1)
				break;
			fos.write(buffer, 0, bytesRead);
			remain -= bytesRead;
		}

	}

	private void checkSource(String packedFile) {
		File file = new File(packedFile);
		if (!file.exists() || !file.isFile())
			return;
	}

	private void copyFrom(FileInputStream fis, RandomAccessFile raf, long size) throws IOException {
		byte[] buffer = new byte[102400];
		int remain = (int) size;
		while (remain > 0) {
			int byteToRead = remain > buffer.length ? buffer.length : remain;
			int bytesRead = fis.read(buffer, 0, byteToRead);
			if (bytesRead == -1)
				break;
			raf.write(buffer, 0, bytesRead);
			remain -= bytesRead;
		}
	}

	private List<File> getListFile(String folder) {
		List<File> listFile = new ArrayList<File>();
		File dir = new File(folder);
		if (!dir.exists() || !dir.isDirectory())
			return listFile;
		File[] files = dir.listFiles(f -> f.isFile());
		if (files != null) {
			for (File temp : files)
				listFile.add(temp);
		}
		return listFile;
	}

	public static void main(String[] args) throws IOException {
		PUF puf = new PUF();
		String folder = "E:\\SJF";
		String packedFile = "E:\\store.txt";
		String extractFile = "1. Intro_Subject.pptx";
		String dest = "E:\\1. Intro_Subject.pptx";
		puf.pack(folder, packedFile);
		puf.unPack(packedFile, extractFile, dest);
	}

}
