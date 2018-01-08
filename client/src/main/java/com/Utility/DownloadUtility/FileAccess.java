package com.Utility.DownloadUtility;

import java.io.*;

public class FileAccess implements Serializable {
    private RandomAccessFile targetFile;

    FileAccess(String fileName, long position) throws IOException {
        this.targetFile = new RandomAccessFile(fileName, "rw");
        this.targetFile.seek(position);
    }

    public synchronized int write(byte[] buffer, int startIndex, int length) {
        int writeLength = -1;
        try {
            targetFile.write(buffer, startIndex, length);
            writeLength = length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writeLength;
    }

    public synchronized boolean seek(long position) {
        try {
            this.targetFile.seek(position);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public synchronized boolean read(byte[] buffer, int offset, int length) {
        try {
            targetFile.read(buffer, offset, length);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}