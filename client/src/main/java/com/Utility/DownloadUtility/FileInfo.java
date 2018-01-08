package com.Utility.DownloadUtility;

import java.net.HttpURLConnection;
import java.net.URL;

public class FileInfo {
    // 获得文件长度
    public static long getFileSize(String siteUrl) {
        int nFileLength = -1;
        try {
            URL url = new URL(siteUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	        httpConnection.setRequestProperty ("User-Agent", "Highter_Compute");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode >= 400) {
                Utility.processErrorCode(responseCode);
                return -2;
            }
            String sHeader;
            for (int i = 1; ; i++) {
                sHeader = httpConnection.getHeaderFieldKey(i);
                if (sHeader != null) {
                    if (sHeader.equals("Content-Length")) {
                        nFileLength = Integer.parseInt(httpConnection.getHeaderField(sHeader));
                        break;
                    }
                } else
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utility.log(String.format("Length of the file: %d", nFileLength));
        return nFileLength;
    }
}
