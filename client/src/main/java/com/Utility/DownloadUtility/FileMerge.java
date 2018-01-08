package com.Utility.DownloadUtility;

public class FileMerge {
    private String[] filePathArray;
    private long[] startIndexArray;
    private long[] endIndexArray;
    private String targetFilePath;
    private int maxIndexRange;
	
	public FileMerge (String[] filePathArray, long[] startIndexArray, long[] endIndexArray, String targetFilePath) {
        this.filePathArray = filePathArray;
        this.startIndexArray = startIndexArray;
        this.endIndexArray = endIndexArray;
        this.targetFilePath = targetFilePath;
        this.maxIndexRange = 0;
        for (int index = 0; index < filePathArray.length; ++index) {
            int delta = (int)(endIndexArray[index] - startIndexArray[index]);
            if (delta > this.maxIndexRange) {
                this.maxIndexRange = delta;
            }
        }
    }

    public boolean merge() {
        try {
            FileAccess targetFileAccess = new FileAccess(targetFilePath, 0);
            byte[] buffer = new byte[this.maxIndexRange];
            for (int index = 0; index < filePathArray.length; ++index) {
                FileAccess currentFileAccess = new FileAccess(this.filePathArray[index], 0);
                int curLength = (int)(this.endIndexArray[index] - this.startIndexArray[index]);
                currentFileAccess.read(buffer, 0, curLength);
                targetFileAccess.seek(this.startIndexArray[index]);
                targetFileAccess.write(buffer, 0, curLength);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
