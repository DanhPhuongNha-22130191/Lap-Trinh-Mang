package lab3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSplitterJoiner {
	private String suffix(int partNumber) {
		String suffix;
		if (partNumber < 10) {
			suffix = ".00" + partNumber;
		} else if (partNumber < 100) {
			suffix = ".0" + partNumber;
		} else {
			suffix = "." + partNumber;
		}
		return suffix;
	}

	public void split(String source, int pSize) {
		try (FileInputStream fis = new FileInputStream(source)) {
			byte[] buffer = new byte[pSize];
			int bytesRead;
			int partNumber = 1;
			while ((bytesRead = fis.read(buffer)) != -1) {
				String pathFileName = source + suffix(partNumber);
				try (FileOutputStream fos = new FileOutputStream(pathFileName)) {
					fos.write(buffer, 0, bytesRead);
				}
				partNumber++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean delete(String path) {
		File file = new File(path);
		File[] list = file.listFiles();
		for (File f : list) {
			if (f.isDirectory()) {
				f.getAbsolutePath();
			}else {
				f.delete();
			}
		}
		return file.delete();

	}

	public static void main(String[] args) {
//		System.out.println(new FileSplitterJoiner().suffix(12));
		new FileSplitterJoiner().split("E:\\test.pdf", 1024*100);
//		System.out.println(new FileSplitterJoiner().delete("E:\\"));
	}
}
