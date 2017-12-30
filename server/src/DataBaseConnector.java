import java.sql.*;
import java.util.*;

import static com.mysql.jdbc.StringUtils.getBytes;

public class DataBaseConnector {
    private static final String url = "jdbc:mysql://localhost:3306/higher_compute?&useSSL=true";
    private static final String user = "admin";
    private static final String password = "administrator";
    private static final int machineIdleStatus = 0;
    private static final int machineSlaveStatus = 1;
    private static final int machineMasterStatus = 2;
    private static final int machineOfflineStatus = 3;

    private boolean connected;
    private Connection connection;

    // 构造函数，建立数据库连接
    DataBaseConnector() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (Exception e) {
            connected = false;
            e.printStackTrace();
        }
    }

    // 执行查询语句
    public ResultSet executeQuery(String inst) {
        if (!this.connected) {
            return null;
        }
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery(inst);
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 执行更新语句
    public int executeUpdate(String inst) {
        if (!this.connected) {
            return -1;
        }
        Statement statement = null;
        try {
            statement = this.connection.createStatement();
            int result = statement.executeUpdate(inst);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 检测是否已连接数据库
    public boolean isConnected() {
        return connected;
    }

    // 检测用户名密码是否匹配
    public boolean checkPassword(String userId, String email, byte[] password) {
        ResultSet resultSet;
        if (userId != null) {
            resultSet = executeQuery("SELECT passwd_hash FROM user_info WHERE user_id = '"
                    + userId + "';");
        } else {
            resultSet = executeQuery("SELECT passwd_hash FROM user_info WHERE email = '"
                    + email + "';");
        }
        try {
            if (resultSet == null) {
                return false;
            }
            if (resultSet.next()) {
                byte[] expect_password = resultSet.getBytes("passwd_hash");
                resultSet.close();
                return Arrays.equals(expect_password, password);
            } else {
                resultSet.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 检测机器是否上线
    public boolean checkMachineOnline(String machineId, String ip) {
        Map<String, String> machineInfo = getMachineInfo(machineId, ip);
        if (machineInfo == null) {
            return false;
        }
        return Integer.parseInt(machineInfo.get("machine_status")) != DataBaseConnector.machineOfflineStatus;
    }

    // 获取用户信息
    public Map<String, String> getUserInfo(String user_id, String email) {
        ResultSet resultSet;
        if (user_id != null) {
            resultSet = executeQuery("SELECT * FROM user_info WHERE user_id = '"
                    + user_id + "';");
        } else if (email != null) {
            resultSet = executeQuery("SELECT * FROM user_info WHERE email = '"
                    + email + "';");
        } else {
            return null;
        }
        try {
            if (resultSet == null) {
                return null;
            }
            if (resultSet.next()) {
                Map<String, String> resultMap = convertUserResultSetToMap(resultSet);
                resultSet.close();
                return resultMap;
            } else {
                resultSet.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取机器信息
    public Map<String, String> getMachineInfo(String machineId, String ip) {
        ResultSet resultSet;
        if (machineId == null && ip == null) {
            return null;
        }
        try {
            if (machineId != null) {
                resultSet = executeQuery("SELECT * FROM machine_info WHERE machine_id = '"
                        + machineId + "';");
            } else {
                resultSet = executeQuery("SELECT * FROM machine_info WHERE ip = '"
                        + ip + "';");
            }
            if (resultSet == null) {
                return null;
            }
            if (resultSet.next()) {
                Map<String, String> resultMap = convertMachineResultSetToMap(resultSet);
                resultSet.close();
                return resultMap;
            } else {
                resultSet.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取任务信息
    public Map<String, String> getTaskInfo(String taskId) {
        ResultSet resultSet;
        try {
            resultSet = executeQuery(String.format("SELECT * FROM task_info WHERE id = \"%s\"", taskId));
            if (resultSet == null) {
                return null;
            }
            if (resultSet.next()) {
                Map<String, String> resultMap = convertTaskResultSetToMap(resultSet);
                resultSet.close();
                return resultMap;
            } else {
                resultSet.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 通过用户的邮箱获取用户ID
    public String getEmailFromUserId(String userId) {
        Map<String, String> searchResult = getUserInfo(userId, null);
        if (searchResult == null) {
            return null;
        } else {
            return searchResult.get("email");
        }
    }

    // 通过用户ID获取用户邮箱
    public String getUserIdFromEmail(String email) {
        Map<String, String> searchResult = getUserInfo(null, email);
        if (searchResult == null) {
            return null;
        } else {
            return searchResult.get("user_id");
        }
    }

    // 通过机器IP获取机器ID
    public String getMachineIdFromIp(String ip) {
        Map<String, String> machineInfo = getMachineInfo(null, ip);
        if (machineInfo == null) {
            return null;
        }
        return machineInfo.get("machine_id");
    }

    // 通过机器ID获取机器IP
    public String getIpFromMachineId(String machineId) {
        Map<String, String> machineInfo = getMachineInfo(machineId, null);
        if (machineInfo == null) {
            return null;
        }
        return machineInfo.get("ip");
    }

    // 获取处于某一状态的全部机器
    public List<Map<String, String>> getMachineWithStatus(int status) {
        if (!connected) {
            return null;
        }
        try {
            ResultSet resultSet = executeQuery("SELECT * FROM machine_info WHERE machine_status = '"
                    + Integer.toString(status) + "';");
            if (resultSet == null) {
                return null;
            }
            return convertMachineResultSetToList(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取属于某用户的全部机器
    public List<Map<String, String>> getUserMachineList(String userId) {
        ResultSet resultSet;
        try {
            resultSet = executeQuery("SELECT * FROM machine_info WHERE user_id ='"
                    + userId + "';");
            if (resultSet == null) {
                return null;
            }
            return convertMachineResultSetToList(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 检测机器是否存在
    public boolean checkMachineExist(String machineId, String ip) {
        Map<String, String> machineInfo = getMachineInfo(machineId, ip);
        return machineInfo != null;
    }

    // 检测邮箱是否存在
    public boolean checkEmailExist(String email) {
        Map<String, String> userInfo = getUserInfo(null, email);
        return userInfo != null;
    }

    // 更新用户信息
    public boolean updateUserInfo(String userId, Map<String, String> infoMap) {
        if (infoMap.size() == 0) {
            return true;
        }
        String commandHead = "UPDATE user_info SET ";
        String commandTail = " WHERE user_id = '" + userId + "';";
        List<String> itemCommandList = new ArrayList<>();
        for (String key : infoMap.keySet()) {
            itemCommandList.add(String.format("%s=\"%s\"", key, infoMap.get(key)));
        }
        String command = commandHead + String.join(",", itemCommandList) + commandTail;
        int result = executeUpdate(command);
        return result == 1;
    }

    // 更新机器信息
    public boolean updateMachineInfo(String machineId, Map<String, String> infoMap) {
        String commandHead = "UPDATE machine_info SET ";
        String commandTail = " WHERE machine_id = '" + machineId + "';";
        List<String> itemCommandList = new ArrayList<>();
        for (String key : infoMap.keySet()) {
            itemCommandList.add(String.format("%s=\"%s\"", key, infoMap.get(key)));
        }
        String command = commandHead + String.join(",", itemCommandList) + commandTail;
        int result = executeUpdate(command);
        return result == 1;
    }

    // 机器下线
    public boolean machineOffline(String machineId) {
        String command = String.format("UPDATE machine_info SET machine_status = 3 WHERE machine_id = \"%s\"", machineId);
        int result = executeUpdate(command);
        return result == 1;
    }

    // 获取参与某一任务的机器的列表
    public List<Map<String, String>> getMachineWithTask(String taskId) {
        String taskAssignInfoCommand = String.format("SELECT * FROM task_assign_info WHERE task_id = \"%s\"", taskId);
        try {
            ResultSet resultSet = executeQuery(taskAssignInfoCommand);
            if (resultSet == null) {
                return null;
            }
            List<Map<String, String>> taskAssignSearchResult = new ArrayList<>();
            while (resultSet.next()) {
                taskAssignSearchResult.add(convertTaskAssignResultSetToMap(resultSet));
            }
            List<Map<String, String>> resultList = new ArrayList<>();
            for (Map<String, String> entity : taskAssignSearchResult) {
                String machineId = entity.get("machine_id");
                Map<String, String> machineInfo = getMachineInfo(machineId, null);
                String entityStatus = entity.get("status");
                machineInfo.put("task_status", entityStatus);
                resultList.add(machineInfo);
            }
            resultSet.close();
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取某一机器参与的所有任务
    public List<Map<String, String>> getTaskWithMachine(String machineId) {
        String searchAssignCommand = String.format("SELECT * FROM task_assign_info WHERE machine_id = \"%s\"",
                machineId);
        ResultSet resultSet = executeQuery(searchAssignCommand);
        try {
            if (resultSet == null) {
                return null;
            }
            List<Map<String, String>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> taskAssignEntity = convertTaskAssignResultSetToMap(resultSet);
                String taskId = taskAssignEntity.get("task_id");
                Map<String, String> taskInfo = getTaskInfo(taskId);
                resultList.add(taskInfo);
            }
            resultSet.close();
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 更新算时
    public boolean updateOnlineTime(String userId, int delta) {
        Map<String, String> userInfo = getUserInfo(userId, null);
        int curOnlineTime = Integer.parseInt(userInfo.get("online_time"));
        int newOnlineTime = curOnlineTime + delta;
        String updateCommand = String.format("UPDATE user_info SET online_time = \"%d\" WHERE user_id = \"%s\"", newOnlineTime, userId);
        int result = executeUpdate(updateCommand);
        return result == 1;
    }

    //------------------------------private methods------------------------------------------

    // 将获取机器信息的查询结果ResultSet转换为Map
    private Map<String, String> convertMachineResultSetToMap(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("machine_id", resultSet.getString("machine_id"));
        resultMap.put("ip", resultSet.getString("ip"));
        resultMap.put("nickname", resultSet.getString("nickname"));
        resultMap.put("machine_status", resultSet.getString("machine_status"));
        resultMap.put("user_id", resultSet.getString("user_id"));
        resultMap.put("compute_ability", resultSet.getString("compute_ability"));
        resultMap.put("remain_compute_ability", resultSet.getString("remain_compute_ability"));
        resultMap.put("machine_username", resultSet.getString("machine_username"));
        resultMap.put("host_name", resultSet.getString("host_name"));
        resultMap.put("public_key", resultSet.getString("public_key"));
        return resultMap;
    }

    // 将获取用户信息的查询结果ResultSet转换为Map
    private Map<String, String> convertUserResultSetToMap(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("user_id", resultSet.getString("user_id"));
        resultMap.put("nickname", resultSet.getString("nickname"));
        resultMap.put("email", resultSet.getString("email"));
        resultMap.put("email_verified", resultSet.getString("email_verified"));
        resultMap.put("credit", resultSet.getString("credit"));
        resultMap.put("online_time", resultSet.getString("online_time"));
        return resultMap;
    }

    // 将获取任务分配的查询结果ResultSet转换为Map
    private Map<String, String> convertTaskAssignResultSetToMap(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("task_id", resultSet.getString("task_id"));
        resultMap.put("id", resultSet.getString("id"));
        resultMap.put("machine_id", resultSet.getString("machine_id"));
        resultMap.put("assign_time", resultSet.getString("assign_time"));
        resultMap.put("status", resultSet.getString("status"));
        return resultMap;
    }

    // 将任务的查询结果ResultSet转换为Map
    private Map<String, String> convertTaskResultSetToMap(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("id", resultSet.getString("id"));
        resultMap.put("name", resultSet.getString("name"));
        resultMap.put("arrive_time", resultSet.getString("arrive_time"));
        resultMap.put("status", resultSet.getString("status"));
        resultMap.put("user_id", resultSet.getString("user_id"));
        return resultMap;
    }

    // 将获取机器信息的查询结果ResultSet转换为List
    private List<Map<String, String>> convertMachineResultSetToList(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        List<Map<String, String>> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(convertMachineResultSetToMap(resultSet));
        }
        resultSet.close();
        return resultList;
    }

    // 将获取用户信息的查询结果ResultSet转换为List
    private List<Map<String, String>> convertUserResultSetToList(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        List<Map<String, String>> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(convertUserResultSetToMap(resultSet));
        }
        resultSet.close();
        return resultList;
    }

    // 将获取任务分配的查询结果ResultSet转换为List
    private List<Map<String, String>> convertTaskAssignResultSetToList(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        List<Map<String, String>> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(convertTaskAssignResultSetToMap(resultSet));
        }
        resultSet.close();
        return resultList;
    }

    // 将任务的查询结果ResultSet转换为List
    private List<Map<String, String>> convertTaskResultSetToList(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        List<Map<String, String>> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(convertTaskResultSetToMap(resultSet));
        }
        resultSet.close();
        return resultList;
    }

    public static void main(String... args) {
        DataBaseConnector c = new DataBaseConnector();
        List<Map<String, String>> result = c.getTaskWithMachine("5");
        System.out.println(result);
    }
}
