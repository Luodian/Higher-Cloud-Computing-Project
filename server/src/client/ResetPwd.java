package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResetPwd extends HttpServlet {
  
  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String user_id = request.getParameter("user_id");
    String pwd = request.getParameter("pwd");
    DBcrud conn = new DBcrud();
    String result = "{\"pwd_reset\":\"0\"}";
    if (AuxiliaryTools.checkValidParameter(pwd) && AuxiliaryTools.checkValidParameter(user_id)) {  
    // 写数据库并得到操作结果
      if (conn.updatePwd(Integer.parseInt(user_id), pwd)==true) {
        result = "{\"pwd_reset\":\"1\"}";
        conn.showPwd(Integer.parseInt(user_id));
      }
      else {
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
