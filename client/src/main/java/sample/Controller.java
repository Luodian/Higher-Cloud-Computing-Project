package sample;

import com.Utility.DownloadUtility.KMeansDistributedClustererExample;
import com.Utility.IgniteUtl.IgniteUtility;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.util.typedef.CO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Controller {

    public  static boolean isFinishHelp = false;

    public static boolean askTask = false;
    public static boolean receiveTask = false;

    public ProgressBar helpDownloadPb;
    public Stage helpDownloadStage;

    App centerApp;
    GridPane bottom_pane;
    public static Ignite remoteIgnite;
    public static Ignite localIgnite;
    public Stage pmstage;
    public StackPane top_pane;

    //    画在图上的矩形结点
    class Node extends Button {
        private double direc;
        private double weight;  //范围是0-1
        private String msg;
        private int index;
        public ClusterNode clusterNode;
        Node(double weight, int index) {
            this.index = index;
            this.weight = weight;
            if (index == 0) {
                this.setText ("REM");
            } else {
                this.setText ("LOC");
            }
            this.setMaxSize(60,60);
            this.setMinSize (100 * weight, 100 * weight);
            this.setStyle ("-fx-background-color: gray");
        }

        public int getIndex() {
            return index;
        }

        String getMsg() {
            return msg;
        }

        void setMsg(String msg) {
            this.msg = msg;
        }

        double getDirec() {
            return direc;
        }

        void setDirec(double direc) {
            this.direc = direc;
        }

        public double getWeight () {
            return weight;
        }

        public void setWeight (double weight) {
            this.weight = weight;
        }
    }


    class App extends Button {
        String picPath;
        String name;
        int state;

        public App(String name, String picPath) {
            this.picPath = picPath;
            this.name = name;
            state = 0;
//			0表示还没有启动，1表示已经启动
        }
    }

    private boolean online = false;
    private int user_id = -1;
    private String user_email;
    private App portrait_btn;
    private App curApp;
//    一个网络中的所有结点，每个结点都是一个进程
    private List<Node> allOtherNodes = new ArrayList<>();
//    当前机器的结点
    private List<Node> myNodes = new ArrayList<>();
//    选择的结点，供应用服务使用
    private List<Node> selectedNodes = new ArrayList<>();

    
    public StackPane center_stack;
    public Label top_time;
    public Pane center_image;
//    public GridPane bottom_pane;
//    public HBox add_and_sub_box;

    //   详细个人信息
    private void handlePortrait(ActionEvent event) {
    final Stage infoStage = new Stage();
    GridPane infoPane = new GridPane();
    infoPane.setPadding(new Insets(20, 20, 20, 20));
    infoPane.setHgap(25);
    infoPane.setVgap(25);
    infoPane.setAlignment(Pos.CENTER);

    ImageView portait = new ImageView(new Image(Main.class.getResourceAsStream("Resources/wdhpor.png")));
    portait.setFitWidth(200);
    portait.setFitHeight(200);
    portait.setPreserveRatio(true);
    portait.setPickOnBounds(true);
    infoPane.add(portait, 0, 0, 3, 3);

    Label nickNameLabel = new Label("昵称");
    Label nickNameInfo = new Label("xx");
    infoPane.add(nickNameLabel, 0, 3);
    infoPane.add(nickNameInfo, 1, 3);

    Label IDlabel = new Label("ID");
    Label IDinfo = new Label("xx");
    infoPane.add(IDlabel, 0, 4);
    infoPane.add(IDinfo, 1, 4);

    Label accumulateTimeLabel = new Label("积累算时");
    Label accumuateTimeInfo = new Label("xx");
    infoPane.add(accumulateTimeLabel, 0, 5);
    infoPane.add(accumuateTimeInfo, 1, 5);


    Scene scene = new Scene(infoPane,Main.screenWidth /5 , Main.screenHeight/2);
    scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
    infoStage.setScene(scene);
    infoStage.setFullScreen(false);
    infoStage.setResizable(false);
    infoStage.show();

}

    private void afterSuccessLogin() {
//        add_and_sub_box.setVisible(true);
        online = true;
        centerApp.setVisible(false);
        bottom_pane.setVisible(true);

        final ProgressBar pb =  new ProgressBar();
        pb.setProgress(-1);

        final ProgressIndicator pin = new ProgressIndicator();
        pin.setProgress(-1);
        Thread threadWithRemote = new Thread() {
            @Override
            public void run() {
                Map<String,String> map = Collections.singletonMap("ROLE", "REMOTENODE");
                remoteIgnite = IgniteUtility.startDefaultIgnite(map,"DOWNLOAD", "REMOTENODE");
                ClusterNode clusterNode = remoteIgnite.cluster().node();
                int cpusNum = clusterNode.metrics().getTotalCpus();
                double heapSize = clusterNode.metrics().getHeapMemoryTotal() / (1024*1024*1024);

                Node curNode = new Node(1,1);
                curNode.clusterNode = clusterNode;
                String str = "heapSize = " + heapSize + "GB\ncpus = "+cpusNum;
                curNode.setMsg(str);
                curNode.setDirec(0);
//                myNodes.add(curNode);

            }
        };
        threadWithRemote.start();

        Thread threadWithLocal = new Thread() {
            @Override
            public void run() {
                Map<String,String> map = Collections.singletonMap("ROLE", "LOCALNODE");
                localIgnite = IgniteUtility.startDownloadIgnite(map,"DOWNLOAD", "LOCALNODE");

            }
        };
        threadWithLocal.start();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(500);
                        if (remoteIgnite!=null&&localIgnite!=null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    pin.setVisible(false);
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                while(true) {
                                                    sleep(500);
                                                    if (receiveTask && !askTask) {
                                                        Platform.runLater(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                isFinishHelp = false;
                                                                helpDownloadStage = new Stage();

                                                                helpDownloadStage.setTitle("正在帮助远程机器下载");
                                                                helpDownloadStage.initOwner(pmstage);
                                                                GridPane helpDownloadPane = new GridPane();
                                                                Button helpDownLoadBtn = new Button("正在下载");
                                                                helpDownLoadBtn.setStyle("-fx-background-color: gray");
                                                                helpDownLoadBtn.setDisable(true);
                                                                helpDownloadPane.add(helpDownLoadBtn,1,3,1,1);
                                                                helpDownloadPb =  new ProgressBar();
                                                                helpDownloadPb.setProgress(-1);
                                                                helpDownloadPane.setAlignment(Pos.CENTER);
                                                                helpDownloadPane.setHgap(20);
                                                                helpDownloadPane.setVgap(20);
                                                                helpDownloadPane.setPadding(new Insets(20,10,20,10));
                                                                helpDownloadPane.add(helpDownloadPb,0,1,3,2);
                                                                new Thread() {
                                                                    @Override
                                                                    public void run() {
                                                                        Platform.runLater(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                while (true) {
                                                                                    try {
                                                                                        sleep(500);
                                                                                        if (isFinishHelp) {
                                                                                            askTask = false;
                                                                                            helpDownloadPb.setVisible(false);
                                                                                            helpDownLoadBtn.setDisable(false);
                                                                                            helpDownLoadBtn.setStyle("-fx-background-color: green");
                                                                                            helpDownLoadBtn.setText("下载完成");
                                                                                            helpDownLoadBtn.setOnAction(new EventHandler<ActionEvent>() {
                                                                                                @Override
                                                                                                public void handle(ActionEvent event) {
                                                                                                    helpDownloadStage.close();
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                        return;
                                                                                    } catch (InterruptedException e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }.start();
                                                                Scene scene = new Scene(helpDownloadPane,Main.screenWidth/4,Main.screenHeight/4);
                                                                helpDownloadStage.setAlwaysOnTop(true);
                                                                helpDownloadStage.setScene(scene);
                                                                helpDownloadStage.show();
                                                            }
                                                        });
                                                        return;
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();

                                    afterConnect();
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

        if (threadWithLocal.isAlive() || threadWithRemote.isAlive()) {
//            DoubleProperty stroke = new SimpleDoubleProperty(100.0);
//            timeline.setCycleCount(Timeline.INDEFINITE);
//
//            final KeyValue kv = new KeyValue(stroke, 0);
//            final KeyFrame kf = new KeyFrame(Duration.millis(1500), kv);
//
//            timeline.getKeyFrames().add(kf);
//            timeline.play();
////            timeline.setOnFinished(new EventHandler<ActionEvent>() {
////                @Override
////                public void handle(ActionEvent event) {
////                    Platform.runLater(new Runnable() {
////                        @Override
////                        public void run() {
////                            System.out.println("将要停止");
////                            afterConnect(ignite);
////                            System.out.println("等待动画停止");
////                        }
////                    });
////                }
////            });
//            loadingVBox = new VBox(3);
//
//            StackPane progressIndicator = new StackPane();
//
//            Rectangle bar = new Rectangle(350,13);
//            bar.setFill(Color.TRANSPARENT);
//            bar.setStroke(Color.WHITE);
//            bar.setArcHeight(15);
//            bar.setArcWidth(15);
//            bar.setStrokeWidth(2);
//
//
//            Rectangle progress = new Rectangle(342,6);
//            progress.setFill(Color.WHITE);
//            progress.setStroke(Color.WHITE);
//            progress.setArcHeight(8);
//            progress.setArcWidth(8);
//            progress.setStrokeWidth(1.5);
//            progress.getStrokeDashArray().addAll(3.0,7.0,3.0,7.0);
//            progress.strokeDashOffsetProperty().bind(stroke);
//
//
//            progressIndicator.getChildren().add(progress);
//            progressIndicator.getChildren().add(bar);
//
//            loadingVBox.getChildren().add(progressIndicator);
//
//            Text label = new Text("Loading...");
//            label.setFill(Color.WHITE);
//
//            loadingVBox.getChildren().add(label);
//
//            center_stack.getChildren().add(loadingVBox);
//            loadingVBox.setPickOnBounds(false);

//            ProgressIndicator p1 = new ProgressIndicator();
//            center_stack.getChildren().add(p1);
//            StackPane.setAlignment(loadingVBox,Pos.CENTER);
            center_stack.getChildren().addAll(pb,pin);
            pb.setVisible(false);
            pb.setPickOnBounds(false);
            pin.setPickOnBounds(false);
            StackPane.setMargin(pb,new Insets(center_image.getHeight()/2-50,Main.screenWidth/2, center_image.getHeight()/2-50,Main.screenWidth/2));
            StackPane.setMargin(pin,new Insets(center_image.getHeight()/2-50,Main.screenWidth/2, center_image.getHeight()/2-50,Main.screenWidth/2));
        }

    }

    private void afterConnect() {

        ClusterGroup remoteCluster = remoteIgnite.cluster().forAttribute("ROLE","REMOTENODE");

        Collection<ClusterNode> remoteNodes = remoteCluster.nodes();

        double maxWeight = -1;
        double minWeight = Double.MAX_VALUE;
        for (ClusterNode node:remoteNodes) {
            long curWeight = node.metrics().getHeapMemoryTotal()/(1024*1024*1024) * (node.metrics().getTotalCpus());
            maxWeight = curWeight>maxWeight?curWeight:maxWeight;
            minWeight = curWeight<maxWeight?curWeight:minWeight;
        }

        int index = 0;

//		个人信息部分的更改
        ImageView portImg = new ImageView(new Image(Main.class.getResourceAsStream("Resources/wdhpor.png")));
        portImg.setFitWidth(110);
        portImg.setFitHeight(100);
        portrait_btn.setGraphic(portImg);
        portrait_btn.setPadding(new Insets(0, 0, 0, 0));

//		服务器图片的更改
        center_image.getChildren ().clear ();
        selectedNodes.clear ();
        allOtherNodes.clear ();


//		这个Button比较大，
        Button ownBtn = new Button();
        ownBtn.setText("进程个数" + myNodes.size());
        ownBtn.setPrefWidth(100);
        ownBtn.setPrefHeight(100);
        ownBtn.setLayoutX (center_image.getWidth () * 0.5);
        ownBtn.setLayoutY (center_image.getHeight () * 0.5);
        ownBtn.setStyle ("-fx-background-color: #3effa0");

//		获取网格上的机器,得到allOtherNodes的个数和配置信息， 配置信息设置为msg属性，ping 一下得到对方距离，设置为Direc,
        index = 0;
        for (ClusterNode clusterNode:remoteNodes) {
            int cpusNum = clusterNode.metrics().getTotalCpus();
            double heapSize = clusterNode.metrics().getHeapMemoryTotal() / (1024*1024*1024);
            double curWeight = 0.5 + (heapSize*cpusNum-minWeight) / (2*(maxWeight-minWeight));
            Node curNode = new Node(curWeight,index++);
            curNode.clusterNode = clusterNode;
            String str = "heapSize = " + heapSize + "GB\ncpus = "+cpusNum;
            curNode.setMsg(str);
            curNode.setDirec(center_image.getHeight () / 6 + center_image.getHeight () / 3 * Math.random ());
            allOtherNodes.add(curNode);
        }

//        int nodeNum = 12;
        int nodeNum = allOtherNodes.size();
        double meanDigree = 2 * Math.PI / nodeNum;
        for (int i = 0; i < nodeNum; i++) {
//            final Node curNode = new Node (Math.random (), i + 1);
            final Node curNode = allOtherNodes.get(i);
//            curNode.setMsg("a:1\nb:23");
//            curNode.setDirec (center_image.getHeight () / 6 + center_image.getHeight () / 3 * Math.random ());

            curNode.setTooltip(new Tooltip(curNode.getMsg()));
            curNode.setOnAction (new EventHandler<ActionEvent> () {
                @Override
                public void handle (ActionEvent event) {

                    final Stage infoStage = new Stage();
                    infoStage.initOwner(pmstage);
                    GridPane infoPane = new GridPane();
                    infoPane.setPadding(new Insets(20, 20, 20, 20));
                    infoPane.setHgap(25);
                    infoPane.setVgap(25);
                    infoPane.setAlignment(Pos.CENTER);

                    Label msgLabel = new Label(curNode.getMsg());
                    infoPane.add(msgLabel, 1, 0, 5, 5);

                    Button selectBtn = new Button();
                    if (selectedNodes.contains(curNode)) {
                        selectBtn.setText("取消该进程");
                    } else {
                        selectBtn.setText("选择该进程");
                    }
                    selectBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (selectedNodes.contains(curNode))
                            {
                                selectedNodes.remove(curNode);
                                curNode.setStyle ("-fx-background-color: gray");
                            } else {
                                selectedNodes.add(curNode);
                                curNode.setStyle("-fx-background-color: #ff5758");
                            }
                            infoStage.close();
                        }
                    });
                    infoPane.add(selectBtn, 1, 6, 5, 5);

                    Scene scene = new Scene(infoPane, 400, 500);
                    scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
                    infoStage.setScene(scene);
                    infoStage.show();

                }
            });
            curNode.setLayoutX (ownBtn.getLayoutX () + curNode.getDirec () * Math.cos (i * meanDigree));
            curNode.setLayoutY (ownBtn.getLayoutY () + curNode.getDirec () * Math.sin (i * meanDigree));
            allOtherNodes.add (curNode);
            center_image.getChildren ().add (curNode);
        }
        center_image.getChildren().add(ownBtn);
        center_image.setId("center_image");
        center_image.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());

    }
    private void handleSettings(ActionEvent event) {
        if (online) {
            final Stage settingsStage = new Stage();
            settingsStage.initOwner(pmstage);
            GridPane settingsPane = new GridPane();
            settingsPane.setPadding(new Insets(20, 20, 20, 20));
            settingsPane.setHgap(20);
            settingsPane.setVgap(20);
            settingsPane.setAlignment(Pos.CENTER);

            Label settingsTitle = new Label("配置信息");
            settingsTitle.setId("settings_title");
            settingsPane.add(settingsTitle, 0, 0, 3, 1);

//		主机名
            Label hostnameLabel = new Label("hostname:");
            Label hostnameInfo = new Label("xx");
            settingsPane.add(hostnameLabel, 0, 1);
            settingsPane.add(hostnameInfo, 1, 1);

//		内网IP
            Label inetIPLabel = new Label("inet Label");
            Label inetIPInfo = new Label("xx");
            settingsPane.add(inetIPLabel, 0, 2);
            settingsPane.add(inetIPInfo, 1, 2);

//		CPU
            Label CPULabel = new Label("CPU");
            Label CPUInfo = new Label("xx");
            settingsPane.add(CPULabel, 0, 3);
            settingsPane.add(CPUInfo, 1, 3);

//		内存
            Label memLabel = new Label("内存");
            Label memInfo = new Label("xx");
            settingsPane.add(memLabel, 0, 4);
            settingsPane.add(memInfo, 1, 4);

//		GPU
            Label GPULabel = new Label("GPU");
            Label GPUInfo = new Label("xx");
            settingsPane.add(GPULabel, 0, 5);
            settingsPane.add(GPUInfo, 1, 5);

            Label scoreLabel = new Label("算力评分");
            final Button score = new Button("开始评分");
            score.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (score.getText().equals("开始评分")) {
                        int scoreNum = 0;
//					进行评分,更改scoreNum
                        score.setText(String.valueOf(scoreNum));
                    }
                }
            });
            settingsPane.add(scoreLabel, 0, 6);
            settingsPane.add(score, 1, 6);

            Label thresholdLabel = new Label("启动阈值");
            Tooltip thresholdTooltip = new Tooltip();
            thresholdTooltip.setText("当电脑空闲资源高于此比例时启动，默认为60%");
            thresholdLabel.setTooltip(thresholdTooltip);
            TextField threshold = new TextField();
            threshold.setTooltip(thresholdTooltip);
            settingsPane.add(thresholdLabel, 0, 7);
            settingsPane.add(threshold, 1, 7);

            Button ensureBtn = new Button("确定");
            Button cancelBtn = new Button("取消");
            ensureBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
//				保存这个阈值

                    settingsStage.close();
                }
            });
            cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    settingsStage.close();
                }
            });
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.BOTTOM_CENTER);
            hBox.setSpacing(150);
            hBox.getChildren().addAll(ensureBtn, cancelBtn);
            settingsPane.add(hBox, 0, 8, 3, 1);

            Scene scene = new Scene(settingsPane, 400, 500);
            scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
            settingsStage.setScene(scene);
            settingsStage.show();
        } else {
            alertWhenNotLogin();
        }
    }

    private void alertWhenNotLogin() {
        final Alert alert = new Alert(Alert.AlertType.WARNING, "请先登陆", new ButtonType("确定", ButtonBar.ButtonData.YES));
        alert.initOwner(pmstage);
        alert.setTitle("请先登陆");
        alert.showAndWait();
    }

    private void login () {
        final Stage loginStage = new Stage ();
        loginStage.setAlwaysOnTop(true);
        GridPane loginPane = new GridPane ();
        loginPane.setPadding (new Insets (20, 20, 20, 20));
        loginPane.setHgap (25);
        loginPane.setVgap (25);
        loginPane.setAlignment (Pos.CENTER);

        Label higherCloud = new Label ("天算计划");
        higherCloud.setId ("higher_cloud");
        loginPane.add (higherCloud, 0, 0, 3, 1);

        Label userNameLabel = new Label("ID/Email");
        Label pswLabel = new Label ("密码");
        final TextField userName = new TextField();
        userName.setPrefWidth (200);
        final PasswordField psw = new PasswordField();
        psw.setPrefWidth (200);
        loginPane.add (userNameLabel, 0, 2);
        loginPane.add (pswLabel, 0, 3);
        loginPane.add (userName, 1, 2, 3, 1);
        loginPane.add (psw, 1, 3, 3, 1);

        Button regiBtn = new Button("注册");
        Button loginBtn = new Button("登陆");
        HBox hBox = new HBox ();
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.setSpacing(150);
        hBox.getChildren().addAll(regiBtn, loginBtn);
        loginPane.add (hBox, 1, 4, 3, 1);
        final Label loginInfo = new Label("");
        loginInfo.setVisible(false);
        loginPane.add(loginInfo, 2, 5, 3, 1);

        regiBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                regi();

            }
        });
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent event) {
                try {
                    String loginMsg;
                    if (userName.getText().equals("") || psw.getText().equals("")) {
                        loginInfo.setText("请输入完整信息");
                        loginInfo.setVisible(true);
                    } else {
                        int cur_id = -1;
                        String cur_email = "";
                        if ((userName.getText().contains("@")) && (userName.getText().contains(".com"))) {
                            cur_email = userName.getText();
                        } else {
                            cur_id = Integer.valueOf(userName.getText());
                        }
                        String loginRst = ServerUtil.login(cur_id, cur_email, psw.getText());
                        if (loginRst.contains("SUCCESS")) {
                            int id_start = loginRst.indexOf("id=") + 3;
                            int id_finish = loginRst.indexOf("rtn_email=");
                            user_id = Integer.valueOf(loginRst.substring(id_start, id_finish));
                            user_email = loginRst.substring(id_finish + 10);
                            loginStage.close();
                            afterSuccessLogin();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        Scene scene = new Scene (loginPane, 800, 500);
        scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.initOwner(pmstage);
        loginStage.show();
    }


    private void regi() {
        final Stage registerStage = new Stage();
        registerStage.initOwner(pmstage);
        GridPane registerPane = new GridPane();
        registerPane.setPadding(new Insets(20, 20, 20, 20));
        registerPane.setHgap(20);
        registerPane.setVgap(20);
        registerPane.setAlignment(Pos.CENTER);

        Label regiTitle = new Label("注册");
        regiTitle.setId("regi_title");
        registerPane.add(regiTitle, 0, 0, 3, 1);

        Label userNameLabel = new Label("用户名");
        final TextField userName = new TextField();
        userName.setPrefWidth(200);

        Label emailLabel = new Label("邮箱");
        final TextField email = new TextField();
        emailLabel.setPrefWidth(200);

        Label pswLabel = new Label("密码");
        final PasswordField psw = new PasswordField();
        psw.setPrefWidth(200);
        Label ensurePswLabel = new Label("确认密码");
        PasswordField ensurePsw = new PasswordField();
        ensurePsw.setPrefWidth(200);

        Button sendValiCode = new Button("发送验证码");
        final Label valiMsg = new Label();
        valiMsg.setVisible(false);

        final TextField valiCode = new TextField();
        valiCode.setVisible(false);
        valiCode.setPrefWidth(200);

        final Button ensureRegiBtn = new Button("激活注册");
        ensureRegiBtn.setVisible(false);

//		注册之后的返回消息，默认不可见，若是注册失败，就会显示出来
        final Label regiRs = new Label();
        regiRs.setId("regi_rs");
        regiRs.setVisible(false);

        sendValiCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String veriEmail = ServerUtil.check_email_exist(email.getText());
                    if (veriEmail.equals("SUCCESS")) {
//						说明已经被注册了
                        valiMsg.setText("该邮箱已被使用");
                        valiMsg.setVisible(true);
                    } else if (veriEmail.equals("FAIL")) {
//                      该邮箱可以使用，执行注册操作
                        String regiMsg = ServerUtil.register(email.getText(), userName.getText(), psw.getText());
                        if (regiMsg.contains("SUCCESS")) {
                            user_id = Integer.valueOf(regiMsg.substring(regiMsg.indexOf("is ") + 3));
                            valiMsg.setText("激活码已发送\n您的用户id为" + user_id + "\n请于邮箱中查看并填在此处");
                            valiMsg.setVisible(true);
                            valiCode.setVisible(true);
                            ensureRegiBtn.setVisible(true);
                        } else {
                            valiMsg.setText("个人信息发送失败");
                            valiMsg.setVisible(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ensureRegiBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//				向服务器发送注册请求，以及个人信息
                try {
                    String activeMsg = ServerUtil.active(user_id, valiCode.getText());
                    if (activeMsg.equals("SUCCESS")) {
                        regiRs.setText("激活成功");
                        regiRs.setVisible(true);
                    } else {
                        regiRs.setText("注册失败");
                        regiRs.setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        registerPane.add(userNameLabel, 0, 2);
        registerPane.add(userName, 1, 2, 3, 1);

        registerPane.add(emailLabel, 0, 3);
        registerPane.add(email, 1, 3, 3, 1);

        registerPane.add(pswLabel, 0, 4);
        registerPane.add(psw, 1, 4, 3, 1);

        registerPane.add(ensurePswLabel, 0, 5);
        registerPane.add(ensurePsw, 1, 5, 3, 1);

        registerPane.add(sendValiCode, 1, 6);

        registerPane.add(valiMsg, 0, 7);
        registerPane.add(valiCode, 1, 7, 3, 1);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(ensureRegiBtn);

        registerPane.add(hBox, 1, 8, 3, 1);

        registerPane.add(regiRs, 4, 8, 3, 1);
        Scene scene = new Scene(registerPane, 800, 500);
        scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
        registerStage.setScene(scene);
        registerStage.show();
    }
    public void init(Stage primaryStage){

        pmstage = primaryStage;
        online = false;



//		时间
        StackPane.setMargin(top_time,new Insets(6,Main.screenWidth/2-100,6,Main.screenWidth/2-100));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
                        top_time.setFont(Font.font("Consolos", FontWeight.BOLD,18));
                        top_time.setText(df.format(new Date()));
                    }
                });

            }
        }, 0, 60000);




        List<App> appList = new ArrayList<>();

        final App loginApp = new App("登录","Resources/wdhpor.png");
        centerApp = loginApp;
        loginApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!online) {
                    login();
                }
            }
        });

        final App portraitApp = new App("个人信息", "Resources/wdhpor.png");
        portrait_btn = portraitApp;
        portraitApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (online) {
                    handlePortrait(event);
                }
//                else {
////                    afterSuccessLogin();
//                    login();
//                }
            }
        });
        final App downloadApp = new App("下载", "Resources/image.jpg");
        downloadApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                curApp = downloadApp;
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("download.fxml"));
                    Parent root = fxmlLoader.load();

                    DownController downController = fxmlLoader.getController();
                    downController.init(primaryStage,selectedNodes,myNodes);

                    Scene scene = new Scene(root, Main.screenWidth*0.6, Main.screenHeight*0.6);
                    scene.getStylesheets().add(Main.class.getResource("download.css").toExternalForm());
                    Stage downStage = new Stage();
                    downStage.setAlwaysOnTop(true);
                    downStage.setResizable(false);
                    downStage.initOwner(pmstage);
                    downStage.setTitle ("Download App");
                    downStage.setScene (scene);
                    downStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            selectedNodes.clear();
                        }
                    });
                    downStage.show ();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final App mlApp = new App("KMeans", "Resources/46.jpg");
        mlApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("选择数据文件");
                String mlFilePath = fileChooser.showOpenDialog(pmstage).getPath();
                curApp = mlApp;
                try {
                    KMeansDistributedClustererExample.execute(mlFilePath);
                    Stage stage = new Stage();
                    Pane pane = new Pane();
                    pane.setPrefWidth(640);
                    pane.setPrefHeight(480);
                    Image imgML = new Image("./ML/result.png");
                    ImageView imageViewML = new ImageView();
                    imageViewML.setImage(imgML);
                    imageViewML.setPreserveRatio(true);
                    imageViewML.setPickOnBounds(false);
                    imageViewML.setFitHeight(pane.getPrefHeight());
                    imageViewML.setFitWidth(pane.getPrefWidth());
                    pane.getChildren().add(imageViewML);
                    stage.setResizable(false);
                    stage.setScene(new Scene(pane,Main.screenWidth/3+100,Main.screenHeight/2));
                    stage.setTitle("KMeans 结果");
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            selectedNodes.clear();
                        }
                    });
                    stage.show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        final App settings = new App("配置", "Resources/settings.png");
        settings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleSettings(event);
            }
        });
        App sayHelloApp = new App("SayHello","Resources/slaver.jpg");
        sayHelloApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        App refreshApp = new App("刷新","Resources/refresh.jpg");
        refreshApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allOtherNodes.clear();
                afterConnect();
            }
        });

        appList.add(loginApp);
        appList.add(portraitApp);
        appList.add(downloadApp);
        appList.add(mlApp);
        appList.add(settings);
        appList.add(refreshApp);
        appList.add(sayHelloApp);

        bottom_pane = new GridPane();
        for (final App app : appList) {
            if (app.name.equals("登录")) {
                center_stack.getChildren().add(app);
                app.setPickOnBounds(false);
                StackPane.setMargin(app,new Insets(center_image.getHeight()/2-50,Main.screenWidth/2, center_image.getHeight()/2-50,Main.screenWidth/2));
            } else{
                bottom_pane.add(app, appList.indexOf(app)-1, 0);
            }
            Image srcImage = new Image(Main.class.getResourceAsStream(app.picPath));
            ImageView portImg = new ImageView(srcImage);
            Rectangle clip = new Rectangle(
                    srcImage.getWidth(), srcImage.getHeight()
            );
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            portImg.setClip(clip);
            if (app.name.equals("登录")){
                portImg.setFitWidth(150);
                portImg.setFitHeight(140);
            } else {
                portImg.setFitWidth(110);
                portImg.setFitHeight(100);
            }
            app.setGraphic(portImg);
            app.setPadding(new Insets(0, 0, 0, 0));
            Tooltip tooltip = new Tooltip();
            tooltip.setText(app.name);
            app.setTooltip(tooltip);
            app.setMouseTransparent(false);
        }


        bottom_pane.setMaxHeight(Main.screenHeight/7);
        bottom_pane.setMaxWidth(Main.screenWidth/2);
        bottom_pane.setHgap(10);
        bottom_pane.setPadding(new Insets(5, 20, 15, 20));
        bottom_pane.setAlignment(Pos.BOTTOM_CENTER);
        bottom_pane.setEffect(new DropShadow(20, Color.GRAY));
        center_stack.setAlignment(Pos.CENTER);
        center_stack.getChildren().add(bottom_pane);
        bottom_pane.setVisible(false);
//        StackPane.setMargin(bottom_pane, new Insets(900, 1000, 50, 1000));
        System.out.println(Main.screenWidth);
        System.out.println(Main.screenHeight);
        StackPane.setMargin(bottom_pane, new Insets(Main.screenHeight*9/11, 1000, Main.screenHeight/15, 1000));
        bottom_pane.setPickOnBounds(false);

//		初始化中心图片
        center_image.getChildren().clear();
        ImageView centerImageView = new ImageView();
        centerImageView.setPickOnBounds(true);
        centerImageView.setPreserveRatio(true);
        Image img = new Image(Main.class.getResourceAsStream("Resources/2.jpg"));
        centerImageView.setPreserveRatio(true);
        centerImageView.setImage(img);
        centerImageView.setFitHeight(center_image.getHeight());
        centerImageView.setFitWidth(center_image.getWidth());
        center_image.getChildren().add(centerImageView);


    }



}
