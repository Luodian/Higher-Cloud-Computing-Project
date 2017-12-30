import java.io.*;
import java.util.List;

/**
 * Created by Elrond Wang
 * 2017/12/5 13:56
 * version 1.0
 * welcome
 * this abstract class was extended by two child class:MasterSSHHandler and SlaveSSHHandler
 */
abstract public class HostSSHHandler implements SSHHandler {
    /**master host's password*/
    private String password;
    /**requirement's return parameter*/
    private List<String> temporalPubKeyList;
    /** establish temporal sshkey in order to adapt to the net establish need*/
    private EstablishSSHKey establishSSHKey;

    public HostSSHHandler(String password) {
        this.password = password;
    }

    /**
     * write the list which is getting from server into local temporal .ssh file
     * and here the parameter is the list
     * */

    public void sshTemporalHandle() {
        /**change origin .ssh file's name into pushdown ,
         * and establish a temporal .ssh file for the connection
         */
        establishSSHKey = new EstablishSSHKey(password);
        String execResult = establishSSHKey.execute();

        /**if the establishment succeed*/
        if (execResult.equals("SUCCESS")){

            /** for the situation that  you were master
             *  and you ask for a task towards server
             *  server response to you with a list of salve's pub-keys
             */
            writePKToTempoAuthFile();
        }
    }
    public void writePKToTempoAuthFile(){
        temporalPubKeyList = requireToServer();
        File authorizedFile = new File(SSHHandler.file_authorized_keys);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(authorizedFile,true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            /**try to write the list of salve's temporal pub-keys
             * into local temporal authorized_keys file
             * or write the master's pub-key into
             * slave's temporal pub-keys
             * */
            for (String otherPubKey:temporalPubKeyList) bufferedWriter.write(otherPubKey);

            /**closs the IO*/
            bufferedWriter.close();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getTemporalPubKeyList() {
        return temporalPubKeyList;
    }

    /**get the local temporal pubkey*/
    public String getLocalPubKey(){
        String localPubKey = new String();
        File file = new File(SSHHandler.file_rsa_pub);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp = null;
            while ((temp = bufferedReader.readLine())!=null) localPubKey += temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localPubKey;
    }
}
