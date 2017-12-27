package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Active extends HttpServlet {

  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String verification_code = request.getParameter("verification_code");
    String userId = request.getParameter("user_id");
    DBcrud conn = new DBcrud();
    String result = "{\"email_verification\":\"0\"}";
    if (AuxiliaryTools.checkValidParameter(verification_code) && AuxiliaryTools.checkValidParameter(userId)) {
        if (conn.changeActiveState(verification_code, userId)) {
          result = "{\"email_verification\":\"1\"}";
        }
    }
 // œÏ”¶
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
