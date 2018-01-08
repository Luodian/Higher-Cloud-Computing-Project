package com.Utility.DownloadUtility;
import java.io.*;
import java.net.*;

public class FileSplitterFetch extends Thread {
    private String sourceURL;  // 文件地址
    private long startPos;  // 文件起始地址(包含)
    private long endPos;  // 文件结束地址(不包含)
    private int threadID;  // 线程编号
    private FileAccess fileAccessInterface ;  // 文件读写类
    private long totalStartPos;  // 总起始位置
    private static final int bufferSize = 1024;

    FileSplitterFetch(String sourceURL, String fileName, long startPos, long endPos, long totalStartPos, int threadID) throws IOException {
        this.sourceURL = sourceURL;
        this.startPos = startPos;
        this.endPos = endPos;
        this.totalStartPos = totalStartPos;
        this.threadID = threadID;
        this.fileAccessInterface = new FileAccess(fileName, this.startPos - this.totalStartPos);
    }

    public void run() {
        System.out.println(this.startPos+ ":" + this.endPos);
        while (this.startPos < this.endPos) {
            try {
                URL url = new URL(this.sourceURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "NetFox");
                String requestProperty = "bytes=" + startPos + "-";
                httpConnection.setRequestProperty("RANGE", requestProperty);
                Utility.log(String.format("Thread %d start executing with start index %s", this.threadID, this.startPos));
                InputStream input = httpConnection.getInputStream();
                byte[] buffer = new byte[FileSplitterFetch.bufferSize];
                int readBytesAmount;
                while ((readBytesAmount = input.read(buffer, 0, FileSplitterFetch.bufferSize)) > 0 && startPos < endPos) {
                    this.startPos += this.fileAccessInterface.write(buffer, 0, readBytesAmount + this.startPos >= this.endPos ? (int)(this.endPos - this.startPos) : readBytesAmount);
                }
                Utility.log(String.format("Thread %d finished", this.threadID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
