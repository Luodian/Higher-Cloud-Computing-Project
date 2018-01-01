package sample.BackEnd.JavaSSH;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elrond Wang
 * 2017/12/4 16:04
 * version 1
 * welcome
 */
public class EstablishSSHKey {
    /**
     * the stdoutInfo for avoiding dead-lock
     */
    private List stdoutList = new ArrayList();
    /**
     * the stdoutInfo for avoiding dead-lock
     */
    private List erroroutList = new ArrayList();
    /**
     * user's password
     */
    private String password;

    EstablishSSHKey(String password) {
        this.password = password;
    }


    /**
     * this method is executed for generating temporal .ssh file
     * and protecting original .ssh file
     * if return "SUCCESS", mean that all the operation execute correctly
     */

    public String execute() {
        try {
            String cmd[] = {SSHHandler.GEN_PUB, password};
            Process process = Runtime.getRuntime().exec(cmd);
            ThreadUtil stdoutUtil = new ThreadUtil(process.getInputStream(), stdoutList);
            ThreadUtil erroroutUtil = new ThreadUtil(process.getInputStream(), erroroutList);
            stdoutUtil.start();
            erroroutUtil.start();
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
        return "FAILURE";
    }

    public List getStdoutList() {
        return stdoutList;
    }

    public List getErroroutList() {
        return erroroutList;
    }
}
