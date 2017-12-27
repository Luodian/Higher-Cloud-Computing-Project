package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TaskFinish extends HttpServlet {

  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /*
     * machine_id：机器ID user_id：用户ID task_id：任务ID
     */
    int status = 0;
    String actor = request.getParameter("actor");
    String taskId = request.getParameter("task_id");
    DBcrud conn = new DBcrud();
    String result = "{\"task_fin\":\"0\"}";

    if (AuxiliaryTools.checkValidParameter(actor) && AuxiliaryTools.checkValidParameter(taskId)) {
      if (actor.trim().equals("master")) {
        if (conn.updateTaskStatus( Integer.parseInt(taskId), status) == true) {
          result = "{\"task_fin\":\"1\"}";
        } 
      }else if (actor.trim().equals("slave")) {
          String machine_id = request.getParameter("machine_id");
          if (AuxiliaryTools.checkValidParameter(machine_id)) {
            if (conn.updateTaskAssignStatus( Integer.parseInt(taskId), status, Integer.parseInt(machine_id)) == true) {
              result = "{\"task_fin\":\"1\"}";
            }
          }
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