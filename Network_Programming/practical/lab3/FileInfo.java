package lab3;

public class FileInfo {
	long position;
	long fileSize;
	String fileName;

	public FileInfo(long position,long fileSize, String fileName) {
		this.position = position;
		this.fileSize = fileSize;
		this.fileName = fileName;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
