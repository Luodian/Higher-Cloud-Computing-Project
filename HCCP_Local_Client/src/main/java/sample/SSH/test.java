package sample.SSH;

import sample.ServerUtils.ServerUtil;

/**
 * Created by ������ on 2017/12/25.
 */
public class test {
    public static void main(String[] args) throws Exception {

            ServerUtil serverUtil = new ServerUtil(1, 1, ServerUtil.GET_ONLINE_MACHINE_INFO) {
                @Override
                protected String taskFinish() throws Exception {
                    return null;
                }
            };
        serverUtil.requireToServer();

//            ArrayList<Integer> arrayList = new ArrayList<Integer>();
//            arrayList.add(2);
//            arrayList.add(3);
//            arrayList.add(4);
//            MasterSSHHandler masterSSHHandler = new MasterSSHHandler("12345678", 1, 1);
//            masterSSHHandler.execute("first", arrayList);
//            UndoSSHKey undoSSHKey = new UndoSSHKey("12345678");
//            undoSSHKey.undo();
//            EstablishSSHKey establishSSHKey = new EstablishSSHKey("12345678");
//            establishSSHKey.execute();n
    }
}
