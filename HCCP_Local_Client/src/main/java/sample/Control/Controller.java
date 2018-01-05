package sample.Control;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.BackEnd.ServerUtils.ServerUtil;
import sample.View.Main;

import java.text.SimpleDateFormat;
import java.util.*;

class Node {
	private double direc;
	private double weight;  //范围是0-1
	private Button btn;
	private String msg;
	
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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
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

	private boolean online = false;

	private int user_id = -1;
	private String user_email;

	private List<Node> selectedNodes = new ArrayList<>();
	private List<Node> myNodes = new ArrayList<>();
	private List<Node> allNodes = new ArrayList<>();

	private String curAppName = "None";

	@FXML
	private HBox add_and_sub_box;
	@FXML
	private StackPane center_stack;

	@FXML
	private Pane center_image;
	@FXML
	private GridPane bottom_pane;
	@FXML
	private Label top_time;

	private Button portrait_btn;
	private Button settings_btn;

	private void alertWhenNotLogin() {
		final Alert alert = new Alert(Alert.AlertType.WARNING, "请先登陆", new ButtonType("确定", ButtonBar.ButtonData.YES));
		alert.setTitle("请先登陆");
		alert.showAndWait();
	}

	private void handleSettings(ActionEvent event) {
		if (online) {
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

			Scene scene = new Scene(infoPane, 400, 500);
			scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
			infoStage.setScene(scene);
			infoStage.show();
	}

//	@FXML
//	protected void handleLoginBtn (ActionEvent event) {
//		if (online) {
//			stopTimeCnt = true;
//			init();
//		} else {
//			login();
//		}
//
//	}

//	@FXML
//	protected void handleSelectCode (ActionEvent event) {
//		if (online) {
//			FileChooser fileChooser = new FileChooser();
//			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All files (*.*)",
//					"*.*");
//			fileChooser.getExtensionFilters().add(filter);
//			File file = fileChooser.showOpenDialog(Main.priStg);
//            String path = file.toString();
//            System.out.println("path:" + path);
//            try {
//                String rs = ServerUtil.ImgUpload(26, path);
//                System.out.println(rs);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//		} else {
//			alertWhenNotLogin();
//		}
//	}

	private void afterSuccessLogin() {

		online = true;
//		个人信息部分的更改
		ImageView portImg = new ImageView(new Image(Main.class.getResourceAsStream("Resources/wdhpor.png")));
		portImg.setFitWidth(130);
		portImg.setFitHeight(110);
		portrait_btn.setGraphic(portImg);
		portrait_btn.setPadding(new Insets(0, 0, 0, 0));

//		服务器图片的更改
		center_image.getChildren ().clear ();
		selectedNodes.clear ();
		allNodes.clear ();

//		启动4个进程
//		........
//		自己进程的默认配置
		double ownWeight = 0.6;

		for (int i = 0; i < 4; i++) {
			Node node = new Node(ownWeight, i + 1);
			myNodes.add(node);
		}

//		这个Button比较大，
		Button ownBtn = new Button();
		ownBtn.setText("进程个数" + myNodes.size());
		ownBtn.setPrefWidth(100);
		ownBtn.setPrefHeight(100);
		ownBtn.setLayoutX (center_image.getWidth () * 0.5);
		ownBtn.setLayoutY (center_image.getHeight () * 0.5);
		ownBtn.setStyle ("-fx-background-color: #3effa0");

//		获取网格上的机器,得到allNodes的个数和配置信息， 配置信息设置为msg属性，ping 一下得到对方距离，设置为Direc,

		int nodeNum = 12;
//		int nodeNum = allNodes.size();
		double meanDigree = 2 * Math.PI / nodeNum;
		for (int i = 0; i < nodeNum; i++) {
			final Node curNode = new Node (Math.random (), i + 1);
//			final Node curNode = allNodes.get(i);
			curNode.setMsg("a:1\nb:23");
			curNode.setDirec (center_image.getHeight () / 6 + center_image.getHeight () / 3 * Math.random ());

			final Button curBtn = curNode.getBtn ();
			curBtn.setTooltip(new Tooltip(curNode.getMsg()));
			curBtn.setOnAction (new EventHandler<ActionEvent> () {
				@Override
				public void handle (ActionEvent event) {
//					selectedNodes.add (curNode);
					curBtn.setStyle ("-fx-background-color: #ff5758");

					final Stage infoStage = new Stage();
					GridPane infoPane = new GridPane();
					infoPane.setPadding(new Insets(20, 20, 20, 20));
					infoPane.setHgap(25);
					infoPane.setVgap(25);
					infoPane.setAlignment(Pos.CENTER);

					Label msgLabel = new Label(curNode.getMsg());
					infoPane.add(msgLabel, 1, 0, 5, 5);
					Scene scene = new Scene(infoPane, 400, 500);
					scene.getStylesheets().add(Main.class.getResource("base.css").toExternalForm());
					infoStage.setScene(scene);
					infoStage.show();

				}
			});
			curBtn.setLayoutX (ownBtn.getLayoutX () + curNode.getDirec () * Math.cos (i * meanDigree));
			curBtn.setLayoutY (ownBtn.getLayoutY () + curNode.getDirec () * Math.sin (i * meanDigree));
			allNodes.add (curNode);
			center_image.getChildren ().add (curBtn);
		}
		center_image.getChildren().add(ownBtn);

	}

	protected void handlePowerSlaverAction (ActionEvent event) {
		if (online) {
			center_image.getChildren().clear();
//			Button ownBtn = ownNode.getBtn();
			Button ownBtn = new Button();
			ownBtn.setPrefWidth(100);
			ownBtn.setPrefHeight(100);
			ownBtn.setId("own_btn");
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
		scene.getStylesheets().add(Main.class.getResource("Login.css").toExternalForm());
		loginStage.setScene(scene);
		loginStage.show();
	}

    private void regi() {
        final Stage registerStage = new Stage();
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
                            user_id = Integer.valueOf(regiMsg.substring(regiMsg.indexOf("is ") + 1));
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
        scene.getStylesheets().add(Main.class.getResource("Regi.css").toExternalForm());
        registerStage.setScene(scene);
        registerStage.show();
    }

	public void init() {
		online = false;

		curAppName = "no";

//		时间
		StackPane.setMargin(top_time, new Insets(0, 800, 0, 800));
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
						top_time.setText(df.format(new Date()));
					}
				});

			}
		}, 0, 1000);

//		底部栏

		List<App> appList = new ArrayList<>();
		App portrait = new App("个人信息", "Resources/dfpor.png");
		portrait_btn = portrait;
		portrait.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (online)
					handlePortrait(event);
				else
					login();
			}
		});

		final App downloadApp = new App("下载", "Resources/image.jpg");
		downloadApp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				curAppName = downloadApp.name;
			}
		});
		final App miner = new App("挖矿", "Resources/46.jpg");
		miner.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				curAppName = miner.name;
			}
		});
		final App settings = new App("配置", "Resources/settings.jpg");
		settings.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				handleSettings(event);
			}
		});

		appList.add(portrait);
		appList.add(downloadApp);
		appList.add(miner);
		appList.add(settings);
		for (final App app : appList) {
			bottom_pane.add(app, appList.indexOf(app), 0);
			Image srcImage = new Image(Main.class.getResourceAsStream(app.picPath));
			ImageView portImg = new ImageView(srcImage);
			Rectangle clip = new Rectangle(
					srcImage.getWidth(), srcImage.getHeight()
			);
			clip.setArcWidth(15);
			clip.setArcHeight(15);
			portImg.setClip(clip);
			portImg.setFitWidth(130);
			portImg.setFitHeight(130);
			app.setGraphic(portImg);
			app.setPadding(new Insets(0, 0, 0, 0));
			Tooltip tooltip = new Tooltip();
			tooltip.setText(app.name);
			app.setTooltip(tooltip);
			if (app.name.equals("个人信息")) {
				app.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						app.setText("You touched me!");
						System.out.println("hello");
					}
				});
			}
		}

		bottom_pane.setMaxWidth(appList.size() * 130 + 150);
		bottom_pane.setPadding(new Insets(20, 20, 20, 20));
		bottom_pane.setAlignment(Pos.BOTTOM_CENTER);
//		Rectangle clip = new Rectangle(bottom_pane.getWidth(),bottom_pane.getHeight());
//		clip.setArcWidth(30);
//		clip.setArcHeight(30);
//		bottom_pane.setClip(clip);
		bottom_pane.setEffect(new DropShadow(20, Color.BLACK));
		center_stack.setAlignment(Pos.CENTER);
		StackPane.setMargin(bottom_pane, new Insets(800, 1000, 50, 1000));

//		初始化中心图片
		center_image.getChildren().clear();
		ImageView centerImageView = new ImageView();
		centerImageView.setPickOnBounds(true);
		centerImageView.setPreserveRatio(true);
		centerImageView.setImage(new Image(Main.class.getResourceAsStream("Resources/cg.jpg")));
		centerImageView.setFitHeight(center_image.getHeight());
		centerImageView.setFitWidth(center_image.getWidth());
		center_image.getChildren().add(centerImageView);

		Button addNodeBtn = new Button("提供进程");
		addNodeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
//				这个0.9 可以设置
				double weight = 0.9;
				myNodes.add(new Node(0.9, myNodes.size() + 1));
			}
		});
		Button subNodeBtn = new Button("减少进程");
		subNodeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				myNodes.remove(myNodes.size() - 1);
			}
		});
		add_and_sub_box.setVisible(true);

		login();

//		初始化登陆按钮和头像信息
//		login_btn.setText("登陆");
//		login_btn.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				handleLoginBtn(event);
//			}
//		});


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

}
