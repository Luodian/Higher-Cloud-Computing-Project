package client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Date;
import java.text.SimpleDateFormat;

import javax.sql.rowset.serial.SerialBlob;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DBcrud {
  private String jdbcDriver = "com.mysql.jdbc.Driver";
  private String dbusername = "admin";
  private String dbpassword = "administrator";
  private String dbUrl = "jdbc:mysql://localhost:3306/higher_compute?useSSL=false&useUnicode=true&characterEncoding=utf-8";

  public Connection connectDB() {
    try {
      Class.forName(jdbcDriver);
      Connection connect = DriverManager.getConnection(dbUrl, dbusername, dbpassword);
      return connect;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  private String BlobToString(Blob blob) throws SQLException, IOException {

    String reString = "";
    InputStream is =  blob.getBinaryStream();

    ByteArrayInputStream bais = (ByteArrayInputStream)is;
    byte[] byte_data = new byte[bais.available()]; //bais.available()���ش����������ֽ���
    bais.read(byte_data, 0,byte_data.length);//���������е����ݶ���ָ��������
    reString = new String(byte_data,"utf-8"); //��תΪString����ʹ��ָ���ı��뷽ʽ
    is.close();

    return reString;
  }
  public Boolean changeActiveState(String verification_code, String user_id) {
    Connection connect = connectDB();
    ResultSet rs;
    if (connect == null) {
      return false;
    }
    String sqlStatement1 = "select user_id from user_info where verification_code = ?";
    String sqlStatement = "update user_info set email_verified=1, verification_code = null where verification_code = (?) and user_id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement1);
      ps.setString(1, verification_code);
      rs = ps.executeQuery();
      if (rs.next()) {
        ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
        ps.setString(1, verification_code);
        ps.setString(2, user_id);
        ps.executeUpdate();
        connect.close();
        return true;
      }
      return false;
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  private JSONArray extractInfo(ResultSet rs) {
    JSONArray jsa = new JSONArray();
    ResultSetMetaData md;
    try {
      md = rs.getMetaData();
      int column = md.getColumnCount();
      while (rs.next()) {
        JSONObject jsb = new JSONObject();
        for (int i = 1; i <= column; i++) {
         jsb.put(md.getColumnName(i), rs.getObject(i));
        }
        jsa.add(jsb);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return jsa;
    }
   
  public JSONObject getUserInfor(String userId) {
    Connection connect = connectDB();
    if (connect == null) {
      return JSONObject.fromObject("{}");
    }

    String sqlStatement = "select nickname, credit, online_time, user_id from user_info where user_id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      JSONObject jsb = extractInfo(rs).getJSONObject(0);
      return jsb;
    }
    catch(Exception e) {
      e.printStackTrace();
      return JSONObject.fromObject("{}");
    }
  }
  
  public JSONArray userMachine(String userId){
    Connection connect = connectDB();
    JSONArray jsa = new JSONArray();
    if (connect == null) {
      return JSONArray.fromObject("[]");
    }
    String sqlStatement = "select host_name, ip, nickname, machine_status, machine_username, compute_ability, remain_compute_ability from machine_info where user_id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, userId);
      ResultSet rs = ps.executeQuery();
      jsa = extractInfo(rs);
      return jsa;
    }
    catch(Exception e) {
      e.printStackTrace();
      return jsa;
    }
  }
  
  public int insertUserInfo(String nickName, String email, String pwdHash){
    Connection connect = connectDB();
    String sqlStatement = "insert into user_info (nickname, passwd_hash, email, email_verified, credit, online_time) value(?,?,?,?,?,?)";
    String sqlGetId = "select LAST_INSERT_ID()";
    PreparedStatement ps;
    ResultSet rs;
    int userId = -1;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, nickName);
      ps.setBlob(2,  new SerialBlob(pwdHash.getBytes("utf-8")));
      ps.setString(3, email);
      ps.setBoolean(4, false);
      ps.setInt(5, 0);
      ps.setInt(6, 0);
      ps.executeUpdate();
      
      ps = (PreparedStatement) connect.prepareStatement(sqlGetId);
      rs = ps.executeQuery();
      while(rs.next()) {
        userId = rs.getInt(1);
      }
      connect.close();
      return userId;  
    }
    catch(Exception e) {
      e.printStackTrace();
      return userId;
    }
  }
  
  public Boolean updateVC(String email, String verification_code){
    Connection connect = connectDB();
    String sqlStatement = "update user_info set verification_code = (?) where email = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, verification_code);
      ps.setString(2, email);
      ps.executeUpdate();
      connect.close();
      return true;  
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public Boolean updatePublicKeyOrIP(String pk, int machineId , String field){
    Connection connect = connectDB();
    String sqlStatement = "update machine_info set "+ field +" = (?) where machine_id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, pk);
      ps.setInt(2, machineId);
      ps.executeUpdate();
      connect.close();
      return true;  
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public int AddNewTask(String name, int userId) {
    Connection connect = connectDB();
    String sqlStatement = "insert into task_info (name, arrive_time, status, user_id) value(?,?,?,?)";
    String sqlGetId = "select LAST_INSERT_ID()";
    PreparedStatement ps;
    ResultSet rs;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
    int taskId = -1;
   
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, name);
      ps.setString(2,df.format(new Date()));
      ps.setInt(3, 3);
      ps.setInt(4, userId);
      ps.executeUpdate();
      
      
      ps = (PreparedStatement) connect.prepareStatement(sqlGetId);
      rs = ps.executeQuery();
      while(rs.next()) {
        taskId = rs.getInt(1);
      }
      connect.close();
      return taskId;
      
    }
    catch(Exception e) {
      e.printStackTrace();
      return taskId;
    }
  }

  public Boolean updateTaskStatus(int taskId, int status){
    Connection connect = connectDB();
    String sqlStatement = "update task_info set status = (?) where id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setInt(1, status);
      ps.setInt(2, taskId);
      ps.executeUpdate();
      connect.close();
      return true;  
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public Boolean updateTaskAssignStatus(int taskId, int status, int machine_id){
    Connection connect = connectDB();
    String sqlStatement = "update task_assign_info set status = (?) where task_id = (?) and machine_id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setInt(1, status);
      ps.setInt(2, taskId);
      ps.setInt(3, machine_id);
      ps.executeUpdate();
      connect.close();
      return true;  
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public Boolean updatePwd(int user_id, String pwd) {
    Connection connect = connectDB();
    String sqlStatement = "update user_info set passwd_hash = (?) where user_id = (?)";
    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement);
      ps.setString(1, pwd);
      ps.setInt(2, user_id);
      ps.executeUpdate();
      connect.close();
      return true;  
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public String findInUserInfo(String known, String knownField, String unknownField) {
    String sqlStatement1 = "select "+unknownField+" from user_info where "+knownField+"= ?";
    Connection connect = connectDB();
    PreparedStatement ps;
    ResultSet rs;
    String unknown = null;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement1);
      ps.setString(1, known);
      rs = ps.executeQuery();
      if (rs.next()) {
        unknown = rs.getString(1);
      }
     }
    catch(Exception e) {
      e.printStackTrace();
    }
    return unknown;
  }
  
  // for test
  public void showPwd(int userId) {
    String sqlStatement1 = "select passwd_hash from user_info where user_id= ?";
    Connection connect = connectDB();
    PreparedStatement ps;
    ResultSet rs;
    try {
      ps = (PreparedStatement) connect.prepareStatement(sqlStatement1);
      ps.setInt(1, userId);
      rs = ps.executeQuery();
      if (rs.next()) {
        System.out.print("passwd hash:");
        System.out.println(BlobToString(rs.getBlob(1)));
      }
     }
    catch(Exception e) {
      e.printStackTrace();
    }
    return ;
  }
}
