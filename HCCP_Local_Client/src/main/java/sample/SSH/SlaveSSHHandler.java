package sample.SSH;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elrond Wang
 * 2017/12/4 22:40
 * version 1.0
 * welcome
 */
public class SlaveSSHHandler extends HostSSHHandler {
    /**
     * slave host's password
     */
    private String password;
    /**
     * slave host's temporal pub-key
     */
    private String localPubKey;
    /**
     * the server's ip
     */
    private String serverip;
    /**
     * the master's ip
     */
    private String masterip;

    public SlaveSSHHandler(String password, String serverip) throws Exception {
        super(password);
        this.password = password;
        this.serverip = serverip;
        sshTemporalHandle();
    }

    /**
     * overwrite the requirement to server
     */
    @Override
    public List<ClientInfo> requireToServer() {
        localPubKey = getLocalPubKey();
        /**
         * this block needs to be updated when the server interface is completed
         * and this method need to get the master's pub-key and master's ip
         * so return a list only having one string
         * but now i fixed the result here
         * */
        List<ClientInfo> result = new ArrayList<ClientInfo>();
        return result;
    }
}
