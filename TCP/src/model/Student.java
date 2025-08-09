package model;

public class Student {
	private int sid;
	private String name;
	private double grade;

	public Student(int sid, String name, double grade) {
		this.sid = sid;
		this.name = name;
		this.grade = grade;
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

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return sid + "\t" + name + "\t" + grade;
	}

}
