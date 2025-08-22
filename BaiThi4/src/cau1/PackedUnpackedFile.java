package cau1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PackedUnpackedFile {
	private void unpack(String packedFile, String extractFile, String destFile) {
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "r")) {
			int numOfFile = raf.readInt();
			long metadataLength = raf.readLong();
			long metadataStart = raf.length() - metadataLength;
			raf.seek(metadataStart);
			for (int i = 0; i < numOfFile; i++) {
				long dataPos = raf.readLong();
				long size = raf.readLong();
				String name = raf.readUTF();
				if (extractFile.equalsIgnoreCase(name)) {
					raf.seek(dataPos);
					try (FileOutputStream fos = new FileOutputStream(destFile)) {
						writeTo(raf, fos, size);
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeTo(RandomAccessFile raf, FileOutputStream fos, long size) throws IOException {
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

	private void pack(String folder, String packedFile) {
		List<File> listFiles = getListFiles(folder);
		int numOfFile = listFiles.size();
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
			raf.setLength(0);
			raf.writeInt(numOfFile);
			raf.writeLong(0); // metadata
			List<Long> dataPos = new ArrayList<Long>();
			for (int i = 0; i < numOfFile; i++) {
				dataPos.add(raf.getFilePointer());
				try (FileInputStream fis = new FileInputStream(listFiles.get(i))) {
					copyFrom(fis, raf);
				}
			}
			long startMetadata = raf.getFilePointer();
			for (int i = 0; i < numOfFile; i++) {
				raf.writeLong(dataPos.get(i));
				raf.writeLong(listFiles.get(i).length());
				raf.writeUTF(listFiles.get(i).getName());
			}
			long endMetadata = raf.getFilePointer();
			long lengthMetadata = endMetadata - startMetadata;
			raf.seek(4);
			raf.writeLong(lengthMetadata);
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
			throw new IllegalArgumentException("File not exist");
		if (!file.isDirectory())
			throw new IllegalArgumentException("File is not directory");
		File[] files = file.listFiles(File::isFile);
		if (files != null)
			for (File f : files) {
				listFiles.add(f);
			}
		return listFiles;
	}

	public static void main(String[] args) {
		PackedUnpackedFile puf = new PackedUnpackedFile();
		String folder = "E:\\test";
		String packedFile = "E:\\p.txt";
		String extractFile = "7B. Google FireBase-MultimediaAndroid.pptx";
		String destFile = "E:\\d.pptx";
		puf.pack(folder, packedFile);
		puf.unpack(packedFile, extractFile, destFile);
	}

}
