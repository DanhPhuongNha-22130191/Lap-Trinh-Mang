package lab1;

import java.io.File;
import java.io.IOException;

public class FileClass {
	// xóa tất cả những gì có thể trong file/thư mục được chỉ định bởi path gồm cả
	// các thư mục con
	// trả về true nếu xóa thành công, false nếu xóa không thành công
	// ==> Nếu file hệ thống(Windown) ko xóa đc thì sao
	private boolean delHelper(String canonicalPath) throws IOException {
		File file = new File(canonicalPath);
		if (!file.exists())
			return false;
		File[] list = file.listFiles();
		if (list != null)
			for (File f : list) {
				delHelper(f.getCanonicalPath());
			}
		return file.delete();
	}
	public String mainMethod(String path) throws IOException {
		boolean res = delHelper(path);
		if(res) return "Success";
		return "Error";
	}

	public static void main(String[] args) throws IOException {
		FileClass fc = new FileClass();
		String path = "E:/AuthServiceDemo";
		System.out.println(fc.mainMethod(path));
	}
}
