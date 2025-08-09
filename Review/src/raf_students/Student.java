package raf_students;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Student {
	int sid;
	String name;
	int bYear;
	double grade;

	public Student(int sid, String name, int bYear, double grade) {
		this.sid = sid;
		this.name = name;
		this.bYear = bYear;
		this.grade = grade;
	}

	public Student() {
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getbYear() {
		return bYear;
	}

	public void setbYear(int bYear) {
		this.bYear = bYear;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return sid + "\t" + name + "\t" + bYear + "\t" + grade;
	}

	public void writeStudent(RandomAccessFile raf, int nameLength) throws IOException {
		raf.writeInt(sid);
		writeName(raf, nameLength);
		raf.writeInt(bYear);
		raf.writeDouble(grade);
	}

	private void writeName(RandomAccessFile raf, int nameLength) throws IOException {
		for (int i = 0; i < nameLength; i++) {
			if (i >= name.length())
				raf.writeChar(0);
			else
				raf.writeChar(name.charAt(i));
		}
	}

	public void readStudent(RandomAccessFile raf, int nameLength) throws IOException {
		this.sid = raf.readInt();
		this.name = readName(raf, nameLength);
		this.bYear = raf.readInt();
		this.grade = raf.readDouble();
	}

	private String readName(RandomAccessFile raf, int nameLength) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nameLength; i++) {
			char c = raf.readChar();
			if (c == 0)
				continue;
			sb.append(c);
		}
		return sb.toString();
	}
}
