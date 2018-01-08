package sample;

/**
 * Created by Elrond Wang
 * 2017/12/25.
 */
public class ClientInfo {
    public int machine_id;
    public String host_name;
    public String ip;
    public String nickname;
    public String machine_user_name;
    public int compute_ability;
    public double remain_compute_ability;
    public int machine_status;

    //    武德浩于2018年1月1日加上以下2个属性
    public int user_id;
    public int task_status;

    public ClientInfo() {
    }

    ClientInfo(String host_name, String ip, String nickname, String machine_user_name) {
        this.host_name = host_name;
        this.ip = ip;
        this.nickname = nickname;
        this.machine_user_name = machine_user_name;
    }

    public ClientInfo(int machine_id, String host_name, String ip, String nickname, String machine_user_name, int compute_ability, double remain_compute_ablity, int machine_status) {
        this.machine_id = machine_id;
        this.host_name = host_name;
        this.ip = ip;
        this.nickname = nickname;
        this.machine_user_name = machine_user_name;
        this.compute_ability = compute_ability;
        this.remain_compute_ability = remain_compute_ability;
        this.machine_status = machine_status;
    }
}
