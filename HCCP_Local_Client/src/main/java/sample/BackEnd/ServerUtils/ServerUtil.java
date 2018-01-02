package sample.BackEnd.ServerUtils;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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

    //    检查邮箱是否存在,即是否已经被注册
    public static String check_email_exist(String email_addr) throws Exception {
        HttpClient client = new DefaultHttpClient();
        String params = "?email_addr=" + email_addr;
        HttpGet get = new HttpGet(url + "/check_email_exist" + params);
        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int email_exists = jsonObject.getInt("email_exists");
            if (email_exists == 1) {
                return "SUCCESS";
            } else
                return "FAIL";
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            return "401 SC_UNAUTHORIZED";
        }
        return "UNKNOWN ERROR";
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

    //   注册
    public static String register(String email_addr, String nick_name, String pwd) throws Exception {

        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/register");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email_addr", email_addr));
        params.add(new BasicNameValuePair("nick_name", nick_name));
        params.add(new BasicNameValuePair("pwd", pwd));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int register_status = jsonObject.getInt("register_status");
            String user_id = jsonObject.getString("user_id");
            if (register_status == 1)
                return "SUCCESS";
            else
                return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    //    9,获取相关节点信息
    public static Object get_machine_info_with_task(String task_id) throws Exception {
        HttpClient client = new DefaultHttpClient();
        String params = "?task_id=" + task_id;
        HttpGet get = new HttpGet(url + "/get_machine_info_with_task" + params);
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

//                ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//                返回的列表中可能是machine_status，也可能是公钥，待裁决
                machine.machine_status = lan.getInt("machine_status");

                String public_key = lan.getString("public_key");
//！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

                machine.machine_user_name = lan.getString("machine_username");
                machine.remain_compute_ability = lan.getInt("remain_compute_ability");
                machine.user_id = lan.getInt("user_id");
                machine.task_status = lan.getInt("task_status");
                machine_infos.add(machine);
            }
            return machine_infos;
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            return "401 SC_UNAUTHORIZED";
        }
        return "UNKNOWN ERROR";
    }

    //    10,邮箱激活及验证，具体地址还不明确
    public static String email_verification(int user_id, String verification_code) throws Exception {
        HttpClient task_post = new DefaultHttpClient();
        //？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        HttpPost post = new HttpPost(url + "/##");
        //    ？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
        params.add(new BasicNameValuePair("verification_code", verification_code));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int email_verification = jsonObject.getInt("email_verification");
            if (email_verification == 1)
                return "SUCCESS";
            else
                return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    //     11,上传头像,为了测试，暂时改为静态
    public static String ImgUpload(int user_id, String path) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url + "/ImgUpload");
        MultipartEntity muit = new MultipartEntity();

        File img = new File(path, "sucie.png");
        FileBody bin = new FileBody(img, "image/*");
        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("img", bin)
                .addPart("user_id", new StringBody(String.valueOf(user_id), ContentType.create("text/plain", Consts.UTF_8)))
                .build();
        httpPost.setEntity(reqEntity);

//
//        HttpClient task_post = new DefaultHttpClient();
//        HttpPost post = new HttpPost(url + "/ImgUpload");
//        FileBody binFileBody = new FileBody(img);
//        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//        entityBuilder.addPart("img",binFileBody);
//        entityBuilder.addPart("user_id",new StringBody(String.valueOf(user_id), ContentType.TEXT_PLAIN));
//
//        HttpEntity reqEntity = entityBuilder.build();
//        post.setEntity(reqEntity);

//        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int img_save = jsonObject.getInt("img_save");
            if (img_save == 1)
                return "SUCCESS";
            else
                return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    //重置密码,设置为静态以供测试
    public static String resetPwd(int user_id, String pwd) throws Exception {

        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/ResetPwd");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
        params.add(new BasicNameValuePair("pwd", pwd));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int pwd_reset = jsonObject.getInt("pwd_reset");
            if (pwd_reset == 1)
                return "SUCCESS";
            else
                return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    @Override
    public String sshTemporalHandle() {
        return "NO OPERATION";
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
                machine.remain_compute_ability = lan.getInt("remain_compute_ability");
                machine_infos.add(machine);
            }
            return machine_infos;
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            return "401 SC_UNAUTHORIZED";
        }
        return "UNKNOWN ERROR";
    }

    //  登录
    public String login(String user_id, String user_email, String passwd) throws Exception {

        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/login");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//            如果没有输入user_id就发送user_email,否则发送user_id
        if (user_id == null || user_id.equals("")) {
            params.add(new BasicNameValuePair("user_email", user_email));
        } else {
            params.add(new BasicNameValuePair("user_id", user_email));
        }
        params.add(new BasicNameValuePair("passwd", passwd));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            String port1 = jsonObject.getString("port1");
            String port2 = jsonObject.getString("port2");
            return "SUCCESS";

        }
        return "401 UNAUTHORIZED";

    }

    //   重置密码验证
    public String pwdResetVal(String email_addr, String user_id, String pwd) throws Exception {
        HttpClient client = new DefaultHttpClient();
        String params = "";
//            如果没有输入user_id就发送user_email,否则发送user_id
        if (user_id == null || user_id.equals("")) {
            params.concat("?email_addr=" + email_addr);
//            params.add(new BasicNameValuePair("email_addr", email_addr));
        } else {
            params.concat("?user_id=" + user_id);
//            params.add(new BasicNameValuePair("user_id", user_id));
        }
        params.concat("&pwd=" + pwd);
        HttpGet get = new HttpGet(url + "/PwdResetVal" + params);
        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int email_sent = jsonObject.getInt("email_sent");
            if (email_sent == 1) {
                return "SUCCESS";
            } else
                return "FAIL";
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            return "401 SC_UNAUTHORIZED";
        }
        return "UNKNOWN ERROR";
    }

    //   修改个人信息
    public String modify_user_info(String user_id, String user_nickname, String host_name,
                                   String machine_username, String machine_nickname, String machine_id) throws Exception {
        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/modify_user_info");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", user_id));
        if (user_nickname != null && !(user_nickname.equals(""))) {
            params.add(new BasicNameValuePair("user_nickname", user_nickname));
        }
        if (host_name != null && !(host_name.equals(""))) {
            params.add(new BasicNameValuePair("host_name", host_name));
        }
        if (machine_nickname != null && !(machine_nickname.equals(""))) {
            params.add(new BasicNameValuePair("machine_nickname", machine_nickname));
        }
        if (machine_id != null && !(machine_id.equals(""))) {
            params.add(new BasicNameValuePair("machine_id", machine_id));
        }
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int modify_status = jsonObject.getInt("modify_status");
            if (modify_status == 1)
                return "SUCCESS";
            else {
                String error_description = jsonObject.getString("error_description");
                return error_description;
            }
        }
        return "401 UNAUTHORIZED";
    }

    //    机器下线
    public String machine_offline(String machine_id) throws Exception {
        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/machine_offline");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("machine_id", machine_id));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = null;
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            JSONObject jsonObject = new JSONObject(result);
            int status = jsonObject.getInt("status");
            if (status == 1)
                return "SUCCESS";
            else
                return "FAIL";
        }
        return "401 UNAUTHORIZED";
    }

    //   18.更新算时,我这里算时增量写为int类型，有待验证，也可能是DATETIME 类型
    public String update_online_time(int machine_id, int user_id, int delta) throws Exception {

        HttpClient task_post = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "/update_online_time");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("machine_id", String.valueOf(machine_id)));
        params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
        params.add(new BasicNameValuePair("delta", String.valueOf(delta)));
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse response = task_post.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//        权限通过
            return "AUTHORIZED";
        }
        return "401 UNAUTHORIZED";
    }
}
