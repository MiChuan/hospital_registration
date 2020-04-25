package application;
	
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.IIOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	
	static Stage primaryStage = null;
    private SplitPane root_reg = null;
    private SplitPane root_login = null;
    private SplitPane root_doc = null;
    private static Scene scene_reg = null;
    private static Scene scene_login = null;
    private static Scene scene_doc = null;
    static String pat_doc_num;//记录登录账号
	@Override
	public void start(Stage primaryStage)  throws ClassNotFoundException, SQLException {
		try {
			primaryStage.setTitle("门诊挂号系统");
        	this.primaryStage = primaryStage;
        	//登录、挂号、医生界面描述
        	root_reg = FXMLLoader.load(getClass().getResource("view/register.fxml"));
        	root_login = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        	root_doc = FXMLLoader.load(getClass().getResource("view/doctor.fxml"));
            scene_reg = new Scene(root_reg);
            scene_login = new Scene(root_login);
            scene_doc = new Scene(root_doc);
            //显示登录界面
            setLoginUi();
            this.primaryStage.show();
            //注册mysql驱动到DriverManager
	        Class.forName(JDBC_DRIVER);
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public void setPrimaryStage(Stage stage){
    	primaryStage = stage;
    }
	
	//登录界面前置
    public static void setLoginUi(){
        primaryStage.setScene(scene_login);
    }
    //挂号界面前置
    public static void setRegUi(){
    	primaryStage.setScene(scene_reg);
    }
    //医生信息界面前置
    public static void setDocUi(){
    	primaryStage.setScene(scene_doc);
    }
}
