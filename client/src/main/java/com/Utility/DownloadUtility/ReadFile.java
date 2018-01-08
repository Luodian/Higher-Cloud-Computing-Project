package com.Utility.DownloadUtility;

import org.apache.ignite.ml.math.impls.matrix.SparseDistributedMatrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.ignite.ml.math.impls.vector.DenseLocalOnHeapVector;

public class ReadFile {
    private int rowNum;
    private int colNum;
    private String fileName;

    public int getColNum() {
        return colNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public ReadFile(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String s = in.readLine();
        String[] sSplit = s.split(" ");
        this.rowNum = Integer.parseInt(sSplit[0]);
        this.colNum = Integer.parseInt(sSplit[1]);
        this.fileName = fileName;
        in.close();
    }

    public void write(SparseDistributedMatrix mat) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String s;
        int i = 0;
        double[] d = new double[colNum];
        String[] sSplit = new String[colNum];
        in.readLine();
        while ((s = in.readLine()) != null) {
            sSplit = s.split(" ");
            for(int j = 0;j<colNum;j++){
                d[j] = Double.parseDouble(sSplit[j]);
            }
            mat.assignRow(i++, new DenseLocalOnHeapVector(d));
        }
        in.close();
    }
}
