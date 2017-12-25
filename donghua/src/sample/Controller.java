package sample;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class Node {
    private double direc;
    private double weight;  //范围是0-1
    private Button btn;
    public Node(double weight, int index){
        this.weight = weight;
        btn = new Button();
        if (index==0) {
            btn.setText("当前主机");
        }  else {
            btn.setText("结点" + index);
        }
        btn.setMinSize(100 * weight,100 * weight);
        btn.setStyle("-fx-background-color: gray");
    }

    public double getDirec() {
        return direc;
    }

    public void setDirec(double direc) {
        this.direc = direc;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Button getBtn() {
        return btn;
    }
}

public class Controller {
    List<Node> selectedNodes = new ArrayList<>();
    Node ownNode= new Node(1,0);
    List<Node> allNodes = new ArrayList<>();

    @FXML protected void handleLoginBtn(ActionEvent event) {
        login();
    }

    @FXML protected void handleSelectCode(ActionEvent event) {
        FileChooser fileChooser  = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All files (*.*)",
                "*.*");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(Main.priStg);
    }

    @FXML private Pane center_image;

    @FXML protected void handleSelectSlaver(ActionEvent event) {
        center_image.getChildren().clear();
        selectedNodes.clear();
        allNodes.clear();
        Button ownBtn = ownNode.getBtn();
        ownBtn.setLayoutX(center_image.getWidth()*0.5);
        ownBtn.setLayoutY(center_image.getHeight()*0.5);
        ownBtn.setStyle("-fx-background-color: #3effa0");
        //初始化
        int nodeNum = 12;
        double meanDigree = 2 * Math.PI / nodeNum;
        for (int i = 0; i < nodeNum; i ++) {
            Node curNode = new Node(Math.random(), i + 1);
            curNode.setDirec(center_image.getHeight() / 6 + center_image.getHeight() / 3 * Math.random());
            Button curBtn = curNode.getBtn();
            curBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    selectedNodes.add(curNode);
                    curBtn.setStyle("-fx-background-color: #ff5758");
                }
            });
            curBtn.setLayoutX(ownBtn.getLayoutX() + curNode.getDirec()*Math.cos(i * meanDigree));
            curBtn.setLayoutY(ownBtn.getLayoutY() + curNode.getDirec()*Math.sin(i * meanDigree));
            allNodes.add(curNode);
            center_image.getChildren().add(curBtn);
        }
        center_image.getChildren().add(ownBtn);
    }
    @FXML protected void handlePowerSlaverAction(ActionEvent event) {
        center_image.getChildren().clear();
        Button ownBtn = ownNode.getBtn();
        ownBtn.setLayoutX(center_image.getWidth()*0.5);
        ownBtn.setLayoutY(center_image.getHeight()*0.5);
        ownBtn.setStyle("-fx-background-color: #3effa0");
        double meanDigree = 2 * Math.PI / selectedNodes.size();
        int index = 0;
        for (Node curNode:selectedNodes) {
            Button curBtn = curNode.getBtn();
            curBtn.setLayoutX(ownBtn.getLayoutX() + curNode.getDirec()*Math.cos(index * meanDigree));
            curBtn.setLayoutY(ownBtn.getLayoutY() + curNode.getDirec()*Math.sin(index * meanDigree));
            index++;
            Line line = new Line(ownBtn.getLayoutX()+ownBtn.getMinWidth()*0.5,ownBtn.getLayoutY()+ownBtn.getMinHeight()*0.5,curBtn.getLayoutX()+curBtn.getMinWidth()*0.5,curBtn.getLayoutY()+curBtn.getMinHeight()*0.5);
            Circle circle = new Circle(0,0,10,Color.BLUE);

            Path curPath = new Path();
            curPath.getElements().add(new MoveTo(ownBtn.getLayoutX()+ownBtn.getWidth()*0.5,ownBtn.getLayoutY()+ownBtn.getHeight()*0.5));
            curPath.getElements().add(new CubicCurveTo(0.5*(line.getStartX()+line.getEndX()),
                    0.5*(line.getEndY()+line.getStartY()), 0.5*(line.getStartX()+line.getEndX()),
                    0.5*(line.getEndY()+line.getStartY()),  line.getEndX(), line.getEndY()));

            PathTransition curPathTransition = new PathTransition();
            curPathTransition.setDuration(Duration.millis(curNode.getDirec() / (center_image.getWidth() / 2) * 5000));
            curPathTransition.setPath(curPath);
            curPathTransition.setNode(circle);
            curPathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            curPathTransition.setCycleCount(Timeline.INDEFINITE);
            curPathTransition.setAutoReverse(false);
            curPathTransition.play();
            center_image.getChildren().addAll(line, curBtn, circle);
        }
        center_image.getChildren().add(ownBtn);
    }
    @FXML private TextArea log;

    private void login() {
        Stage loginStage = new Stage();
        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20,20,20,20));
        loginPane.setHgap(25);
        loginPane.setVgap(25);
        loginPane.setAlignment(Pos.CENTER);

        Label higherCloud = new Label("天算计划");
        higherCloud.setId("higher_cloud");
        loginPane.add(higherCloud,0,0,3,1);

        Label userNameLabel = new Label("用户名");
        Label pswLabel = new Label("密码");
        TextField userName = new TextField();
        userName.setPrefWidth(200);
        PasswordField psw = new PasswordField();
        psw.setPrefWidth(200);
        loginPane.add(userNameLabel,0,2);
        loginPane.add(pswLabel,0,3);
        loginPane.add(userName,1,2,3,1);
        loginPane.add(psw,1,3,3,1);

        Button submitBtn = new Button("登陆");
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(submitBtn);
        loginPane.add(hBox,1,4,3,1);
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread() {
                    //这里去发送登陆命令
                }.start();
                loginStage.close();
            }
        });
        Scene scene =new Scene(loginPane,800,500);
        scene.getStylesheets().add(Controller.class.getResource("Login.css").toExternalForm());
        loginStage.setScene(scene);
        loginStage.show();
    }


}
