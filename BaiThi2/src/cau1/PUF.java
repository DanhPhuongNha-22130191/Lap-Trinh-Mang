package cau1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PUF {
	private void pack(String folder, String extension, String packedFile) {
		List<File> listFiles = getListFiles(folder);
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
			int num = listFiles.size();
			raf.setLength(0);
			raf.writeInt(num);
			List<Long> offsets = new ArrayList<Long>();
			for (File file : listFiles) {
				offsets.add(raf.getFilePointer());
				raf.writeLong(0);
				raf.writeUTF(file.getName()+extension);
			}
			for (int i = 0; i < num; i++) {
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
			throw new RuntimeException("File not exist!");
		File[] files = file.listFiles(File::isFile);
		if (files != null)
			for (File f : files)
				listFiles.add(f);
		return listFiles;
	}

	private void unpack(String packedFile, String extractFile, String destFile)
			throws FileNotFoundException, IOException {
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "r")) {
			int num = raf.readInt();
			for (int i = 0; i < num; i++) {
				long dataPos = raf.readLong();
				String name = raf.readUTF();
				if(extractFile.equalsIgnoreCase(name)) {
					raf.seek(dataPos);
				long size=	raf.readLong();
					try(FileOutputStream fos = new FileOutputStream(destFile)){
						readFrom(raf,fos,size);
					}break;
				}
			}
		}
	}

	private void readFrom(RandomAccessFile raf, FileOutputStream fos, long size) throws IOException {
		int remain = (int) size;
		byte[] buffer = new byte[102400];
		while(remain>0) {
			int bytesToRead = remain> buffer.length?buffer.length:remain;
			int bytesRead = raf.read(buffer, 0, bytesToRead);
			if(bytesRead==-1) break;
			fos.write(buffer, 0, bytesRead);
			remain-=bytesRead;
		}
		
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		PUF puf = new PUF();
		String folder = "E:\\test";
		String extension = "_noah";
		String packedFile = "E:\\pack.txt";
		String extractFile = "3. Core topic 1_update.pptx"+extension;
		String destFile = "E:\\dest.pptx";
		puf.pack(folder, extension, packedFile);
		puf.unpack(packedFile, extractFile, destFile);
	}

}
