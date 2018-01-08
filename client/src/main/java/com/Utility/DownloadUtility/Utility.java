package com.Utility.DownloadUtility;
public class Utility {
    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(int message) {
        System.out.println(message);
    }

    public static void processErrorCode(int nErrorCode) {
        System.err.println("Error Code : " + nErrorCode);
    }

    public static long[] fileSplit(long startPosition, long endPosition, int split) {
        long fileLength = endPosition - startPosition;
        long[] splitPoint = new long[split];
        if (fileLength < 0) {
            return null;
        } else {
            for (int i = 0; i < split; i++) {
                splitPoint[i] = (i * (fileLength / split)) + startPosition;
            }
        }
        return splitPoint;
    }
}
