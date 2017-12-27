import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "MachineOfflineServlet")
public class MachineOfflineServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String machineId = request.getParameter("machine_id");
        if (machineId == null) {
            response.sendError(404);
            return;
        }
        DataBaseConnector connector = new DataBaseConnector();
        boolean result = connector.machineOffline(machineId);
        PrintWriter writer = response.getWriter();
        if (result) {
            writer.print("{status:1}");
        } else {
            writer.print("{status:0}");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }
}
