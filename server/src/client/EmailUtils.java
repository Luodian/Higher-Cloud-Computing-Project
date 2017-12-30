package client;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

public class EmailUtils {

  public static final String HOST = "smtp.qq.com";
  public static final String PROTOCOL = "smtp";
  public static final String PORT = "465";
  private static final String FROM = "2218234907@qq.com";
  public static final String PWD = "bkuxxumcjjnudjda";
  

  private static Session getSession() {
    MailSSLSocketFactory sf;
    Properties props = System.getProperties();
    try {
      sf = new MailSSLSocketFactory();

      sf.setTrustAllHosts(true);
      props.put("mail.smtp.ssl.socketFactory", sf);
    } catch (GeneralSecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    props.setProperty("mail.debug", "true");
    props.put("mail.smtp.ssl.enable", "true");
    props.put("mail.smtp.localhost", "127.0.0.1");
    props.setProperty("mail.smtp.host", HOST);
    props.setProperty("mail.smtp.port", PORT);
    props.setProperty("mail.smtp.socketFactory.port", PORT);
    props.setProperty("mail.smtp.auth", "true");

    Authenticator authenticator = new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(FROM, PWD);
      }

    };
    Session session = Session.getDefaultInstance(props, authenticator);

    return session;
  }

  public static void sendAccountActivateEmail(String email, String uuid, int userId) {
    Session session;
    try {
      session = getSession();
      MimeMessage message = new MimeMessage(session);
      message.setSubject("天算验证码");
      message.setSentDate(new Date());
      message.setFrom(new InternetAddress(FROM));
      message.setRecipient(RecipientType.TO, new InternetAddress(email));
      message.setContent("天算用户"+userId+":您的验证码为："+uuid,
          "text/html;charset=utf-8");
      message.saveChanges();
      Transport.send(message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
