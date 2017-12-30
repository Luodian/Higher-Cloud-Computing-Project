package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadIP extends HttpServlet {
  
  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String machine_id = request.getParameter("machine_id");
    String ip = request.getParameter("ip");
    DBcrud conn = new DBcrud();
    String result = "{\"ip_save\":\"0\"}";
    if (AuxiliaryTools.checkValidParameter(ip) && AuxiliaryTools.checkValidParameter(machine_id)) {  
    // 写数据库并得到操作结果
      if (conn.updatePublicKeyOrIP(ip, Integer.parseInt(machine_id), "ip")==true) {
        result = "{\"ip_save\":\"1\"}";
      }
    }
    
    // 响应
    response.setContentType("text/html;charset=UTF-8");
    response.setStatus(200);
    PrintWriter out;
      out = response.getWriter();
      out.println(result);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.sendError(403);
  }
}

