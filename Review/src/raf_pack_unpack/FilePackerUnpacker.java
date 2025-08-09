package raf_pack_unpack;

import java.io.*;
import java.util.*;

public class FilePackerUnpacker {

	public void pack(String sourceFolder, String extension, String packedFilePath) throws IOException {
		List<File> filesToPack = getListFiles(sourceFolder);
		try (RandomAccessFile raf = new RandomAccessFile(packedFilePath, "rw")) {
			raf.setLength(0);
			raf.writeInt(filesToPack.size());
			List<Long> headerPositions = new ArrayList<>();
			for (File file : filesToPack) {
				headerPositions.add(raf.getFilePointer());
				raf.writeLong(0);
				raf.writeUTF(file.getName() + extension);
			}
			for (int i = 0; i < filesToPack.size(); i++) {
				long dataPosition = raf.getFilePointer();
				raf.writeLong(filesToPack.get(i).length());
				try (FileInputStream fis = new FileInputStream(filesToPack.get(i))) {
					copyFrom(fis, raf);
				}
				raf.seek(headerPositions.get(i));
				raf.writeLong(dataPosition);
				raf.seek(raf.length());
			}
		}
	}

	private void copyFrom(FileInputStream fis, RandomAccessFile raf) throws IOException {
		byte[] buffer = new byte[102400];
		int bytesRead;
		while ((bytesRead = fis.read(buffer)) != -1) {
			raf.write(buffer, 0, bytesRead);
		}

	}

	private List<File> getListFiles(String sourceFolder) throws IOException {
		File file = new File(sourceFolder);
		if (!file.exists())
			throw new IOException("File not exists with: " + sourceFolder);
		File[] files = file.listFiles(File::isFile);
		List<File> list = new ArrayList<>();
		if (files != null) {
			for (File f : files)
				list.add(f);
		}
		return list;
	}

	public void unpack(String packedFilePath, String targetFileName, String destinationPath) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(packedFilePath, "r")) {
			int numberOfFiles = raf.readInt();
			for (int i = 0; i < numberOfFiles; i++) {
				long dataPosition = raf.readLong();
				String fileName = raf.readUTF();
				if (targetFileName.equalsIgnoreCase(fileName)) {
					raf.seek(dataPosition);
					long fileSize = raf.readLong();
					try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
						copyFrom(raf, fos, fileSize);
					}
					break;
				}
			}
		}
	}

	private void copyFrom(RandomAccessFile raf, FileOutputStream fos, long size) throws IOException {
		int remain = (int) size;
		int bytesRead;
		int bytesToRead;
		byte[] buffer = new byte[102400];
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
		FilePackerUnpacker fpu = new FilePackerUnpacker();

		String sourceFolder = "E:\\test";
		String extension = "_noah";
		String packedFile = "E:\\pack.txt";
		fpu.pack(sourceFolder, extension, packedFile);

		String targetFileName = "7C. API REST - SOAP .pptx" + extension;
		String destinationFile = "E:\\7C. API REST - SOAP .pptx";
		fpu.unpack(packedFile, targetFileName, destinationFile);
	}
}
