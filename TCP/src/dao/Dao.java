package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;

public class Dao {
	private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private static final String DB_PATH = "D:\\Programmings\\Eclipse\\Lap-Trinh-Mang\\TCP\\TCP.accdb";
	private static final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;

	public Dao() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		connection = DriverManager.getConnection(URL);
	}

	public boolean checkUserName(String username) throws SQLException {
		String sql = "SELECT * FROM User WHERE username =?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		return rs.next();
	}

	public boolean login(String username, String password) throws SQLException {
		String sql = "SELECT * FROM User WHERE username =? AND password = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, username);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();
		return rs.next();
	}

	public List<Student> findById(int sid) throws SQLException {
		List<Student> list = new ArrayList<>();
		String sql = "SELECT * FROM Student WHERE sid =?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, sid);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("sid");
			String name = rs.getString("name");
			double grade = rs.getDouble("grade");
			list.add(new Student(id, name, grade));
		}
		return list;
	}

	public List<Student> findByName(String name) throws SQLException {
		List<Student> list = new ArrayList<>();
		String sql = "SELECT * FROM Student WHERE name LIKE?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, "%" + name);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("sid");
			String studentName = rs.getString("name");
			double grade = rs.getDouble("grade");
			list.add(new Student(id, studentName, grade));
		}
		return list;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Dao dao =new Dao();
		System.out.println(dao.checkUserName("danh"));
		System.out.println(dao.login("danh","123"));
		for (Student st : dao.findByName("Nhã")) {
			System.out.println(st);
		}
	}
}
