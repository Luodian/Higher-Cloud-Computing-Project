package sample;

import com.Utility.IgniteUtl.IgniteUtility;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.ignite.Ignite;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DownController {
    public TextField url_text;
    public Pane down_canvas;
    public Button down_btn;


    public Stage pmstage;

    private List<Controller.Node> selectedNodes;
    private List<Controller.Node> myNodes;
    private List<Line> downLines = new ArrayList<>();
    private List<Line> mergeLines = new ArrayList<>();
    private List<Line> curLines;
    private String filePath = "";
    private String fileName="";
    private String url = "";

    public static boolean isFinishMerge = false;
    public static boolean isFinishPutIntoCache = false;

    private List<Circle> downCircles = new ArrayList<>();
    private List<Circle> mergeCircles= new ArrayList<>();


//    public void openDownLoad(Stage primaryStage, List<Controller.Node> selectedNodes, List<Controller.Node> myNodes) throws IOException {
//        pmstage = primaryStage;
//        this.selectedNodes = selectedNodes;
//        this.myNodes = myNodes;
//
//        Parent root = FXMLLoader.load(getClass().getResource("download.fxml"));
//        Screen screen = Screen.getPrimary ();
//
//        Rectangle2D bounds = screen.getVisualBounds ();
//
//        double minX = bounds.getMinX ();
//        double minY = bounds.getMinY ();
//        double maxX = bounds.getMaxX ();
//        double maxY = bounds.getMaxY ();
//
//        Scene scene = new Scene(root, (maxX - minX) * 0.6, (maxY - minY) * 0.6);
//        scene.getStylesheets().add(Main.class.getResource("download.css").toExternalForm());
//        Stage downStage = new Stage();
//        downStage.initOwner(pmstage);
//        downStage.setTitle ("Download App");
//        downStage.setScene (scene);
//        downStage.show ();
//
//
//
////        down_grid.setPadding(new Insets(20,20,20,20));
//
////        down_canvas.setPrefHeight(scene.getHeight()*0.6);
////        down_canvas.setPrefWidth(scene.getWidth()*0.6);
//    }

    private void alertWhenFinish() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "下载完成", new ButtonType("确定", ButtonBar.ButtonData.YES));

        alert.initOwner(pmstage);
        alert.setTitle("下载完成");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.toFront();
        stage.show();
//        alert.showAndWait();
    }

    public void init(Stage primaryStage, List<Controller.Node> selectedNodes, List<Controller.Node> myNodes){
        pmstage = primaryStage;
        this.selectedNodes = selectedNodes;
        this.myNodes = myNodes;

        isFinishPutIntoCache = false;
        isFinishMerge = false;

        url_text.setPrefWidth(down_canvas.getPrefWidth()*2/3);
        down_btn.setPrefWidth(down_canvas.getPrefWidth()/6);

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        if (isFinishMerge) {
                            down_btn.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        Desktop.getDesktop().open(new File("./Downloads"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
//                            for (Circle circle:mergeCircles) {
//                                circle.setVisible(false);
//                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    curLines = mergeLines;
                                    toMove();
                                }
                            });
                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            for (Circle circle:mergeCircles) {
                                circle.setVisible(false);
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    alertWhenFinish();
                                    down_btn.setText("下载完成");
                                }
                            });
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        down_btn.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {

                if(!url_text.getText().equals("")) {
                    url = url_text.getText();
                    int index = url.lastIndexOf('/');
                    fileName = url.substring(index+1);
                    System.out.println("filename"+fileName);
                }

//                DirectoryChooser directoryChooser = new DirectoryChooser();
//                directoryChooser.setTitle("选择下载路径");

//                filePath = directoryChooser.showDialog(pmstage).getPath();
                filePath = "./Downloads";

                Collection<UUID> collection = new ArrayList<>();
                for (Controller.Node curNode:selectedNodes) {
                    collection.add(curNode.clusterNode.id());
                }

                if (curLines==null) {
                    if (Controller.localIgnite!=null && Controller.remoteIgnite!=null){
//                        监听是否所有节点都回传回了数据，成功之后会自动启动合并
                        IgniteUtility.listenDownloading(Controller.localIgnite,filePath,fileName,"DOWNLOAD",selectedNodes.size()+myNodes.size());
                        System.out.println(url);
                        IgniteUtility.multicast(Controller.localIgnite,collection,url,selectedNodes.size()+myNodes.size());
                        Controller.askTask = true;
                        startDownAndPut();
                        new Thread() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        sleep(1000);
                                        if (isFinishPutIntoCache) {
                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (Circle circle:downCircles){
                                                        circle.setVisible(false);
                                                    }
                                                    startMerge();
                                                }
                                            });
                                            return;
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();
                    }

                }
            }
        });

        String downFileMsg = "文件名：操作系统课件.pdf\n文件大小：100MB";
        Button serverBtn = new Button("服务器");
        serverBtn.setPrefWidth(100);
        serverBtn.setPrefHeight(100);
        serverBtn.setStyle ("-fx-background-color: #3effa0");
        serverBtn.setTooltip(new Tooltip(downFileMsg));

        serverBtn.setLayoutX(down_canvas.getLayoutX() + down_canvas.getPrefWidth()/4);
        serverBtn.setLayoutY(down_canvas.getLayoutY()+down_canvas.getPrefHeight()/2);

//        down_canvas.getChildren().add(serverBtn);

        Button localBtn = new Button("本机");
        localBtn.setPrefSize(100,100);
        localBtn.setStyle ("-fx-background-color: #3effa0");
        localBtn.setTooltip(new Tooltip(downFileMsg));

        localBtn.setLayoutX(down_canvas.getLayoutX() + down_canvas.getPrefWidth());
        localBtn.setLayoutY(down_canvas.getLayoutY()+down_canvas.getPrefHeight()/2);

//        down_canvas.getChildren().add(localBtn);

        List<Controller.Node> allNodes = new ArrayList<>();
        allNodes.addAll(selectedNodes);
        allNodes.addAll(myNodes);
        System.out.println("allNodes 数量:"+allNodes.size());
        int up = -1;
        int index=0;
        double rectStartX = 0;
        double rectStartY = 0;
        for (Controller.Node curNode : allNodes){

            if (up == -1) {
                up = 1;
            } else {
                up = -1;
            }
            curNode.setLayoutX(serverBtn.getLayoutX() + down_canvas.getPrefWidth()/4);
            curNode.setLayoutY(serverBtn.getLayoutY() + 65 * index * up);
//            if (allNodes.indexOf(curNode) == 0) {
            double curX = curNode.getLayoutX () + curNode.getMinWidth () * 0.5+ down_canvas.getPrefWidth()/4 -30;
            double curY = curNode.getLayoutY () + curNode.getMinHeight () * 0.5 - 30;

            rectStartX = rectStartX >curX ?curX:rectStartX;
            rectStartY = rectStartY > curY?curY:rectStartY;
            if (rectStartX == 0 || rectStartY == 0) {
                rectStartX = curX;
                rectStartY = curY;
            }
//                rectStartX = curNode.getLayoutX () + curNode.getMinWidth () * 0.5+ down_canvas.getPrefWidth()/4 -30;
//                rectStartY = curNode.getLayoutY () + curNode.getMinHeight () * 0.5 - 30;
//            }
            Line line = new Line(serverBtn.getLayoutX () + serverBtn.getPrefWidth () * 0.5,
                    serverBtn.getLayoutY () + serverBtn.getPrefHeight () * 0.5,
                    curNode.getLayoutX () + curNode.getMinWidth () * 0.5,
                    curNode.getLayoutY () + curNode.getMinHeight () * 0.5
                    );

            Line line1 = new Line(curNode.getLayoutX () + curNode.getMinWidth () * 0.5,
                    curNode.getLayoutY () + curNode.getMinHeight () * 0.5,
                    curNode.getLayoutX () + curNode.getMinWidth () * 0.5+ down_canvas.getPrefWidth()/4,
                    curNode.getLayoutY () + curNode.getMinHeight () * 0.5);

            Line line2 = new Line(curNode.getLayoutX () + curNode.getMinWidth () * 0.5+ down_canvas.getPrefWidth()/4,
                    curNode.getLayoutY () + curNode.getMinHeight () * 0.5,localBtn.getLayoutX () + localBtn.getPrefWidth () * 0.5,
                    localBtn.getLayoutY () + localBtn.getPrefHeight () * 0.5);
            line.setVisible(false);
            line1.setVisible(false);
            line2.setVisible(false);
            down_canvas.getChildren().addAll(line,line1,line2,curNode);
            downLines.add(line);
            downLines.add(line1);
            mergeLines.add(line2);
            if (up == 1) {
                index++;
            }
        }
        down_canvas.getChildren().addAll(serverBtn,localBtn);
        Button sharedMemBlock = new Button("共享内存");
        sharedMemBlock.setLayoutX(rectStartX);
        sharedMemBlock.setLayoutY(rectStartY);
        sharedMemBlock.setPrefWidth(100);
        sharedMemBlock.setPrefHeight(down_canvas.getPrefHeight()*0.5);

//        Rectangle rectangle = new Rectangle();
//        rectangle.setLayoutX(rectStartX);
//        rectangle.setLayoutY(rectStartY);
//        rectangle.setWidth(100);
//        rectangle.setHeight(down_canvas.getPrefHeight()*0.5);
//        rectangle.setStyle("-fx-background-color: #3effa0");
//        rectangle.setAccessibleText("Share\nMemory");
        down_canvas.getChildren().add(sharedMemBlock);

    }
    private void startDownAndPut() {
       curLines = downLines;
       toMove();
    }
    private void startMerge() {
       curLines = mergeLines;
       if (!isFinishMerge)
            toMove();
    }

    private void toMove(){
        for (Line line : curLines) {
            Circle circle = new Circle(line.getStartX(), line.getStartY(), 7, Color.ORANGE);

            Path curPath = new Path();
            curPath.getElements().add(new MoveTo(line.getStartX(),line.getStartY()));
            curPath.getElements().add(new CubicCurveTo(0.5 * (line.getStartX() + line.getEndX()),
                    0.5 * (line.getEndY() + line.getStartY()), 0.3*line.getStartX()+0.7*line.getEndX(),
                    0.3*line.getStartY()+0.7*line.getEndY(), line.getEndX(), line.getEndY()));

            PathTransition curPathTransition = new PathTransition();
            curPathTransition.setDuration(Duration.millis(line.getBaselineOffset() / (down_canvas.getPrefWidth() / 2) * 30000));
            curPathTransition.setPath(curPath);
            curPathTransition.setNode(circle);
            curPathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            curPathTransition.setCycleCount(Timeline.INDEFINITE);
            curPathTransition.setAutoReverse(false);
            curPathTransition.play();

            down_canvas.getChildren().addAll(circle,curPath);
            if (curLines == downLines)
                downCircles.add(circle);
            else
                mergeCircles.add(circle);

        }
    }

}
