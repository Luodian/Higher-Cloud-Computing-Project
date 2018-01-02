package sample.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Control.Controller;

import java.io.IOException;

public class Main extends Application {
	
	public static Stage priStg;

	public static void main(String[] args) {

		launch(args);
//		try {
//			String rs = (String) ServerUtil.get_machine_info_with_task(String.valueOf(2));
//			System.out.println(rs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println (SystemPreferences.fetchHostName ());
//		System.out.println (SystemPreferences.fetchGPUInfo ());
//		System.out.println (SystemPreferences.GetProcessorNum ());
//		System.out.println (SystemPreferences.AssesBenchMark (SystemPreferences.GetProcessorNum (), "BigCat"));
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
//		System.out.println(ClassPath.getClassPath());
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("sample.fxml"));
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
		Parent root = fxmlLoader.load();
		Screen screen = Screen.getPrimary ();

//		初始化
		Controller controller = fxmlLoader.getController();
		controller.init();

		Rectangle2D bounds = screen.getVisualBounds ();

		double minX = bounds.getMinX ();
		double minY = bounds.getMinY ();
		double maxX = bounds.getMaxX ();
		double maxY = bounds.getMaxY ();

		Scene scene = new Scene(root, (maxX - minX) * 0.6, (maxY - minY) * 0.6);

		primaryStage.setTitle ("Higher Compute Platform");
		primaryStage.setScene (scene);
		primaryStage.show ();
		priStg = primaryStage;

//        primaryStage.setTitle("Transitions and Timeline Animation");
//        final Scene scene = new Scene(new Group(), (maxX - minX) * 0.6, (maxY - minY) * 0.6);
//        scene.setFill(Color.WHITE);

//
//        final Rectangle rectPath = new Rectangle(0, 0, 40, 40);
//        rectPath.setArcHeight(10);
//        rectPath.setArcWidth(10);
//        rectPath.setFill(Color.ORANGE);
//
//        final Timeline timeline = new Timeline();
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();

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
//
//        Rectangle ownNode = new Rectangle(scene.getWidth()*0.5,scene.getHeight()*0.5, 40, 40);
//        ownNode.setFill(Color.FIREBRICK);
//        ownNode.setArcHeight(10);
//        ownNode.setArcWidth(10);

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
////        Scene stageScene = new Scene(new Group(),(maxX - minX) * 0.6, (maxY - minY) * 0.6);
//        primaryStage.setScene(scene);
//        primaryStage.show();
	}
	
}