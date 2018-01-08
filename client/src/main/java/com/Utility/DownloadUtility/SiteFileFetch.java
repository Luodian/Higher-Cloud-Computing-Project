package com.Utility.DownloadUtility;

import java.io.File;

public class SiteFileFetch extends Thread {
    private String siteURL; //网站URL
    private String filePath; //保存的文件路径
    private String fileName; //保存的文件名
    private int splitter; //文件分割数量
    private long[] startPos; // 开始位置
    private long[] endPos; // 结束位置
    private FileSplitterFetch[] fileSplitterFetch; // 子线程对象
    private long fileLength; // 文件长度
    private long startPosition; //文件起始位置(包含)
    private long endPosition; //文件结束位置(不包含)


    public SiteFileFetch(String url, String path, String name, long startP, long endP, int split) {
        siteURL = url;
        filePath = path;
        fileName = name;
        splitter = split;
        startPos = new long[splitter];
        endPos = new long[splitter];
        startPosition = startP;
        endPosition = endP;
    }

    public void run() {
        // 获得文件长度
        // 分割文件
        // 实例 FileSplitterFetch
        // 启动 FileSplitterFetch 线程
        // 等待子线程返回
        try {
            startPos = Utility.fileSplit(startPosition, endPosition, splitter);
            if (startPos==null){
                Utility.log("File invalided!");
            }
            else {
                for (int i = 0; i < endPos.length - 1; i++) {
                    endPos[i] = startPos[i + 1];
                }
                endPos[endPos.length - 1] = endPosition;
            }

            // 启动子线程
            fileSplitterFetch = new FileSplitterFetch[startPos.length];
            for (int i = 0; i < startPos.length; i++) {
                fileSplitterFetch[i] = new FileSplitterFetch(siteURL, filePath + File.separator + fileName,
                        startPos[i], endPos[i], startPosition , i);
                if (i == 4) System.out.println("??");
                fileSplitterFetch[i].start();
            }

            // 是否结束 while 循环
            for (int i = 0; i < startPos.length; i++) {
                fileSplitterFetch[i].join();
            }
            Utility.log("File download finished");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

