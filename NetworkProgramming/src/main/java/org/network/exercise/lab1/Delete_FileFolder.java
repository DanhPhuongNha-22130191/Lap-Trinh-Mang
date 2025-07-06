package org.network.exercise.lab1;

import java.io.File;

public class Delete_FileFolder {
    /**
     * Delete file and/or directory in the specified directory tree.
     *
     * @param path            The root directory path where deletion should begin
     * @param isDeleteFolders If true, both files and directories will be deleted.
     *                        If false, only files will be deleted.
     * @return true if the deletion was successful(or If not thing needed to be deleted); false if the path
     * not exist.
     */
    public boolean delete(String path, boolean isDeleteFolders) {
        File file = new File(path);
        if (!file.exists()) return false;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    delete(f.getAbsolutePath(), isDeleteFolders);
                } else {
                    f.delete();
                }
            }
        }
        return isDeleteFolders ? file.delete() : true;
    }

    public static void main(String[] args) {
        String path = "E:\\WareHouse";
        System.out.println(new Delete_FileFolder().delete(path, true));
    }
}
