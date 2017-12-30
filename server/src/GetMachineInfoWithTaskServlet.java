import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet(name = "GetMachineInfoWithTaskServlet")
public class GetMachineInfoWithTaskServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String taskId = request.getParameter("task_id");
        if (taskId == null) {
            response.sendError(404);
            return;
        }
        DataBaseConnector connector = new DataBaseConnector();
        List<Map<String, String>> resultList = connector.getMachineWithTask(taskId);
        if (resultList == null) {
            response.sendError(404);
        } else {
            JSONArray jsonArray = new JSONArray();
            resultList.forEach(jsonArray::put);
            PrintWriter writer = response.getWriter();
            writer.print(jsonArray.toString());
        }
    }
}
