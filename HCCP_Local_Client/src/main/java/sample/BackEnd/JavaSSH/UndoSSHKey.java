package sample.BackEnd.JavaSSH;

public class UndoSSHKey {
    /**
     * user's password
     */
    private String password;

    UndoSSHKey(String password) {
        this.password = password;
    }

    /**
     * this method is executed for undo temporal authorized_keys and known_hosts file
     * and recover origin authorized_keys and known_hosts file if they were existed
     * if return "SUCCESS", mean that all the operation execute correctly
     */

    public String undo() {
        try {
            String cmd[] = {SSHHandler.UNDO_AUTHORIZED_KEYS, password};
            Process process = Runtime.getRuntime().exec(cmd);
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
}
