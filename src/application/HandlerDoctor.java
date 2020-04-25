package application;
import application.model.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class HandlerDoctor implements Initializable
{
    static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_data?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    static final String USER = "root";
    static final String PASSWORD = "512013";
    static Connection connection = null;
    static Statement statement = null,statement2=null;

    ObservableList<PatientForList> pat_list = FXCollections.observableArrayList();
    ObservableList<Income> income_list = FXCollections.observableArrayList();
    
    @FXML
    TableView<PatientForList> table_reg;
    @FXML
    TableView<Income> table_income;
    @FXML
    private Button btn_logout,btn_exit;
    @FXML
    private TableColumn<?, ?> col_regnum,col_patname,col_regtime,col_regtype
    	,col_officename,col_docnum,col_docname,col_regtype2,col_regcount,col_income,col_valid;
    @FXML
    TextField text_begin,text_end;
    @FXML
    DatePicker date_end,date_begin;

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
    	date_begin.setValue(LocalDate.now());
    	date_end.setValue(LocalDate.now());;
    	col_regnum.setCellValueFactory(new PropertyValueFactory<>("regNum"));  
    	col_patname.setCellValueFactory(new PropertyValueFactory<>("patName"));  
    	col_regtime.setCellValueFactory(new PropertyValueFactory<>("regTime")); 
    	col_regtype.setCellValueFactory(new PropertyValueFactory<>("regType"));
    	col_valid.setCellValueFactory(new PropertyValueFactory<>("valid"));
    	
    	col_officename.setCellValueFactory(new PropertyValueFactory<>("officeName"));  
    	col_docnum.setCellValueFactory(new PropertyValueFactory<>("docNum"));  
    	col_docname.setCellValueFactory(new PropertyValueFactory<>("docName")); 
    	col_regtype2.setCellValueFactory(new PropertyValueFactory<>("regType"));
    	col_regcount.setCellValueFactory(new PropertyValueFactory<>("regCount"));  
    	col_income.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
    }
    
    @FXML
    private void on_mouse_entered(Event event)
    {
    	try
		{
    		String time_begin,time_end;
    		LocalDate datetmp = date_begin.getValue();
    		LocalDate datetmp2 = date_end.getValue();
    		if(datetmp==null||datetmp2==null)
    		{
    			time_begin = LocalDate.now().toString();
    			time_end = LocalDate.now().toString();
    		}
    		else
    		{
    			time_begin = datetmp.toString();
    			time_end = datetmp2.toString();
    		}
    		time_begin += " 00:00:00";
    		time_end += " 23:59:59";
    		pat_list.clear();
    		income_list.clear();
  	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
  	         statement = connection.createStatement();
  	         statement2 = connection.createStatement();
  	         //查询对应医生的挂号信息
   	         String doc_num=Main.pat_doc_num;
   	         String sql = "select GHBH,HZBH,BRBH,"
   	         		+ "RQSJ,THBZ from t_ghxx where "
   	         		+ "YSBH= '"+doc_num+"'";
   	         ResultSet rs = statement.executeQuery(sql);
   	         String register_num=null,registration_num=null,pat_num=null;
   	         String reg_datetime=null,pat_name=null,expertstr=null;
   	         String office_name = null,doc_name=null,unreg_str=null;
   	         boolean expert=true,unreg=false;
   	         ResultSet rs2 =null;
   	         int regcount = 0;
   	         double totalcost = 0;;
   	         while(rs.next())
   	         {
   	        	register_num = rs.getString("GHBH");
   	        	registration_num = rs.getString("HZBH");
   	        	pat_num = rs.getString("BRBH");
   	        	reg_datetime = rs.getString("RQSJ");
   	        	unreg = rs.getBoolean("THBZ");
   	        	unreg_str = unreg?"否":"是";//退号标志
   	        	//查询当前号种是否专家号
   	        	sql = "select SFZJ from t_hzxx where "
   	        			+ "HZBH= '"+registration_num+"'";
   	        	rs2 = statement2.executeQuery(sql);
   	        	if(rs2.next())
   	        	{
   	        		expert = rs2.getBoolean("SFZJ");
   	        	}
   	        	//查询病人名称
   	        	sql = "select BRMC from t_brxx where"
   	        			+ " BRBH= '"+pat_num+"'";
   	        	rs2 = statement2.executeQuery(sql);
   	        	if(rs2.next())
   	        	{
   	        		pat_name = rs2.getString("BRMC");
   	        	}
   	        	expertstr = expert? "专家号":"普通号";
   	        	pat_list.add(new PatientForList(register_num,pat_name,
   	        			reg_datetime,expertstr,unreg_str));
   	         }
   	         
   	         sql = "select t_ksxx.KSMC,t_ghxx.YSBH,"
   	         		+ "t_ksys.YSMC,t_hzxx.SFZJ,"
   	         		+ "count(t_ghxx.YSBH),sum(t_ghxx.GHFY) "
   	         		+ "from t_ghxx,t_hzxx,t_ksys,t_ksxx "
   	         		+ "where "
   	         		+ "t_ghxx.HZBH=t_hzxx.HZBH " 
   	         		+ "and t_ksys.YSBH=t_ghxx.YSBH " 
   	         		+ "and t_ksxx.KSBH=t_hzxx.KSBH "
   	         		+ "and t_ghxx.RQSJ>='"+time_begin+"' and "
   	         		+ "t_ghxx.RQSJ<='"+time_end+"'" 
   	         		+ "group by t_ghxx.YSBH,t_hzxx.SFZJ";
   	         rs = statement.executeQuery(sql);
   	         while(rs.next())
   	         {
   	        	office_name = rs.getString("t_ksxx.KSMC");
   	        	doc_num = rs.getString("t_ghxx.YSBH");
   	        	doc_name = rs.getString("t_ksys.YSMC");
   	        	
   	        	expert = rs.getBoolean("t_hzxx.SFZJ");
   	        	regcount = rs.getInt("count(t_ghxx.YSBH)");
   	        	totalcost = rs.getDouble("sum(t_ghxx.GHFY)");
   	        	expertstr = expert? "专家号":"普通号";
   	        	income_list.add(new Income(office_name, doc_num,doc_name,
   	        			expertstr,Integer.toString(regcount),Double.toString(totalcost)));
   	         }
   	         if(rs!=null)
   	        	 rs.close();
   	         if(rs2!=null)
   	        	 rs2.close();
   	         statement2.close();
   	         statement.close();
   	         connection.close();
		}
    	catch(SQLException se){
	         	se.printStackTrace();
	    }
    	finally
    	{
    		table_reg.setItems(pat_list);
    		table_income.setItems(income_list);
    	}
    }
    
    @FXML
    private void on_btn_exit_clicked(ActionEvent event)
    {
    	Event.fireEvent(Main.getPrimaryStage(),
    		new WindowEvent(Main.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
    
    @FXML
    private void on_btn_logout_clicked(ActionEvent event)
    {
    	Main.setLoginUi();
    }
}