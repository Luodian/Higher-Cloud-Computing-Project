package client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class test extends HttpServlet{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public void init(){
    try {
      super.init();
    } catch (ServletException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   }
   
  public void service(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
    SysNetWork.getIdlePort();
    request.getRequestDispatcher("/success.jsp").forward(request,response);  
   }
  
}