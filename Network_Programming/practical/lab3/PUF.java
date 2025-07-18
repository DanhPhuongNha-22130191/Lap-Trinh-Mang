package lab3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PUF {
	private void pack(String folder, String packedFile) {
		File src = new File(folder);
		if (!src.exists()) {
			System.err.println("File not exists!");
			return;
		}
		File[] dir = src.listFiles(file -> !file.isDirectory());
		if (dir == null || dir.length == 0) {
			System.err.println("Unable to open the drum folder or folder!");
			return;
		}
		try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
			raf.setLength(0);
			int count = dir.length;

			// Temp header
			raf.writeInt(count);
			List<FileInfo> info = new ArrayList<>();
			for(File f: dir) {
				raf.writeLong(0);
				raf.writeLong(0);
				byte[] nameBytes = f.getName().getBytes("UTF-8");
				raf.writeInt(nameBytes.length);
				raf.write(nameBytes);
				info.add(new FileInfo(0, 0,f.getName()));
			}
			for (int i = 0; i < dir.length; i++) {
				File f = dir[i];
				FileInfo fi = info.get(i);
				fi.position = raf.getFilePointer();
				try(RandomAccessFile randomAF = new RandomAccessFile(f, "r")){
					int bytesRead;
					byte[] buffer = new byte[10240];
					long total=0;
					while((bytesRead=randomAF.read(buffer))!=-1) {
						raf.write(buffer,0,bytesRead);
						total+=bytesRead;
					}
					fi.fileSize = total;
				}
			}
			raf.seek(4);
			for(FileInfo infor: info) {
				raf.writeLong(infor.position);
				raf.writeLong(infor.fileSize);
				byte[] nameBytes = infor.fileName.getBytes("UTF-8");
				raf.writeInt(nameBytes.length);
				raf.write(nameBytes);
			}
System.out.println("Hoàn tất! ");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		PUF puf = new PUF();
		String folder = "E:\\TEMP";
		String packedFile = "E:\\store.txt";
		puf.pack(folder, packedFile);
	}

}
