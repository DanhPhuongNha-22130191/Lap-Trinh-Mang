import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ListFile {
	public void getFilesByExt(String folder, String ext) {
		List<String> listFile = new ArrayList<>();
		File file = new File(folder);
		if (!file.exists())
			return;
		File[] list = file.listFiles();
		if (list != null) {
			for (File f : list) {
				if (f.isFile() && f.getName().endsWith(ext)) {
					System.out.println(f.getAbsolutePath());
					listFile.add(f.getName());
				}
			}
		}
	}

	public void ver2(String folder, String ext) {
		File file = new File(folder);
		if (!file.exists())
			return;
		File[] list = file.listFiles(f -> f.isFile() && f.getName().endsWith(ext));
		if (list != null) {
			for (File f : list) {
				System.out.println(f.getAbsolutePath());
			}
		}
	}

	public void ver3(String folder, String ext) {
		File file = new File(folder);
		if (!file.exists())
			return;
		File[] list = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ext);
			}
		});
		if (list != null) {
			for (File f : list) {
				if (f.isFile()) {
					System.out.println(f.getAbsolutePath());
				}
			}
		}
	}

	public static void main(String[] args) {
		new ListFile().ver2("E:/AuthServiceDemo", ".json");
	}

}
