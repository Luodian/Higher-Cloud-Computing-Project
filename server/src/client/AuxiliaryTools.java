package client;

import java.util.Random;

public class AuxiliaryTools {
  public static final int VERIFICATION_CODE_LENGTH = 6;
  public static Boolean checkValidParameter(String param) {
    if (param==null || param.trim().equals("")) return false;
    return true;
  }
  
  public static String getRandomString(int length) { //length表示生成字符串的长度  
    String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";     
    Random random = new Random();     
    StringBuffer sb = new StringBuffer();     
    for (int i = 0; i < length; i++) {     
        int number = random.nextInt(base.length());     
        sb.append(base.charAt(number));     
    }     
    return sb.toString();     
 }     
 
}
