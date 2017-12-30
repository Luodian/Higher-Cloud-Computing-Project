import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LogInServlet")
public class LogInServlet extends HttpServlet {
    private final static String encoding = "utf8";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user_id = request.getParameter("user_id");
        String email = request.getParameter("user_email");
        String passwordString = request.getParameter("passwd");
        byte[] password;
        if ((user_id == null && email == null) || passwordString == null) {
            PrintWriter out = response.getWriter();
            out.print("{authorized:0}");
            return;
        } else {
            password = passwordString.getBytes(LogInServlet.encoding);
        }
        DataBaseConnector connector = new DataBaseConnector();
        boolean authResult = connector.checkPassword(user_id, email, password);
        if (authResult) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("authorized", "1");
            Map<String, String> userInfoMap = connector.getUserInfo(user_id, email);
            jsonObject.put("user_id", userInfoMap.get("user_id"));
            jsonObject.put("email", userInfoMap.get("email"));
            PrintWriter out = response.getWriter();
            out.print(jsonObject.toString());
        } else {
            PrintWriter out = response.getWriter();
            out.print("{authorized:0}");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }
}
