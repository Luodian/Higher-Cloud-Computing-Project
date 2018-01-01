package sample.BackEnd.ServerUtils;

public class MasterTask {
    private int user_id;
    private int task_id;

    public MasterTask(int user_id, int task_id) {
        this.user_id = user_id;
        this.task_id = task_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getTask_id() {
        return task_id;
    }
}
