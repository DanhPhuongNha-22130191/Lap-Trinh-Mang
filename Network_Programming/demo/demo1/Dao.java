package demo1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Dao {
	private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private static final String DB_PATH = "D:/Programmings/Eclipse/Lap-Trinh-Mang/lab6.accdb";
	private static final String URL = "jdbc:ucanaccess://" + DB_PATH;
	private Connection connection;

	public Dao() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		connection = DriverManager.getConnection(URL);
	}

	public boolean checkUserName(String username) throws SQLException {
		String sql = "SELECT* FROM User WHERE username = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		return rs.next();
	}

	public boolean login(String username, String password) throws SQLException {
		String sql = "SELECT* FROM User WHERE username = ? AND password =?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, username);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();
		return rs.next();
	}

	public List<Student> findById(int sid) throws SQLException {
		List<Student> list = new ArrayList<>();
		String sql = "SELECT* FROM Student WHERE sid = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, sid);
		ResultSet rs = stmt.executeQuery();
		String name;
		double grade;
		while (rs.next()) {
			name = rs.getString("name");
			grade = rs.getInt("grade");
			list.add(new Student(sid, name, grade));
		}
		return list;
	}

	public List<Student> findByName(String name) throws SQLException {
		List<Student> list = new ArrayList<>();
		String sql = "SELECT* FROM Student WHERE name LIKE ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, "%"+name);
		ResultSet rs = stmt.executeQuery();
		int sid;
		double grade;
		while (rs.next()) {
			sid = rs.getInt("sid");
			name = rs.getString("name");
			grade = rs.getInt("grade");
			list.add(new Student(sid, name, grade));
		}
		return list;
	}

	public void dbClose() throws SQLException {
		connection.close();
	}

//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		Dao dao = new Dao();
//		List<Student> list = dao.findById(111);
//		for(Student s : list) {
//			System.out.println(s);
//		}
//		System.out.println(dao.login("thong","123"));
//	}
}
