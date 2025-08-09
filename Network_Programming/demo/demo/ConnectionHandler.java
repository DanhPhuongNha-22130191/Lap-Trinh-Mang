package demo;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

public class ConnectionHandler extends Thread {
    private Socket socket;
    private BufferedReader netIn;
    private PrintWriter netOut;

    private String com, param;
    private String lastUserName = null;
    private boolean isLogin = false;

    private Dao dao;

    public ConnectionHandler(Socket socket) throws IOException, ClassNotFoundException, SQLException {
        this.socket = socket;
        netIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        netOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        dao = new Dao();
    }

    public void run() {
        try {
            String line, res;
            netOut.println("Welcome...");

            // === LOGIN PHASE ===
            while (!isLogin) {
                line = netIn.readLine();
                if (line == null || "EXIT".equalsIgnoreCase(line)) break;

                requestAnalyze(line);
                res = "";

                try {
                    switch (com) {
                        case "USER":
                            if (dao.checkUserName(param)) {
                                res = "OK user name";
                                lastUserName = param;
                            } else {
                                res = "ERR user name";
                            }
                            break;

                        case "PASS":
                            if (lastUserName == null) {
                                res = "ERR invalid user name";
                            } else {
                                if (dao.login(lastUserName, param)) {
                                    res = "OK login success";
                                    isLogin = true;
                                } else {
                                    res = "ERR login";
                                }
                            }
                            break;

                        default:
                            res = "ERR invalid command";
                            break;
                    }
                } catch (SQLException e) {
                    res = "ERR database error";
                }

                netOut.println(res);
            }

            // === COMMAND PHASE AFTER LOGIN ===
            while (isLogin) {
                line = netIn.readLine();
                if (line == null || "EXIT".equalsIgnoreCase(line)) break;

                requestAnalyze(line);
                res = "";

                try {
                    List<Student> list;
                    switch (com) {
                        case "FID":
                            list = dao.findById(Integer.parseInt(param));
                            res = makeResponse(list);
                            break;
                        case "FBN":
                            list = dao.findByName(param);
                            res = makeResponse(list);
                            break;

                        default:
                            res = "ERR unknown command";
                            break;
                    }
                } catch (SQLException e) {
                    res = "ERR database error";
                } catch (NumberFormatException e) {
                    res = "ERR invalid ID format";
                }
                
                netOut.println(res);
            }

            dao.dbClose();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestAnalyze(String line) {
        StringTokenizer st = new StringTokenizer(line);
        com = st.nextToken().toUpperCase();
        param = line.substring(com.length()).trim();
    }

    private String makeResponse(List<Student> list) {
        if (list == null || list.isEmpty()) {
            return "No result found.";
        }
        StringBuilder sb = new StringBuilder();
        for (Student s : list) {
            sb.append(s).append("\n");
        }
        sb.append(".\r\n");
        return sb.toString().trim();
    }
}
