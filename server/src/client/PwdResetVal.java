package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PwdResetVal extends HttpServlet {
  
  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userId = request.getParameter("user_id");
    String email = request.getParameter("email_addr");
    String verification_code = AuxiliaryTools.getRandomString(AuxiliaryTools.VERIFICATION_CODE_LENGTH);
    DBcrud conn = new DBcrud();
    String result = "{\"email_send\":\"0\"}";
    if (AuxiliaryTools.checkValidParameter(userId)) {  
      email = conn.findInUserInfo(userId,"user_id","email");
    }
    else if (AuxiliaryTools.checkValidParameter(email)) {
      userId = conn.findInUserInfo(email,"email","user_id");
    }
    
    if (AuxiliaryTools.checkValidParameter(email) && AuxiliaryTools.checkValidParameter(userId)) {
      conn.updateVC(email, verification_code);
      EmailUtils.sendAccountActivateEmail(email.trim(), verification_code, Integer.parseInt(userId));
      result = "{\"email_send\":\"1\";\"user_id\":\"" + userId + "\"}";
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
