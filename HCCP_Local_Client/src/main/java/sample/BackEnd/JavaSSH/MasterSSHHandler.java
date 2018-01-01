package sample.BackEnd.JavaSSH;

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
import sample.BackEnd.ServerUtils.ServerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elrond Wang
 * 2017/12/4 21:07
 * version 1.0
 * welcome
 */

public class MasterSSHHandler extends HostSSHHandler {
    /**
     * master host's password
     */
    private String password;
    /**
     * the machine's id in server database
     */
    private int machine_id;
    /**
     * the current user of the machine
     */
    private int user_id;
    /**
     * ssh public key info
     */
    private String public_key;
    /**
     * the list of the selected machines` ids
     */
    private ArrayList<Integer> machine_id_list;
    /**
     * the name of the task
     */
    private String task_name;
    /***/
    private int task_id = -1;

    public MasterSSHHandler(String password, int machine_id, int user_id) throws Exception {
        super(password);
        this.password = password;
        this.machine_id = machine_id;
        this.user_id = user_id;
    }

    public String execute(String task_name, ArrayList<Integer> machine_id_list) throws Exception {
        if (machine_id_list == null) return "ERROR MACHINE_ID_LIST:NULL";
        this.machine_id_list = machine_id_list;
        this.task_name = task_name;
        String result = sshTemporalHandle();
        return result;
    }

    /**
     * overwrite the requirement to server
     */
    @Override
    public List<ClientInfo> requireToServer() throws Exception {
        this.public_key = getLocalPubKey();
        /**
         * this block needs to be updated when the server interface is completed
         * and now the result list is fixed
         */

        /**task_post_action*/
        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(ServerUtil.url + "/SubmitTask");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("machine_id", String.valueOf(this.machine_id)));
        params.add(new BasicNameValuePair("user_id", String.valueOf(this.user_id)));
        params.add(new BasicNameValuePair("task_name", this.task_name));
        params.add(new BasicNameValuePair("public_key", this.public_key));
        String json_machine_id_list = "[";
        for (int i = 0; i < machine_id_list.size(); i++) {
            json_machine_id_list += String.valueOf(machine_id_list.get(i));
            if (i != machine_id_list.size() - 1) json_machine_id_list += ",";
            else json_machine_id_list += "]";
        }
        params.add(new BasicNameValuePair("machine_id_list", json_machine_id_list));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response_task_post = task_post.execute(post);
        String return_param = null;
        if (response_task_post.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity httpEntity = response_task_post.getEntity();
            if (httpEntity != null) {
                return_param = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(return_param);
            int task_pub = jsonObject.getInt("task_pub");
            if (task_pub == 0) {
                System.out.println("sorry, task publish action meet unexpected error!");
            } else if (task_pub == 1) {
                this.task_id = jsonObject.getInt("task_id");


                /**wait to test*/
                AskServerIsReady askServerIsReady = new AskServerIsReady(ServerUtil.url, 1000);
                askServerIsReady.start();
                askServerIsReady.t.join();
                String result = askServerIsReady.getResult();
                if (result.equals("SUCCESS")) {
                    return askServerIsReady.getSlaves();
                }
            }
        }
        return null;
    }

    class AskServerIsReady implements Runnable {

        public Thread t;
        private String url;
        private ArrayList<ClientInfo> slaves = null;
        private String result;
        private int times;

        public AskServerIsReady(String url, int times) {
            this.url = url;
            this.times = times;
        }

        @Override
        public void run() {
            /**the master need to ask server whether all slaves were informed*/
            try {
                /**the master need to ask server whether all slaves were informed*/
                int current_times = 0;
                while (current_times < times) {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url + "/get_slave_status");
                    HttpResponse response = client.execute(get);
                    String result = null;
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity httpEntity = response.getEntity();
                        if (httpEntity != null) {
                            result = EntityUtils.toString(httpEntity);
                        }
                        JSONArray arr = new JSONArray(result);
                        slaves = new ArrayList<ClientInfo>();
                        int flag = 0;
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject lan = arr.getJSONObject(i);
                            if ((i == 0) && (lan.getInt("slave_ready") == 0)) {
                                flag = 1;
                                break;
                            } else if (i != 1) {
                                ClientInfo slave = new ClientInfo(lan.getString("host_name"),
                                        lan.getString("ip"), lan.getString("nickname"),
                                        lan.getString("machine_user_name"));
                                slaves.add(slave);
                            }
                        }
                        if (flag == 0) {
                            result = "SUCCESS";
                            break;
                        }
                        if (flag == 1) {
                            current_times++;
                            continue;
                        }
                    } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                        result = "401 UNAUTHORIZED";
                        break;
                    }
                }
                result = "REQUIREMENT OVER TIME";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ArrayList<ClientInfo> getSlaves() {
            return slaves;
        }

        public void start() {
            t = new Thread(this);
            t.start();
        }

        public String getResult() {
            return result;
        }
    }
}
