package sample.Control;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.BackEnd.ServerUtils.ServerUtil;
import sample.View.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Node {
	private double direc;
	private double weight;  //范围是0-1
	private Button btn;
	
	public Node (double weight, int index) {
		this.weight = weight;
		btn = new Button ();
		if (index == 0) {
			btn.setText ("当前主机");
		} else {
			btn.setText ("结点" + index);
		}
		btn.setMinSize (100 * weight, 100 * weight);
		btn.setStyle ("-fx-background-color: gray");
	}
	
	public double getDirec () {
		return direc;
	}
	
	public void setDirec (double direc) {
		this.direc = direc;
	}
	
	public double getWeight () {
		return weight;
	}
	
	public void setWeight (double weight) {
		this.weight = weight;
	}
	
	public Button getBtn () {
		return btn;
	}
}

public class Controller {
	List<Node> selectedNodes = new ArrayList<> ();
	Node ownNode = new Node (1, 0);
	List<Node> allNodes = new ArrayList<> ();
	@FXML
	private Pane center_image;

	@FXML
	private Button portrait_btn;
	@FXML
	private TextArea log;
	@FXML
	private Button login_btn;

	//	@FXML
//	private ImageView center_image_view;
	private void alertWhenNotLogin() {
		final Alert alert = new Alert(Alert.AlertType.WARNING, "请先登陆", new ButtonType("确定", ButtonBar.ButtonData.YES));
		alert.setTitle("请先登陆");
		alert.showAndWait();
	}

	@FXML
	protected void handleSettings(ActionEvent event) {
		if (login_btn.getText().equals("下线")) {
			final Stage settingsStage = new Stage();
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

	protected void handlePortrait(ActionEvent event) {
		if (login_btn.getText().equals("下线")) {
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

			Scene scene = new Scene(infoPane, 400, 500);
			scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
			infoStage.setScene(scene);
			infoStage.show();
		} else {
			alertWhenNotLogin();
		}
	}

	@FXML
	protected void handleLoginBtn (ActionEvent event) {
		if (login_btn.getText().equals("下线")) {
			init();
		} else {
			login();
		}

	}
	
	@FXML
	protected void handleSelectCode (ActionEvent event) {
		if (login_btn.getText().equals("下线")) {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All files (*.*)",
					"*.*");
			fileChooser.getExtensionFilters().add(filter);
			File file = fileChooser.showOpenDialog(Main.priStg);
            String path = file.toString();
            System.out.println("path:" + path);
            try {
                String rs = ServerUtil.ImgUpload(26, path);
                System.out.println(rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
		} else {
			alertWhenNotLogin();
		}
	}

	private void afterSuccessLogin() {
//		个人信息部分的更改
		ImageView portImg = new ImageView(new Image(Main.class.getResourceAsStream("Resources/wdhpor.png")));
//		宽和高相等
//		portImg.setFitWidth(portrait_btn.getWidth());
//		portImg.setFitHeight(portrait_btn.getWidth());
//		portrait_btn.setMouseTransparent(true);
		portImg.setFitWidth(login_btn.getWidth());
		portImg.setFitHeight(login_btn.getWidth());
		portrait_btn.setGraphic(portImg);
		portrait_btn.setPadding(new Insets(0, 0, 0, 0));
		login_btn.setText("下线");

//		服务器图片的更改
		center_image.getChildren ().clear ();
		selectedNodes.clear ();
		allNodes.clear ();
		Button ownBtn = ownNode.getBtn ();
		ownBtn.setLayoutX (center_image.getWidth () * 0.5);
		ownBtn.setLayoutY (center_image.getHeight () * 0.5);
		ownBtn.setStyle ("-fx-background-color: #3effa0");
		//初始化
		int nodeNum = 12;
		double meanDigree = 2 * Math.PI / nodeNum;
		for (int i = 0; i < nodeNum; i++) {
			final Node curNode = new Node (Math.random (), i + 1);
			curNode.setDirec (center_image.getHeight () / 6 + center_image.getHeight () / 3 * Math.random ());
			final Button curBtn = curNode.getBtn ();
			curBtn.setOnAction (new EventHandler<ActionEvent> () {
				@Override
				public void handle (ActionEvent event) {
					selectedNodes.add (curNode);
					curBtn.setStyle ("-fx-background-color: #ff5758");
				}
			});
			curBtn.setLayoutX (ownBtn.getLayoutX () + curNode.getDirec () * Math.cos (i * meanDigree));
			curBtn.setLayoutY (ownBtn.getLayoutY () + curNode.getDirec () * Math.sin (i * meanDigree));
			allNodes.add (curNode);
			center_image.getChildren ().add (curBtn);
		}
		center_image.getChildren ().add (ownBtn);
	}

//	@FXML
//	protected void handleSelectSlaver (ActionEvent event) {
//
//	}
	
	@FXML
	protected void handlePowerSlaverAction (ActionEvent event) {
		if (login_btn.getText().equals("下线")) {
			center_image.getChildren().clear();
			Button ownBtn = ownNode.getBtn();
			ownBtn.setLayoutX(center_image.getWidth() * 0.5);
			ownBtn.setLayoutY(center_image.getHeight() * 0.5);
			ownBtn.setStyle("-fx-background-color: #3effa0");
			double meanDigree = 2 * Math.PI / selectedNodes.size();
			int index = 0;
			for (Node curNode : selectedNodes) {
				Button curBtn = curNode.getBtn();
				curBtn.setLayoutX(ownBtn.getLayoutX() + curNode.getDirec() * Math.cos(index * meanDigree));
				curBtn.setLayoutY(ownBtn.getLayoutY() + curNode.getDirec() * Math.sin(index * meanDigree));
				index++;
				Line line = new Line(ownBtn.getLayoutX() + ownBtn.getMinWidth() * 0.5, ownBtn.getLayoutY() + ownBtn.getMinHeight() * 0.5, curBtn.getLayoutX() + curBtn.getMinWidth() * 0.5, curBtn.getLayoutY() + curBtn.getMinHeight() * 0.5);
				Circle circle = new Circle(0, 0, 10, Color.BLUE);

				Path curPath = new Path();
				curPath.getElements().add(new MoveTo(ownBtn.getLayoutX() + ownBtn.getWidth() * 0.5, ownBtn.getLayoutY() + ownBtn.getHeight() * 0.5));
				curPath.getElements().add(new CubicCurveTo(0.5 * (line.getStartX() + line.getEndX()),
						0.5 * (line.getEndY() + line.getStartY()), 0.5 * (line.getStartX() + line.getEndX()),
						0.5 * (line.getEndY() + line.getStartY()), line.getEndX(), line.getEndY()));

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
		} else {
			alertWhenNotLogin();
		}
	}
	
	private void login () {
		final Stage loginStage = new Stage ();
		GridPane loginPane = new GridPane ();
		loginPane.setPadding (new Insets (20, 20, 20, 20));
		loginPane.setHgap (25);
		loginPane.setVgap (25);
		loginPane.setAlignment (Pos.CENTER);
		
		Label higherCloud = new Label ("天算计划");
		higherCloud.setId ("higher_cloud");
		loginPane.add (higherCloud, 0, 0, 3, 1);

		Label userNameLabel = new Label("帐号");
		Label pswLabel = new Label ("密码");
		TextField userName = new TextField ();
		userName.setPrefWidth (200);
		PasswordField psw = new PasswordField ();
		psw.setPrefWidth (200);
		loginPane.add (userNameLabel, 0, 2);
		loginPane.add (pswLabel, 0, 3);
		loginPane.add (userName, 1, 2, 3, 1);
		loginPane.add (psw, 1, 3, 3, 1);

		Button regiBtn = new Button("注册");
		Button submitBtn = new Button ("登陆");
		HBox hBox = new HBox ();
		hBox.setAlignment(Pos.BOTTOM_CENTER);
		hBox.setSpacing(150);
		hBox.getChildren().addAll(regiBtn, submitBtn);
		loginPane.add (hBox, 1, 4, 3, 1);
		regiBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				regi();

			}
		});
		submitBtn.setOnAction (new EventHandler<ActionEvent> () {
			@Override
			public void handle (ActionEvent event) {
				new Thread () {
					@Override
					public void run() {
						//这里去发送登陆命令
					}
				}.start ();
				loginStage.close ();
				afterSuccessLogin();
			}
		});
		Scene scene = new Scene (loginPane, 800, 500);
		scene.getStylesheets().add(Main.class.getResource("Login.css").toExternalForm());
		loginStage.setScene(scene);
		loginStage.show();
	}

	private void regi() {
		final Stage loginStage = new Stage();
		GridPane loginPane = new GridPane();
		loginPane.setPadding(new Insets(20, 20, 20, 20));
		loginPane.setHgap(25);
		loginPane.setVgap(25);
		loginPane.setAlignment(Pos.CENTER);

		Label regiTitle = new Label("注册");
		regiTitle.setId("regi_title");
		loginPane.add(regiTitle, 0, 0, 3, 1);

		Label userNameLabel = new Label("用户名");
		TextField userName = new TextField();
		userName.setPrefWidth(200);

		Label emailLabel = new Label("邮箱");
        final TextField email = new TextField();
		emailLabel.setPrefWidth(200);

		Button sendValiCode = new Button("发送验证码");


		Label pswLabel = new Label("密码");
		PasswordField psw = new PasswordField();
		psw.setPrefWidth(200);
		Label ensurePswLabel = new Label("确认密码");
		PasswordField ensurePsw = new PasswordField();
		ensurePsw.setPrefWidth(200);

		Label valiCodeLabel = new Label("验证码");
		TextField valiCode = new TextField();
		valiCode.setPrefWidth(200);

		Button ensureRegiBtn = new Button("确认信息并注册");

//		注册之后的返回消息，默认不可见，若是注册失败，就会显示出来
		final Label regiRs = new Label();
		regiRs.setId("regi_rs");
		regiRs.setVisible(false);

        sendValiCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//				命令服务器向所填地址发送验证码
                try {
                    String veriEmail = ServerUtil.check_email_exist(email.getText());
                    if (veriEmail.equals("SUCCESS")) {
//						说明已经被注册了
                        regiRs.setText("该邮箱已被使用");
                        regiRs.setVisible(true);
                    } else if (veriEmail.equals("FAIL")) {

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
//				String



				regiRs.setText("注册失败");
				regiRs.setVisible(true);
			}
		});

		loginPane.add(userNameLabel, 0, 2);
		loginPane.add(userName, 1, 2, 3, 1);

		loginPane.add(emailLabel, 0, 3);
		loginPane.add(email, 1, 3, 3, 1);
		loginPane.add(sendValiCode, 4, 3);

		loginPane.add(pswLabel, 0, 4);
		loginPane.add(psw, 1, 4, 3, 1);

		loginPane.add(ensurePswLabel, 0, 5);
		loginPane.add(ensurePsw, 1, 5, 3, 1);

		loginPane.add(valiCodeLabel, 0, 6);
		loginPane.add(valiCode, 1, 6, 3, 1);

		HBox hBox = new HBox();
		hBox.setAlignment(Pos.BOTTOM_RIGHT);
		hBox.getChildren().add(ensureRegiBtn);

		loginPane.add(hBox, 1, 8, 3, 1);

        loginPane.add(regiRs, 4, 8, 3, 1);
		Scene scene = new Scene(loginPane, 800, 500);
		scene.getStylesheets().add(Main.class.getResource("Regi.css").toExternalForm());
		loginStage.setScene (scene);
		loginStage.show ();
	}

	public void init() {
//		初始化中心图片
		center_image.getChildren().clear();
		ImageView centerImageView = new ImageView();
		centerImageView.setPickOnBounds(true);
		centerImageView.setPreserveRatio(true);
		centerImageView.setImage(new Image(Main.class.getResourceAsStream("Resources/46.jpg")));
		center_image.getChildren().add(centerImageView);

//		初始化登陆按钮和头像信息
		login_btn.setText("登陆");
		ImageView portImg = new ImageView(new Image(Main.class.getResourceAsStream("Resources/dfpor.png")));
		portImg.setFitWidth(130);
		portImg.setFitHeight(110);
		portrait_btn.setPadding(new Insets(0, 0, 0, 0));
		portrait_btn.setGraphic(portImg);
//		portrait_btn.setMouseTransparent(true);
		portrait_btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("在之力");
				handlePortrait(event);
			}
		});
	}

}
