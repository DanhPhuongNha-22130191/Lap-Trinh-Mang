package cau1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PUF {
	private void pack(String folder, String packedFile) {
		List<File> listFiles = getListFiles(folder);
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
			raf.setLength(0);
			raf.writeInt(listFiles.size());
			List<Long> offsets = new ArrayList<>();
			for (File file : listFiles) {
				long hPos = raf.getFilePointer();
				offsets.add(hPos);
//				System.out.println(hPos);
				raf.writeLong(0);
				raf.writeLong(file.length());
				raf.writeUTF(file.getName());
			}
			for (int i = 0; i < listFiles.size(); i++) {
				try (FileInputStream fis = new FileInputStream(listFiles.get(i))) {
					long dataPos = raf.getFilePointer();
					raf.seek(offsets.get(i));
					raf.writeLong(dataPos);
					raf.seek(raf.length());
					copyFrom(fis, raf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void copyFrom(FileInputStream fis, RandomAccessFile raf) throws IOException {
		byte[] buffer = new byte[102400];
		int bytesRead;
		while ((bytesRead = fis.read(buffer)) != -1) {
			raf.write(buffer, 0, bytesRead);
		}
	}

	private List<File> getListFiles(String folder) {
		List<File> list = new ArrayList<>();
		File file = new File(folder);
		if (!file.exists())
			return null;
		File[] files = file.listFiles(File::isFile);
		if (files != null)
			for (File f : files) {
				list.add(f);
			}
		return list;
	}

	private void unpack(String packedFile, String extractFile, String destFile) {
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "r")) {
			int num = raf.readInt();
			for (int i = 0; i < num; i++) {
				long pos = raf.readLong();
				long size = raf.readLong();
				String name = raf.readUTF();
				if (extractFile.equalsIgnoreCase(name)) {
					try (FileOutputStream fos = new FileOutputStream(destFile)) {
						raf.seek(pos);
						copyFrom(raf, fos, size);
					}break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void copyFrom(RandomAccessFile raf, FileOutputStream fos, long size) throws IOException {
		int remain = (int) size;
		byte[] buffer = new byte[102400];
		while (remain > 0) {
			int bytesToRead = remain > buffer.length ? buffer.length : remain;
			int bytesRead = raf.read(buffer, 0, bytesToRead);
			if (bytesRead == -1)
				break;
			fos.write(buffer, 0, bytesRead);
			remain -= bytesRead;
		}

	}

	public static void main(String[] args) {
		PUF puf = new PUF();
		String folder = "E:\\test";
		String packedFile = "E:\\pack.txt";
		String extractFile = "1. Intro_Subject.pptx";
		String destFile = "E:\\dest.pptx";
//		puf.pack(folder, packedFile);
		puf.unpack(packedFile, extractFile, destFile);
	}

}
