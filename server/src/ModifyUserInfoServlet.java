import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "ModifyUserInfoServlet")
public class ModifyUserInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Enumeration<String> parameterEnum = request.getParameterNames();
        Map<String, String> parameterMap = new HashMap<>();
        for (; parameterEnum.hasMoreElements(); ) {
            String curParameter = parameterEnum.nextElement();
            parameterMap.put(curParameter, request.getParameter(curParameter));
        }
        if (!parameterMap.containsKey("user_id")) {
            PrintWriter writer = response.getWriter();
            writer.print("{modify_status:0,error_description:\"no user_id\"}");
            return;
        }
        String userId = parameterMap.get("user_id");
        DataBaseConnector connector = new DataBaseConnector();
        Map<String, String> userInfoMap = new HashMap<>();
        if (parameterMap.containsKey("user_nickname")) {
            String nickname = parameterMap.get("user_nickname");
            userInfoMap.put("nickname", nickname);
        }
        boolean userUpdateResult = connector.updateUserInfo(userId, userInfoMap);
        boolean machineUpdateResult;
        if (!parameterMap.containsKey("machine_id")) {
            if (parameterMap.containsKey("machine_username") || parameterMap.containsKey("machine_nickname")
                    || parameterMap.containsKey("host_name")) {
                machineUpdateResult = false;
            } else {
                machineUpdateResult = true;
            }
        } else {
            String machineId = parameterMap.get("machine_id");
            Map<String, String> machineInfoMap = new HashMap<>();
            if (parameterMap.containsKey("machine_username")) {
                machineInfoMap.put("machine_username", parameterMap.get("machine_username"));
            }
            if (parameterMap.containsKey("machine_nickname")) {
                machineInfoMap.put("nickname", parameterMap.get("machine_nickname"));
            }
            if (parameterMap.containsKey("host_name")) {
                machineInfoMap.put("host_name", parameterMap.get("host_name"));
            }
            machineUpdateResult = connector.updateMachineInfo(machineId, machineInfoMap);
        }
        PrintWriter writer = response.getWriter();
        if (machineUpdateResult) {
            if (userUpdateResult) {
                writer.print("{modify_status:1}");
            } else {
                writer.print("{modify_status:0,error_description:\"updated machine info successfully, but fail to update user info\"}");
            }
        } else {
            if (userUpdateResult) {
                writer.print("{modify_status:0,error_description:\"updated user info successfully, but fail to update machine info\"}");
            } else {
                writer.print("{modify_status:0,error_description:\"fail to update user info and machine info\"}");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(403);
    }
}
