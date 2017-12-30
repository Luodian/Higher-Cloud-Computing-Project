import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

@WebServlet(name = "GetMachineInfoServlet")
public class GetMachineInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String machineId = request.getParameter("machine_id");
        if (machineId == null) {
            response.sendError(404);
            return;
        }
        DataBaseConnector connector = new DataBaseConnector();
        Map<String, String> machineInfo = connector.getMachineInfo(machineId, null);
        if (machineInfo == null) {
            response.sendError(404);
        } else {
            machineInfo.remove("user_id");
            machineInfo.remove("public_key");
            PrintWriter writer = response.getWriter();
            JSONObject jsonObject = new JSONObject(machineInfo);
            writer.print(jsonObject.toString());
        }
    }
}
