//package sample;
//
//import java.io.*;
//
//public class ConsoleSimulator implements Runnable {
//
//    private volatile boolean isStop = false;
//    private static final int INFO = 0;
//    private static final int ERROR = 1;
//    private InputStream is;
//    private int type;
//    public ConsoleSimulator(InputStream is, int type) {
//        this.is = is;
//        this.type = type;
//    }
//
//    public void stop() {
//        isStop = true;
//    }
//
//    @Override
//    public void run() {
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader bfr = new BufferedReader(isr);
//        String s;
//        try{
//            while((! isStop) && ((s = bfr.readLine())!= null)) {
//                if (s.length() != 0) {
//                    if (type == INFO) {
//                        System.out.println("INFO>" + s);
//                    } else {
//                        System.out.println("ERROR>" + s);
//                    }
//                }
//                Thread.sleep(10);
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        Process process = Runtime.getRuntime().exec("./test");
//        InputStream inputStream = process.getInputStream();
//        InputStream errorStream= process.getErrorStream();
//        OutputStream outputStream = process.getOutputStream();
//        PrintWriter printWriter = new PrintWriter(outputStream,true);
//        Thread tIN = new Thread(new ConsoleSimulator(inputStream, INFO));
//        Thread tErr = new Thread(new ConsoleSimulator(errorStream, ERROR));
//        tIN.start();
//        tErr.start();
//        int result = process.waitFor();
//        new Thread() {
//            @Override
//            public void run() {
//
//            }
//        }.start();
//    }
//}