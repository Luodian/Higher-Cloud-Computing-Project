package sample.ServerUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SlaveToServerUtil extends ServerUtil {
    protected int user_id;
    protected MasterTask masterTask = null;

    public SlaveToServerUtil(int machine_id, int ip, int user_id, int operation_selected, MasterTask masterTask) {
        super(machine_id, ip, operation_selected);
        this.masterTask = masterTask;
        this.user_id = user_id;
    }

    public SlaveToServerUtil(int machine_id, int ip, int operation_selected, int user_id) {
        super(machine_id, ip, operation_selected);
        this.user_id = user_id;
    }

    @Override
    protected String taskFinish() throws Exception {
        if (masterTask == null)
            return "You have not yet bound specific tasks";
        HttpClient task_finish = new DefaultHttpClient();
        HttpPost post = new HttpPost(ServerUtil.url + "/TaskFinish");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("machine_ip", String.valueOf(machine_id)));
        params.add(new BasicNameValuePair("user_id", String.valueOf(masterTask.getUser_id())));
        params.add(new BasicNameValuePair("task_id", String.valueOf(masterTask.getTask_id())));
        params.add(new BasicNameValuePair("actor", "slave"));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_finish.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            JSONObject lan = new JSONObject(result);
            int task_fin = lan.getInt("task_fin");
            if (task_fin == 1) return "SUCCESS";
            else return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public MasterTask getMasterTask() {
        return masterTask;
    }

    public void setMasterTask(MasterTask masterTask) {
        this.masterTask = masterTask;
    }
}
