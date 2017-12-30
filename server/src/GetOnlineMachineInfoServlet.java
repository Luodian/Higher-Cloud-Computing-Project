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

@WebServlet(name = "GetOnlineMachineInfoServlet")
public class GetOnlineMachineInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataBaseConnector connector = new DataBaseConnector();
        List<Map<String, String>> resultList = connector.getMachineWithStatus(0);
        JSONArray jsonArray = new JSONArray();
        resultList.forEach(jsonArray::put);
        PrintWriter writer = response.getWriter();
        writer.print(jsonArray.toString());
    }
}
