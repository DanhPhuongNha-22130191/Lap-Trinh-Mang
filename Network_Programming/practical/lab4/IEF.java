package lab4;

import java.io.*;
import java.util.*;

public class IEF {
	private List<Student> listStudents;

	private List<Student> loadData(File stFile, File gradeFile, String charset) throws IOException {
		listStudents = new ArrayList<Student>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stFile), charset))) {
			br.read();
			String line, name;
			int sid, bYear;
			while ((line = br.readLine()) != null) {
				List<String> params = new ArrayList<String>();
				StringTokenizer token = new StringTokenizer(line, "\t");
				sid = Integer.parseInt(token.nextToken());
				while (token.hasMoreTokens()) {
					params.add(token.nextToken());
				}
				name = params.get(0);
				bYear = Integer.parseInt(params.get(1));
				listStudents.add(new Student(sid, name, bYear));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(gradeFile), charset))) {
			br.read();
			String line;
			double sum, average;
			int sid;
			while ((line = br.readLine()) != null) {
				sum = 0;
				average = 0;
				List<Double> grades = new ArrayList<>();
				StringTokenizer token = new StringTokenizer(line, "\t");
				sid = Integer.parseInt(token.nextToken());
				while (token.hasMoreTokens()) {
					grades.add(Double.parseDouble(token.nextToken()));
				}
				for (int i = 0; i < grades.size(); i++) {
					sum += grades.get(i);
				}
				average = Math.round((sum * 100.0) / grades.size()) / 100.0;
				for (Student st : listStudents) {
					if (sid == st.getSid())
						st.setGrade(average);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listStudents;
	}

	public void export(List<Student> list, String textFile, String charset)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(textFile),charset), true);
		for (Student student : list) {
			int sid = student.getSid();
			String name = student.getName();
			int bYear = student.getbYear();
			double grade = student.getGrade();
			pw.print(sid + "\t");
			pw.print(name + "\t");
			pw.print(bYear + "\t");
			pw.println(grade);
		}
		pw.close();
	}

	public static void main(String[] args) throws IOException {
		File stFile = new File("E:\\st.txt");
		File gradeFile = new File("E:\\grade.txt");
		String UTF_16BE = "UTF-16BE";
		String UTF_8 = "UTF-8";
		String destFile = "E:\\dest.txt";
		IEF ief = new IEF();
		List<Student> list = ief.loadData(stFile, gradeFile, UTF_16BE);
		ief.export(list, destFile, UTF_8);
	}

}
