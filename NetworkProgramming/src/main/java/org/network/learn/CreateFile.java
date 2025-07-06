package org.network.learn;

import java.io.*;

public class CreateFile {
    public void create(String path) throws FileNotFoundException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read(String path) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    public void write(String path, String content) throws IOException {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(path,true))) {
            write.write(content);
        }
    }
    public void deleteContent(String path) throws IOException {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(path))) {
            write.write("");
        }
    }
    public static void main(String[] args) throws IOException {
        CreateFile c = new CreateFile();
        File f = new File("E:\\hello.txt");
//        System.out.println(f.length());
        c.write("E:\\hello.txt","Baby bé iu kìn chá nà ");
//        c.deleteContent("E:\\hello.txt");
        System.out.println(c.read("E:\\hello.txt"));
    }
}
