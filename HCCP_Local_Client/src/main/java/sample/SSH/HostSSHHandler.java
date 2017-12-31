package sample.SSH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Elrond Wang
 * 2017/12/5 13:56
 * version 1.0
 * welcome
 * this abstract class was extended by two child class:MasterSSHHandler and SlaveSSHHandler
 */
abstract public class HostSSHHandler implements SSHHandler {
    /**
     * get the number of actual slaves
     */
    public static int ACTUAL_SLAVES_NUMBER = -1;
    /**
     * master host's password
     */
    private String password;
    /**
     * requirement's return parameter
     */
    private List<ClientInfo> temporal;
    /**
     * establish temporal sshkey in order to adapt to the net establish need
     */
    private EstablishSSHKey establishSSHKey;

    public HostSSHHandler(String password) {
        this.password = password;
    }

    /**
     * get the local temporal pubkey
     */
    public static String getLocalPubKey() {
        String localPubKey = new String();
        File file = new File(SSHHandler.FILE_RSA_PUB);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) localPubKey += temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localPubKey;
    }

    /**
     * write the list which is getting from server into local temporal .ssh file
     * and here the parameter is the list
     */

    public String sshTemporalHandle() throws Exception {
        /**change origin .ssh file's name into pushdown ,
         * and establish a temporal .ssh file for the connection
         */
        establishSSHKey = new EstablishSSHKey(password);
        String execResult = establishSSHKey.execute();

        /**if the establishment succeed*/
        if (execResult.equals("SUCCESS")) {

            /** for the situation that  you were master
             *  and you ask for a task towards server
             *  server response to you with a list of salve's pub-keys
             */
            return writePKToTempoAuthFile();
        }
        return execResult;
    }

    public String writePKToTempoAuthFile() throws Exception {
        Object temp = requireToServer();
        if ((temp.getClass() == List.class))
            temporal = (List<ClientInfo>) temp;
        if (temporal == null) return "FAIL";
        ACTUAL_SLAVES_NUMBER = temporal.size();
        try {
            /**try to write the list of salve's temporal pub-keys
             * into local temporal authorized_keys file
             * or write the master's pub-key into
             * slave's temporal pub-keys
             * */
            String all_slaves_info = "";
            for (ClientInfo slave : temporal) {
                all_slaves_info = all_slaves_info + (" " +
                        slave.machine_user_name + " " +
                        slave.ip);
            }
            Process process = Runtime.getRuntime().exec(SSHHandler.SSH_ALL_SLAVES + all_slaves_info);
            process.waitFor();
            if (process.waitFor() != 0) {
                if (process.exitValue() == 1)//p.exitValue()==0��ʾ����������1������������
                {
                    System.err.println("commad execute failing!");
                    return "EXCEPTION";
                }

            }
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAIL";
    }
}
