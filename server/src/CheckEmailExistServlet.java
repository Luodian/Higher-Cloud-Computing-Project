import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CheckEmailExistServlet")
public class CheckEmailExistServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email_addr");
        if (email == null) {
            response.sendError(404);
            return;
        }
        DataBaseConnector connector = new DataBaseConnector();
        boolean result = connector.checkEmailExist(email);
        JSONObject jsonObject = new JSONObject();
        if (result) {
            jsonObject.put("email_exists", "1");
        } else {
            jsonObject.put("email_exists", "0");
        }
        PrintWriter out = response.getWriter();
        out.print(jsonObject.toString());
    }
}
