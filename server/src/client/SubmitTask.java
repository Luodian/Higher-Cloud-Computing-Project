package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SubmitTask extends HttpServlet {

  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //int machine_id = Integer.parseInt(request.getParameter("machine_id"));
    String user_id = request.getParameter("user_id");
    String taskName = request.getParameter("task_name");
    //String publicKey = request.getParameter("public_key");
    DBcrud conn = new DBcrud();
    String result = "{\"task_pub\":\"0\"}";
    int taskId = -1;
    if (AuxiliaryTools.checkValidParameter(user_id) && AuxiliaryTools.checkValidParameter(taskName))
    
      taskId = conn.AddNewTask(taskName, Integer.parseInt(user_id));
      if (taskId > 0) {
        result = "{\"task_pub\":\"1\";\"task_id\":\""+taskId+"\"}";
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
