package lab4;

public class Student {
	int sid;
	String name;
	int bYear;
	double grade;

	public Student(int sid, String name, int bYear) {
		this.sid = sid;
		this.name = name;
		this.bYear = bYear;
	}

	public String getName() {
		return name;
	}

	public int getbYear() {
		return bYear;
	}

	public double getGrade() {
		return grade;
	}

	public int getSid() {
		return sid;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return sid + "\t" + name + "\t" + bYear + "\t" + grade;
	}

}
