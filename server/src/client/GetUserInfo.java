package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetUserInfo extends HttpServlet {
 
  private static final long serialVersionUID = 1L;

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userId = request.getParameter("user_id");
    DBcrud conn = new DBcrud();
    JSONObject jsa = new JSONObject();
    JSONArray um = new JSONArray();
    if (AuxiliaryTools.checkValidParameter(userId)){
      jsa = (conn.getUserInfor(userId));
      if ((um = conn.userMachine(userId)).size()>0) {
        jsa.put("machine_list",um);
      }
    }
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out;
      out = response.getWriter();
      out.println(jsa);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendError(403);

  }
}
