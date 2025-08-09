package lab3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PUF_Ver2 {

	private void pack(String folder, String packedFile) throws IOException {
		List<File> listFile = getListFile(folder);
		RandomAccessFile raf = new RandomAccessFile(packedFile, "rw");
		raf.setLength(0);
		int numberOfFile = listFile.size();
		raf.writeInt(numberOfFile);
		long[] offsets = new long[numberOfFile];
		long currentPos = 4;
		for (int i = 0; i < numberOfFile; i++) {
			byte[] bytesName = listFile.get(i).getName().getBytes("UTF-8");
			int nameLength = bytesName.length;
			raf.writeInt(nameLength);
			raf.write(bytesName);
			raf.writeLong(listFile.get(i).length());
			offsets[i] = raf.getFilePointer();
			raf.writeLong(0);
			currentPos += nameLength + 4 + 8 + 8;
		}
		FileInputStream fis;
		int bytesRead;
		byte[] buffer = new byte[102400];
		for (int i = 0; i < numberOfFile; i++) {
			long pos = raf.getFilePointer();
			raf.seek(offsets[i]);
			raf.writeLong(pos);
			raf.seek(pos);
			fis = new FileInputStream(listFile.get(i));
			while ((bytesRead = fis.read(buffer)) != -1) {
				raf.write(buffer, 0, bytesRead);
			}
			fis.close();
		}
		raf.close();
	}

	private List<File> getListFile(String folder) {
		Objects.requireNonNull(folder, "Folder path cannot be null");
		if (folder.isEmpty()) {
			throw new IllegalArgumentException("Folder path cannot be empty");
		}
		File directory = new File(folder);
		if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
			throw new IllegalArgumentException("Folder not exits, not directory or cannot read" + directory);
		}
		File[] files = directory.listFiles(File::isFile);
		List<File> listFile = new ArrayList<>();
		if (files != null) {
			Collections.addAll(listFile, files);
		}
		return listFile;
	}

	private void unPack(String packedFile, String extractFile, String dest) {
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "r")) {
			int numberOfFile = raf.readInt();
			long[] posOffsets = new long[numberOfFile];
			FileOutputStream fos;
			for (int i = 0; i < numberOfFile; i++) {
				long currentPos = raf.getFilePointer();
				int nameLength = raf.readInt();
				byte[] bytesName = new byte[nameLength];
				raf.readFully(bytesName);
				String fileName = new String(bytesName, StandardCharsets.UTF_8);
				long sise = raf.readLong();
				long pos = raf.readLong();
				if (fileName.equalsIgnoreCase(extractFile)) {
					raf.seek(pos);
					fos = new FileOutputStream(dest);
					copyFrom(raf, fos, sise);
					fos.close();

				}
				raf.seek(currentPos+4 + nameLength + 8 + 8);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void copyFrom(RandomAccessFile raf, FileOutputStream fos, long sise) throws IOException {
		int bytesToRead;
		int bytesRead;
		byte[] buffer = new byte[102400];
		int remain = (int) sise;
		while (remain > 0) {
			bytesToRead = remain > buffer.length ? buffer.length : remain;
			bytesRead = raf.read(buffer, 0, bytesToRead);
			if (bytesRead == -1)
				break;
			fos.write(buffer, 0, bytesRead);
			remain -= bytesRead;
		}

	}

	public static void main(String[] args) throws IOException {
		PUF_Ver2 puf_ver2 = new PUF_Ver2();
		String folder = "E:\\SJF";
		String packedFile = "E:\\store.txt";
		String extractFile = "1. Intro_Subject.pptx";
		String dest = "E:\\1. Intro_Subject.pptx";
//		puf_ver2.pack(folder, packedFile);
		puf_ver2.unPack(packedFile, extractFile, dest);
	}

}
