import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elrond Wang
 * 2017/12/4 21:07
 * version 1.0
 * welcome
 */

public class MasterSSHHandler extends HostSSHHandler{
    /** master host's password*/
    private String password;
    /** master host's temporal pub-key*/
    private String localPubKey;
    /** the server's ip*/
    private String serverip;
    /** the list of slave's ip*/
    private List<String> slaveips;

    public MasterSSHHandler(String password, String serverip, List<String> slaveips) {
        super(password);
        this.password = password;
        this.serverip = serverip;
        this.slaveips = slaveips;
        sshTemporalHandle();
    }

    /***/
    public String sshSlave(String slaveips) throws InterruptedException {
        try {
            Process process = Runtime.getRuntime().exec("ssh -o StrictHostKeyChecking=no "+ slaveips);
            process.waitFor();

            return "SUCCESS";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "FAILURE";
    }

    /**overwrite the requirement to server*/
    @Override
    public List<String> requireToServer() {
        localPubKey = getLocalPubKey();
        /**
         * this block needs to be updated when the server interface is completed
         * and now the result list is fixed
         */
        List<String> result = new ArrayList<String>();
        result.add("");
        result.add("");
        return result;
    }
}
