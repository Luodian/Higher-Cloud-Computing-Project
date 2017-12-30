package client;

import java.io.IOException;
import java.net.ServerSocket;

public class SysNetWork {
  public static int[] getIdlePort() {
    int ports[] = {-1,-1};
    ServerSocket serverSocket;
    try {
      serverSocket = new ServerSocket(0);
      ports[0] = serverSocket.getLocalPort();
      serverSocket.close();
      serverSocket = new ServerSocket(0);
      ports[1] = serverSocket.getLocalPort();
      serverSocket.close();
      System.out.println(ports[0]);
      System.out.println(ports[1]);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } //读取空闲的可用端口
    return ports;
  }
  

}
