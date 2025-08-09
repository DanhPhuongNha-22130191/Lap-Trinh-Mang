package raf_students;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class StudentApp {
	private RandomAccessFile raf;
	private List<Student> students;

	private int numOfStudent, recSize, nameLength;

	private final int HEADER = 8;
	private final int NAME_LENGTH = 25;

	public StudentApp(String dest, List<Student> students) throws IOException {
		this.raf = new RandomAccessFile(dest, "rw");
		this.students = students;
		if (raf.length() != 0) {
			numOfStudent = raf.readInt();
			recSize = raf.readInt();
		} else {
			numOfStudent = 0;
			recSize = NAME_LENGTH * 2 + 4 + 4 + 8;
			raf.writeInt(numOfStudent);
			raf.writeInt(recSize);
		}
		nameLength = (recSize - 4 - 4 - 8) / 2;
	}

	public void save() throws IOException {
		for (Student student : students) {
			add(student);
		}
	}

	private void add(Student student) throws IOException {
		student.writeStudent(raf, nameLength);
		numOfStudent++;
		raf.seek(0);
		raf.writeInt(numOfStudent);
		raf.seek(raf.length());

	}

	private Student getStudent(int index) throws IOException {
		int pos = HEADER + recSize * index;
		raf.seek(pos);
		Student st = new Student();
		st.readStudent(raf, nameLength);
		return st;
	}

	public void updateStudent(int index, Student st) throws IOException {
		int pos = HEADER + recSize * index;
		raf.seek(pos);
		st.writeStudent(raf, nameLength);
	}

	public Student findById(int sid) throws IOException {
		for (int i = 0; i < numOfStudent; i++) {
			raf.seek(HEADER + recSize * i);
			if (raf.readInt() == sid)
				return getStudent(i);
		}
		return null;
	}

	public List<Student> readAll() throws IOException {
		List<Student> list = new ArrayList<>();
		for (int i = 0; i < numOfStudent; i++) {
			list.add(getStudent(i));
		}
		return list;
	}

	public static void main(String[] args) throws IOException {
		Student st1 = new Student(1, "Tran Nam Anh", 2004, 9);
		Student st2 = new Student(2, "Bach Nguyet Quang", 2004, 9);
		Student st3 = new Student(3, "Tinh Ai Thien Thu", 2004, 8);
		List<Student> students = new ArrayList<>();
		students.add(st1);
		students.add(st2);
		students.add(st3);
		String dest = "E:\\student.txt";
		StudentApp app = new StudentApp(dest, students);
		
//		app.save();
		
//		System.out.println(app.getStudent(2));
		
		List<Student> list = app.readAll();
		for (Student student : list) {
			System.out.println(student);
		}
		
//		app.updateStudent(2, st3);
//		System.out.println(app.getStudent(0));
		
//		System.out.println(app.findById(3));

	}
}
