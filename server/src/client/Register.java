package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Register extends HttpServlet {

  private static final long serialVersionUID = 1L;
 

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String nickName = request.getParameter("nick_name");
    String email = request.getParameter("email_addr");
    String pwdHash = request.getParameter("pwd");
    DBcrud conn = new DBcrud();
    String result = "{\"register_status\":\"0\"}";
    String verification_code = AuxiliaryTools.getRandomString(AuxiliaryTools.VERIFICATION_CODE_LENGTH);
    
    int userId;
    if (AuxiliaryTools.checkValidParameter(nickName) && AuxiliaryTools.checkValidParameter(email)
        && AuxiliaryTools.checkValidParameter("pwd")) {
      userId = conn.insertUserInfo(nickName.trim(), email.trim(), pwdHash.trim());
      System.out.print(userId);
      if (userId > 0) {
        conn.showPwd(userId);
        conn.updateVC(email, verification_code);
        EmailUtils.sendAccountActivateEmail(email.trim(), verification_code, userId);
        result = "{\"register_status\":\"1\";\"user_id\":\"" + userId + "\"}";
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
