package com.Utility.DownloadUtility;

import org.apache.ignite.Ignite;

import java.io.Serializable;

import static com.Utility.IgniteUtl.IgniteUtility.startDownloadIgnite;

public class test implements Serializable {
	public static void main (String[] args) {
		try {
			///Users/luodian/Desktop/a.jpg
//			Ignite ignite = startDownloadIgnite ("https://www.python.org/ftp/python/3.6.4/python-3.6.4-embed-amd64.zip","/Users/luodian/Desktop/","python-3.6.4-embed-amd64.zip","A", 3);
			/// /			mod_test (16132, 5);
		} catch (Exception e) {
            e.printStackTrace();
        }
    }
}