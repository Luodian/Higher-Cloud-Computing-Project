package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    public static Stage priStg;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 1920,1080);
        primaryStage.setTitle("welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
        priStg = primaryStage;
//        primaryStage.setTitle("Transitions and Timeline Animation");
//        final Scene scene = new Scene(new Group(), 1920, 1080);
//        scene.setFill(Color.WHITE);

//
//        final Rectangle rectPath = new Rectangle (0, 0, 40, 40);
//        rectPath.setArcHeight(10);
//        rectPath.setArcWidth(10);
//        rectPath.setFill(Color.ORANGE);
//
//        final Timeline timeline = new Timeline();
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
//
//        Path path = new Path();
//        path.getElements().add(new MoveTo(20,20));
////        path.getElements().add(new CubicCurveTo(1800, 0, 1800, 120, 1000, 920));
//        path.getElements().add(new CubicCurveTo(0, 120, 0, 240, 680, 1040));
//
//        PathTransition pathTransition = new PathTransition();
//        pathTransition.setDuration(Duration.millis(4000));
//        pathTransition.setPath(path);
//        pathTransition.setNode(rectPath);
//        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
//        pathTransition.setCycleCount(Timeline.INDEFINITE);
//        pathTransition.setAutoReverse(true);
//        pathTransition.play();
//

//        Rectangle ownNode = new Rectangle(scene.getWidth()*0.5,scene.getHeight()*0.5, 40, 40);
//        ownNode.setFill(Color.FIREBRICK);
//        ownNode.setArcHeight(10);
//        ownNode.setArcWidth(10);
//
//        int nodeNUm = 10;
//        List<Double> nodeDirec = new ArrayList<>();
//        double meanDigree = 2 * Math.PI / nodeNUm;
//
//        for (int i = 0; i < nodeNUm; i ++) {
//            nodeDirec.add(500 * Math.random());
//            Rectangle curRect = new Rectangle(ownNode.getX() + nodeDirec.get(i)*Math.cos(i * meanDigree), ownNode.getY() + nodeDirec.get(i)*Math.sin(i * meanDigree),30,30);
//            curRect.setArcWidth(10);
//            curRect.setArcHeight(10);
//            Line line = new Line(ownNode.getX()+ownNode.getWidth()*0.5,ownNode.getY()+ownNode.getHeight()*0.5,curRect.getX()+curRect.getWidth()*0.5,curRect.getY()+curRect.getHeight()*0.5);
//            Circle circle = new Circle(0,0,10,Color.BLUE);
//
//            Path curPath = new Path();
//            curPath.getElements().add(new MoveTo(ownNode.getX()+ownNode.getWidth()*0.5,ownNode.getY()+ownNode.getHeight()*0.5));
//            curPath.getElements().add(new CubicCurveTo(0.5*(line.getStartX()+line.getEndX()),
//                    0.5*(line.getEndY()+line.getStartY()), 0.5*(line.getStartX()+line.getEndX()),
//                    0.5*(line.getEndY()+line.getStartY()),  line.getEndX(), line.getEndY()));
//
//            PathTransition curPathTransition = new PathTransition();
//            curPathTransition.setDuration(Duration.millis(4000));
//            curPathTransition.setPath(curPath);
//            curPathTransition.setNode(circle);
//            curPathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
//            curPathTransition.setCycleCount(Timeline.INDEFINITE);
//            curPathTransition.setAutoReverse(true);
//            curPathTransition.play();
//
////            ((Group)scene.getRoot()).getChildren().addAll(line,curRect,circle);
//
//        }
//        ((Group)scene.getRoot()).getChildren().addAll(ownNode);
//        Scene stageScene = new Scene(new Group(),1920,1080);
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);

    }

}