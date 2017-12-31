package sample.BackEnd.JavaSSH;

/**
 * Created by ������ on
 * 2017/12/4 20:35
 * version 1.0
 * the goal is to set a series of configurations in order to login others without password
 * this interface was implemented by HostSSHHandler
 * welcome
 */
public interface SSHHandler {
    String FILE_RSA_PUB = "/home/elrond/.ssh/id_rsa.pub";
    String FILE_AUTHORIZED_KEYS = "/home/elrond/.ssh/authorized_keys";
    String GEN_PUB = "/home/elrond/.hccp/gen_pub.hccp";
    String UNDO_AUTHORIZED_KEYS = "/home/elrond/.hccp/gen_pub.hccp";
    String MASTER_TO_SLAVE = "/home/elrond/.hccp/mastertoslave.hccp ";
    String SSH_ALL_SLAVES = "/home/elrond/.hccp/sshallslaves.hccp ";

    Object sshTemporalHandle() throws Exception;

    Object requireToServer() throws Exception;
}
