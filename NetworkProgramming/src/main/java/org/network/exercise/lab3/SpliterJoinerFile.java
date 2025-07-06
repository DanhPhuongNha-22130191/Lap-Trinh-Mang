package org.network.exercise.lab3;

import java.io.*;

public class SpliterJoinerFile {
    public void split(String source, int pSize) {
        try (FileInputStream fis = new FileInputStream(source)) {
            byte[] buffer = new byte[pSize];
            int bytesRead;
            int partNumber = 1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                String outputFileName = source + suffix(partNumber);
                try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                    fos.write(buffer, 0, bytesRead);
                }
                partNumber++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void join(String path) {
        int prefix = path.lastIndexOf('.');
        if (prefix == -1) {
            throw new RuntimeException("Invalid file name");
        }
        String basePath = path.substring(0, prefix);
        try (FileOutputStream fos = new FileOutputStream(basePath)) {
            int partNumber = 1;
            while (true) {
                String inputFileName = basePath + suffix(partNumber);
                File file = new File(inputFileName);
                if (!file.exists()) break;
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                file.delete();
                partNumber++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String suffix(int partNumber) {
        return "." + String.format("%03d", partNumber);
    }

    public static void main(String[] args) {
//        new SpliterJoinerFile().split("E:\\hello.txt", 5);
        new SpliterJoinerFile().join("E:\\hello.txt.001");
    }
}
