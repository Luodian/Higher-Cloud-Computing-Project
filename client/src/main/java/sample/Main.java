package sample;

import com.Utility.IgniteUtl.IgniteUtility;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Main extends Application {

    public static double screenWidth = 1920;
    public static double screenHeight = 1080;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        Screen screen = Screen.getPrimary ();

//        界面初始化
        Controller controller = fxmlLoader.getController();
        controller.init(primaryStage);

        Rectangle2D bounds = screen.getVisualBounds ();

        double minX = bounds.getMinX ();
        double minY = bounds.getMinY ();
        double maxX = bounds.getMaxX ();
        double maxY = bounds.getMaxY ();

        screenWidth = maxX - minX;
        screenHeight = maxY - minY;

        Scene scene = new Scene(root, (maxX - minX) * 0.6, (maxY - minY) * 0.6);

        primaryStage.setTitle ("Higher Cloud Compute Platform");
        primaryStage.setScene (scene);
		primaryStage.setFullScreen(true);
        primaryStage.show ();

    }


    public static void main(String[] args) {

        Screen screen = Screen.getPrimary ();

//        界面初始化

        Rectangle2D bounds = screen.getVisualBounds ();

        double minX = bounds.getMinX ();
        double minY = bounds.getMinY ();
        double maxX = bounds.getMaxX ();
        double maxY = bounds.getMaxY ();

        screenWidth = maxX - minX;
        screenHeight = maxY - minY;

        launch(args);

//        Map<String,String> map = Collections.singletonMap("ROLE", "DOWNLOAD");
//        Ignite ignite = IgniteUtility.startDefaultIgnite(map,"DOWNLOAD");
//        //使用Downloads标签注册这个ignite结点的信息。
//        Map<String,String> attrs = Collections.singletonMap("AGENT","Download");
//
//        //使用Downloads注册这个cache的名字。
//        Ignite ignite = IgniteUtility.startDefaultIgnite(attrs,"Downloads");
//
//        ClusterGroup remoteCluster = ignite.cluster().forRemotes();
//        ClusterGroup localCluster = ignite.cluster().forLocal();
//        Collection<ClusterNode> remoteNodes = remoteCluster.nodes();
//        Collection<ClusterNode> localNodes = localCluster.nodes();
//
//        for (ClusterNode clusterNode:localNodes) {
//            long heapSize = clusterNode.metrics().getHeapMemoryTotal();
//            int cpusNum = clusterNode.metrics().getTotalCpus();
//            System.out.println("heapSize:"+heapSize);
//            System.out.println("cpus:"+cpusNum);
////            Node curNode = new Node();
//
//        }
    }
}
