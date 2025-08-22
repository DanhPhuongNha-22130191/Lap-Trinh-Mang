package cau1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PackedUnpackedFile {
	private void pack(String folder, String extension, String packedFile) {
		List<File> listFiles = getListFiles(folder);
		int numOfFile = listFiles.size();
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
			raf.setLength(0);
			raf.writeInt(numOfFile);
			List<Long> offsets = new ArrayList<Long>();
			for (File file : listFiles) {
				offsets.add(raf.getFilePointer());
				raf.writeLong(0);
				raf.writeUTF(file.getName() + extension);
			}
			for (int i = 0; i < numOfFile; i++) {
				long dataPos = raf.getFilePointer();
				raf.seek(offsets.get(i));
				raf.writeLong(dataPos);
				raf.seek(raf.length());
				raf.writeLong(listFiles.get(i).length());
				try (FileInputStream fis = new FileInputStream(listFiles.get(i))) {
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
		List<File> listFiles = new ArrayList<>();
		File file = new File(folder);
		if (!file.exists())
			throw new IllegalArgumentException("File not exists!");
		if (!file.isDirectory())
			throw new IllegalArgumentException("File is not directory!");
		File[] files = file.listFiles(File::isFile);
		if (files != null) {
			for (File f : files) {
				listFiles.add(f);
			}
		}
		return listFiles;
	}

	private void unpack(String packedFile, String extractFile, String destFile) {
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "r")) {
			int numberOfFile = raf.readInt();
			for (int i = 0; i < numberOfFile; i++) {
				long dataPos = raf.readLong();
				String name = raf.readUTF();
				if (extractFile.equalsIgnoreCase(name)) {
					raf.seek(dataPos);
					long size = raf.readLong();
					try (FileOutputStream fos = new FileOutputStream(destFile)) {
						readFrom(raf, fos, size);
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readFrom(RandomAccessFile raf, FileOutputStream fos, long size) throws IOException {
		byte[] buffer = new byte[102400];
		int remain = (int) size;
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
		PackedUnpackedFile puf = new PackedUnpackedFile();
		String folder = "E:\\test";
		String extension = "_ext";
		String packedFile = "E:\\pack.txt";
		String extractFile = "7C. API REST - SOAP .pptx"+extension;
		String destFile = "E:\\dest.pptx";
		puf.pack(folder, extension, packedFile);
		puf.unpack(packedFile, extractFile, destFile);
	}

}
