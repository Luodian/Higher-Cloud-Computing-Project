package sample.BackEnd.ServerUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.BackEnd.JavaSSH.ClientInfo;
import sample.BackEnd.JavaSSH.HostSSHHandler;
import sample.BackEnd.JavaSSH.SSHHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ServerUtil implements SSHHandler {
    public static String url = "http://cn.yuhong-zhong.com:8080/higher_compute";
    public static int PK_SEND = 12;
    public static int IP_SEND = 14;
    public static int TASK_FINISH = 16;
    public static int GET_ONLINE_MACHINE_INFO = 13;
    protected int machine_id;
    protected String ip;
    protected int selectedd_OP = -1;

    public ServerUtil(int machine_id, int ip, int operation_selected) {
        this.machine_id = machine_id;
        this.selectedd_OP = operation_selected;
    }

    @Override
    public String sshTemporalHandle() throws Exception {
        return "NO OPERATION";
    }

    @Override
    public Object requireToServer() throws Exception {
        switch (selectedd_OP) {
            case 12: {
                return pkSend();
            }
            case 13: {
                return getOnlineMachineInfo();
            }
            case 14: {
                return ipSend();
            }
            case 16: {
                return taskFinish();
            }
            default: {
                return "WRONG OPERATION";
            }
        }
    }

    /**
     * send public key to server
     */
    private String pkSend() throws Exception {
        File file = new File(SSHHandler.FILE_RSA_PUB);
        if (file.exists()) {
            String rsa_pub = HostSSHHandler.getLocalPubKey();
            HttpClient task_post = new DefaultHttpClient();
            HttpPost post = new HttpPost(url + "/UploadPK");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("machine_id", String.valueOf(machine_id)));
            params.add(new BasicNameValuePair("public_key", rsa_pub));
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = task_post.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = null;
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    result = EntityUtils.toString(httpEntity);
                }
                JSONObject jsonObject = new JSONObject(result);
                int PK_save = jsonObject.getInt("PK_save");
                if (PK_save == 1) {
                    return "SUCCESS";
                }
                return "FAIL";
            }
            return "401 UNAUTHORIZED";
        } else return "THE ID_RSA.PUB FILE DOESN'T EXIST";
    }

    /**
     * send current ip to server
     */
    private String ipSend() throws Exception {
        HttpClient ip_send = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/UploadIP");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("machine_id", String.valueOf(machine_id)));
        params.add(new BasicNameValuePair("ip", ip));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse httpResponse = ip_send.execute(post);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int ip_save = jsonObject.getInt("ip_save");
            if (ip_save == 1) {
                return "SUCCESS";
            } else return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    /**
     * tell the server that the task was finished
     */
    protected abstract String taskFinish() throws Exception;

    /**
     * change the selected operation
     */
    public void changeOperation(int op) {
        switch (op) {
            case 12: {
                selectedd_OP = op;
                break;
            }
            case 13: {
                selectedd_OP = op;
                break;
            }
            case 14: {
                selectedd_OP = op;
                break;
            }
            case 16: {
                selectedd_OP = op;
                break;
            }
            default: {
                System.err.println("WRONG OP");
                break;
            }
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Object getOnlineMachineInfo() throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url + "/get_online_machine_info");
        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            JSONArray arr = new JSONArray(result);
            ArrayList<ClientInfo> machine_infos = new ArrayList<ClientInfo>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject lan = arr.getJSONObject(i);
                ClientInfo machine = new ClientInfo();
                machine.host_name = lan.getString("host_name");
                machine.ip = lan.getString("ip");
                machine.compute_ability = lan.getInt("compute_ability");
                machine.machine_id = lan.getInt("machine_id");
                machine.nickname = lan.getString("nickname");
                machine.machine_status = lan.getInt("machine_status");
                machine.machine_user_name = lan.getString("machine_username");
                machine.remain_compute_ablity = lan.getInt("remain_compute_ablity");
                machine_infos.add(machine);
            }
            return machine_infos;
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            return "401 SC_UNAUTHORIZED";
        }
        return "UNKNOWN ERROR";
    }
}
