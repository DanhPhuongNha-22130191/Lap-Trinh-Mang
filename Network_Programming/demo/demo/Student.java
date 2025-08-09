package demo;

public class Student {
	int sid;
	String name;
	double grade;

	public Student(int sid, String name, double grade) {
		this.sid = sid;
		this.name = name;
		this.grade = grade;
	}

	@Override
	public String toString() {
		return sid + "/t" + name + "/t" + grade;
	}

}
