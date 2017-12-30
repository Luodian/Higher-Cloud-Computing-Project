package sample.BackEnd.JavaSSH;

import java.util.List;

/**
 * Created by ������ on
 * 2017/12/4 20:35
 * version 1.0
 * the goal is to set a series of configurations in order to login others without password
 * this interface was implemented by HostSSHHandler
 * welcome
 */
public interface SSHHandler {
	String file_rsa_pub = "$HOME/.ssh/ssh_rsa.pub";
	String file_authorized_keys = "$HOME/.ssh/authorized_keys";
	String gen_pub = "$HOME/hccp/gen_pub.hccp ";
	
	void sshTemporalHandle ();
	
	List<String> requireToServer ();
}
