package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

public class HandlerLogin implements Initializable {
    // JDBC URL UserName Password
    static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_data?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    static final String USER = "root";
    static final String PASSWORD = "512013";
    static Connection connection = null;
    static Statement statement = null;
    
    ObservableList<String> ob_pat = FXCollections.observableArrayList();
    ObservableList<String> ob_doc = FXCollections.observableArrayList();
    Vector<String> pat_list,doc_list;
    
    @FXML
    private Button btn_exit,btn_login;
    @FXML
    private AnchorPane anchorpane_down;
    @FXML
    public ComboBox<String> combo_account,combo_type;
    @FXML
    private TextField text_pass;

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
    	pat_list = new Vector<>();
    	doc_list = new Vector<>();
    	combo_type.getItems().addAll("患者","医生");//设置combobox的所有选项
    	combo_type.getSelectionModel().select(0);//设置选择哪一个选项，或者说默认选项
		try
		{
  	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
  	         statement = connection.createStatement();
  	         //查询患者编号 患者姓名
   	         String sql = "select BRBH,BRMC from t_brxx";
   	         ResultSet rs = statement.executeQuery(sql);
   	         String pat_num,pat_name;
   	         while(rs.next())
   	         {
   	        	 pat_num = rs.getString("BRBH");
   	             pat_name  = rs.getString("BRMC");
   	             ob_pat.add(pat_name);//按顺序的患者姓名
   	             pat_list.add(pat_num);//按顺序的患者编号
   	         }
   	         //查询医生编号 医生名称
   	         sql = "select YSBH,YSMC from t_ksys";
   	         rs = statement.executeQuery(sql);
   	         String doc_name,doc_num;
   	         while(rs.next())
   	         {
   	        	 doc_num = rs.getString("YSBH");
   	        	 doc_name = rs.getString("YSMC");
   	        	 ob_doc.add(doc_name);//按顺序的医生姓名
   	        	 doc_list.add(doc_num);//按顺序的医生编号
   	         }
   	         rs.close();
   	         statement.close();
   	         connection.close();
		}
    	catch(SQLException se){
	         	se.printStackTrace();
	    }
		combo_account.setItems(ob_pat);//默认选择患者
		combo_account.getSelectionModel().select(0);
		//类型上设置一个监听器，当选择的登录类型发生变化就需要改变combobox的内容
		combo_type.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)-> 
		{
        	int sel = combo_type.getSelectionModel().getSelectedIndex();
        	if(sel==0)
        	{
        		combo_account.setItems(ob_pat);
        	}
        	else if(sel==1)
        	{
        		combo_account.setItems(ob_doc);
        	}
        	combo_account.getSelectionModel().select(0);
	    });
    }

    @FXML
    private void on_btn_exit_clicked(ActionEvent event) throws SQLException
    {
    	Event.fireEvent(Main.getPrimaryStage(),
    			new WindowEvent(Main.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
    
    @FXML
    private void on_btn_login_clicked(ActionEvent event)//按下登录
    {
    	if(combo_account.getValue() != null &&
            false == combo_account.getValue().toString().isEmpty())//账号有输入
    	{
        	int type = combo_type.getSelectionModel().getSelectedIndex();//类型
    		String pass = text_pass.getText();//密码
    		int sel_index = combo_account.getSelectionModel().getSelectedIndex();
    		String pat_doc_num = type==0?pat_list.elementAt(sel_index):doc_list.elementAt(sel_index);
    		if(pass.isEmpty())//没输入密码
    		{
    			JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
              			"请输入密码", "警告", JOptionPane.WARNING_MESSAGE);
    			return;
    		}
    		try
    		{
      	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
      	         statement = connection.createStatement();
       	         String sql = null;
       	         ResultSet rs = null;
       	         if(type==0)
       	         {//读取患者登录口令
       	        	sql = "select DLKL from t_brxx "
           	         		+ "where BRBH = '"+pat_doc_num+"'";
       	         }
       	         else if(type==1)
       	         {//读取医生登录口令
       	        	sql = "select DLKL from t_ksys "
           	         		+ "where YSBH = '"+pat_doc_num+"'";
       	         }
       	         else return;
       	         rs = statement.executeQuery(sql);//查询对应账号的密码
       	         if(rs.next())//存在账号
       	         {
       	             String login_cmd  = rs.getString("DLKL");
       	             if(login_cmd.equals(pass))//密码输入正确
       	             {
       	            	Main.pat_doc_num = pat_doc_num;
       	            	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       	            	//更新登录时间
       	            	if(type==0)
       	            	 {
       	            		sql = "update t_brxx set DLRQ = "
         							+ "'"+df.format(new Date())+"' where BRBH= '"+pat_doc_num+"'";
       	            	 }
       	            	 else if(type==1)
       	            	 {
       	            		sql = "update t_ksys set DLRQ = "
         							+ "'"+df.format(new Date())+"' where YSBH= '"+pat_doc_num+"'";
       	            	 }
       	            	 else return;
       	            	statement.executeUpdate(sql);
     					text_pass.clear();
     					System.out.println("登录成功");
     					//显示挂号界面
     					if(type==0)
     					{
     						Main.setRegUi();
     					}
     					//显示医生信息界面
     					else if(type==1)
     					{
     						Main.setDocUi();
     					}
       	             }
     				 else
     				 {
     					JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
     	              			"账号不存在或密码错误", "警告", JOptionPane.WARNING_MESSAGE);
     				 }
       	         }
       	         rs.close();
       	         statement.close();
       	         connection.close();
    		}
        	catch(SQLException se){
   	         	se.printStackTrace();
   	     	}
    	}
    }
}
