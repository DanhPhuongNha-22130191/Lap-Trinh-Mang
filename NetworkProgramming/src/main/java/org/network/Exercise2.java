package org.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Exercise2 {
    private File currentDir;
    private final BufferedReader reader;

    private enum Command {
        EXIT, CD, DELETE, DIR;

        public static Command fromString(String command) {
            try {
                return valueOf(command.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public Exercise2(String path) {
        this.currentDir = validateDirectory(path);
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private File validateDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + path);
        }
        return dir;
    }

    public void run() {
        try {
            while (true) {
                if (!processCommand()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        } finally {
            closeReader();
        }
    }

    private boolean processCommand() throws IOException {
        System.out.print(currentDir.getAbsolutePath() + "> ");
        String input = reader.readLine();
        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        List<String> tokens = Arrays.stream(input.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            return true;
        }

        Command command = Command.fromString(tokens.get(0));
        if (command == null) {
            System.out.println("Unknown command: " + tokens.get(0));
            return true;
        }

        List<String> args = tokens.subList(1, tokens.size());
        return executeCommand(command, args);
    }

    private boolean executeCommand(Command command, List<String> args) {
        switch (command) {
            case EXIT:
                System.out.println("Exiting shell...");
                return false;
            case CD:
                return changeDirectory(args);
            case DELETE:
                return deleteItem(args);
            case DIR:
                listDirectory();
                return true;
            default:
                System.out.println("Unsupported command");
                return true;
        }
    }

    private void listDirectory() {
        File[] files = currentDir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("<empty>");
            return;
        }

        Arrays.stream(files)
                .sorted(Comparator.comparing(File::isDirectory).reversed()
                        .thenComparing(f -> f.getName().toUpperCase()))
                .forEach(f -> System.out.println(f.getName().toUpperCase()));
    }

    private boolean deleteItem(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Usage: delete <name>");
            return true;
        }

        File file = new File(currentDir, args.get(0));
        if (!file.exists()) {
            System.out.println("Not found: " + args.get(0));
            return true;
        }

        boolean deleted = deleteRecursive(file);
        System.out.println(deleted ? "Deleted: " + args.get(0) : "Failed to delete: " + args.get(0));
        return true;
    }

    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (contents != null) {
                Arrays.stream(contents).forEach(this::deleteRecursive);
            }
        }
        return file.delete();
    }

    private boolean changeDirectory(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Usage: cd <directory>");
            return true;
        }

        String target = args.get(0);
        if (target.equals("..")) {
            File parent = currentDir.getParentFile();
            if (parent == null) {
                System.out.println("No parent directory");
                return true;
            }
            currentDir = parent;
        } else {
            File newDir = new File(currentDir, target);
            if (!newDir.exists() || !newDir.isDirectory()) {
                System.out.println("Directory not found: " + target);
                return true;
            }
            currentDir = newDir;
        }
        return true;
    }

    private void closeReader() {
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing reader: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new Exercise2("E:/").run();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}