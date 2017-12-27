import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UpdateOnlineTimeServlet")
public class UpdateOnlineTimeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("user_id");
        String deltaString = request.getParameter("delta");
        if (userId == null || deltaString == null) {
            response.sendError(404);
            return;
        }
        int delta = Integer.parseInt(deltaString);
        DataBaseConnector connector = new DataBaseConnector();
        connector.updateOnlineTime(userId, delta);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }
}
