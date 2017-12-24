//package sample;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.Vector;
//
//import com.jcraft.jsch.*;
//
///**
// * This class provide interface to execute command on remote Linux.
// */
//
//public class SSHCommandExecutor {
//    private String ipAddress;
//
//    private String username;
//
//    private String password;
//
//    public static final int DEFAULT_SSH_PORT = 22;
//
//    private Vector<String> stdout;
//
//    public SSHCommandExecutor(final String ipAddress, final String username, final String password) {
//        this.ipAddress = ipAddress;
//        this.username = username;
//        this.password = password;
//        stdout = new Vector<String>();
//    }
//
//    public int execute(final String command) {
//        int returnCode = 0;
//        JSch jsch = new JSch();
//        MyUserInfo userInfo = new MyUserInfo();
//
//        try {
//            // Create and connect session.
//            Session session = jsch.getSession(username, ipAddress, DEFAULT_SSH_PORT);
//            session.setPassword(password);
//            session.setUserInfo(userInfo);
//
//            session.connect();
//
//            // Create and connect channel.
//            Channel channel = session.openChannel("exec");
//            ((ChannelExec) channel).setCommand(command);
//
//            channel.setInputStream(null);
//            BufferedReader input = new BufferedReader(new InputStreamReader(channel
//                    .getInputStream()));
//
//            channel.connect();
//            System.out.println("The remote command is: " + command);
//
//            // Get the output of remote command.
//            String line;
//            while ((line = input.readLine()) != null) {
//                stdout.add(line);
//            }
//            input.close();
//
//            // Get the return code only after the channel is closed.
//            if (channel.isClosed()) {
//                returnCode = channel.getExitStatus();
//            }
//            System.out.println(session.getHostKey());
//            // Disconnect the channel and session.
//            channel.disconnect();
//            session.disconnect();
//        } catch (JSchException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return returnCode;
//    }
//
//    public Vector<String> getStandardOutput() {
//        return stdout;
//    }
//
//    public static void main(final String [] args) {
//        SSHCommandExecutor sshExecutor = new SSHCommandExecutor("172.20.91.207", "elrond", "12345678");
//        sshExecutor.execute("cd Desktop");
//
//        Vector<String> stdout = sshExecutor.getStandardOutput();
//        for (String str : stdout) {
//            System.out.println(str);
//        }
//    }
//}
//class MyUserInfo implements UserInfo{
//
//    @Override
//    public String getPassphrase() {
//        return null;
//    }
//
//    @Override
//    public String getPassword() {
//        return null;
//    }
//
//    @Override
//    public boolean promptPassword(String s) {
//        return true;
//    }
//
//    @Override
//    public boolean promptPassphrase(String s) {
//        return true;
//    }
//
//    @Override
//    public boolean promptYesNo(String s) {
//        return true;
//    }
//
//    @Override
//    public void showMessage(String s) {
//
//    }
//}