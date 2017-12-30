import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by ÕıÏ«’È.
 * 2017/12/4 16:25
 * verson 1.0
 * welcome
 */
public class ThreadUtil implements Runnable{

    /**set the character standard*/
    private String character = "GB2312";
    private List list;
    private InputStream inputStream;
    private Thread thread;
    public  ThreadUtil(InputStream inputStream, List list){
        this.inputStream = inputStream;
        this.list = list;
    }
    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream,character));
            String line = null;
            while ((line = br.readLine()) != null)
                if (line != null) list.add(line);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                br.close();
            }catch (IOException i){
                i.printStackTrace();
            }

        }
    }
    public void start() {
        thread = new Thread(this);
        /**set the thread as daemon*/
        thread.setDaemon(true);
        thread.start();
    }
    public void setCharacter(String encoding){
        this.character = encoding;
    }
}
