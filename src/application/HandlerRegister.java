package application;

import application.*;
import application.Main;
import application.model.PatientReg;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class HandlerRegister implements Initializable{
	static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_data?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    static final String USER = "root";
    static final String PASSWORD = "512013";
    static Connection connection = null;
    static Statement statement = null;
    private boolean flag_of_changedbysel = false;
    private boolean flag_of_changedbyreg = false;
    private boolean flag_of_changedbyunreg = false;
    boolean flag_off_cut = false;
    String sel_off_num,sel_doc_num,sel_reg_num;

    ObservableList<String> ob_office = FXCollections.observableArrayList();
    ObservableList<String> cut_ob_office = FXCollections.observableArrayList();
    ObservableList<String> ob_doc = FXCollections.observableArrayList();
    ObservableList<String> ob_regname = FXCollections.observableArrayList();
    ObservableList<PatientReg> ob_patreg = FXCollections.observableArrayList();
    ObservableList<PatientReg> ob_unreg = FXCollections.observableArrayList();
    Vector<String> office_list,cut_of_list,doc_list,reg_list;
    
    @FXML
    private Button btn_ok,btn_clear,btn_exit;
    @FXML
    private ListView<String> list_office,list_doc,list_regtype,list_regname;
    @FXML
    private ContextMenu context_office,context_doc,context_regtype,context_regname;
    @FXML
    private TextField text_office,text_doc,text_regtype,text_regname,text_cost,
    	text_pay,text_charge,text_regnum;
    @FXML
    private TableView<PatientReg> table_patreg;
    @FXML
    private TableColumn<?, ?>col_regnum,col_regname,col_docnum,col_docname,
    col_regcount,col_regcost,col_unreg,col_regtime;
    @FXML
    private Tab tab_reg,tab_unreg;

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
    	col_regnum.setCellValueFactory(new PropertyValueFactory<>("regNum"));  
    	col_regname.setCellValueFactory(new PropertyValueFactory<>("regName"));  
    	col_docnum.setCellValueFactory(new PropertyValueFactory<>("docNum")); 
    	col_docname.setCellValueFactory(new PropertyValueFactory<>("docName"));
    	
    	col_regcount.setCellValueFactory(new PropertyValueFactory<>("regCount"));  
    	col_regcost.setCellValueFactory(new PropertyValueFactory<>("regCost"));  
    	col_unreg.setCellValueFactory(new PropertyValueFactory<>("unReg")); 
    	col_regtime.setCellValueFactory(new PropertyValueFactory<>("regTime"));
    	table_patreg.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	
	   	 try
	   	 {
	   		office_list = new Vector<>();
	   		cut_of_list = new Vector<>();
	   		doc_list = new Vector<>();
	   		reg_list = new Vector<>();
	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
	         statement = connection.createStatement();
	         //查询科室编号，科室名称
	         String sql = "select KSBH,KSMC from t_ksxx";
	         ResultSet rs = statement.executeQuery(sql);
	         String office_name,office_num;
	         while(rs.next())
	         {
	        	 office_num = rs.getString("KSBH");
	             office_name = rs.getString("KSMC");
	             ob_office.add(office_name);
	             office_list.add(office_num);
	         }
	         rs.close();
	         statement.close();
	         connection.close();
	     }catch(SQLException se){
	         se.printStackTrace();
	     }
	     
	   	list_office.setItems(ob_office);
    	list_doc.getSelectionModel().selectedItemProperty().addListener
    	(
    	    (ObservableValue<? extends String> observable, 
    	    		String oldValue, String newValue) ->
    	    {
    	    	int index = list_doc.getSelectionModel().getSelectedIndex();
    	    	if(index>-1)
    	    	{
    	    		sel_doc_num = doc_list.elementAt(index);
    	    	}
    	    	
			    text_doc.setText(newValue);
			    context_doc.hide();
    	    }
    	);
    	list_regtype.getSelectionModel().selectedItemProperty().addListener
    	(
    	    (ObservableValue<? extends String> observable, 
    	    		String oldValue, String newValue) ->
    	    {
			    text_regtype.setText(newValue);
			    context_regtype.hide();
    	    }
    	);
    	list_regname.getSelectionModel().selectedItemProperty().addListener
    	(
    	    (ObservableValue<? extends String> observable, 
    	    		String oldValue, String newValue) ->
    	    {
    	    	int index = list_regname.getSelectionModel().getSelectedIndex();
    	    	if(index>-1)
    	    	{
    	    		sel_reg_num = reg_list.elementAt(index);
    	    	}
			    text_regname.setText(newValue);
			    context_regname.hide();
    	    }
    	);
    	text_office.textProperty().addListener(new ChangeListener<String>()
    		{
    			@Override
    			public void changed(ObservableValue<? extends String> observable, 
 		                              String oldValue, String newValue)
    			{
    				if(flag_of_changedbyreg==true)
    				{
    					flag_of_changedbyreg = false;
    					return;
    				}
    				if(flag_of_changedbysel==false)
    				{
        		    	context_office.show(Main.primaryStage,
        		    			Main.primaryStage.getX()+128.0+10,
        		    			Main.primaryStage.getY()+33.0+130);
    				}
    				else
    					flag_of_changedbysel = false;
    			    Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
    			    if(newValue==null||newValue.isEmpty())
    			    {
    			    	list_office.setItems(ob_office);
    			    	flag_off_cut = false;
    			    }
    			    else if(pattern.matcher(newValue).matches())
    			    {
    		    	   	 try
    		    	   	 {
    		    	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
    		    	         statement = connection.createStatement();
    		    	         String sql = "select KSBH,KSMC from t_ksxx "
    		    	         		+ "where PYZS like '"+newValue+"%'";
    		    	         ResultSet rs = statement.executeQuery(sql);
    		    	         cut_ob_office.clear();
    		    	         cut_of_list.clear();
    		    	         String office_name,office_num;
    		    	         while(rs.next())
    		    	         {
    		    	        	 office_num = rs.getString("KSBH");
    		    	             office_name = rs.getString("KSMC");
    		    	             cut_ob_office.add(office_name);
    		    	             cut_of_list.add(office_num);
    		    	         }
    		    	         list_office.setItems(cut_ob_office);
    		    	         flag_off_cut = true;
    		    	         rs.close();
    		    	         statement.close();
    		    	         connection.close();
    		    	     }catch(SQLException se) {
    		    	         se.printStackTrace();
    		    	     }
    			    }
    			}
    		}
    	);
    }
    
    @FXML
    private void tabunreg_sel_changed(Event event)
    {
    	boolean flag_unreg=false,expert=false;
    	String regnum,regname,docnum,docname,regcount,regcost,unreg,regtime;
		try
		{
 	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
 	         statement = connection.createStatement();
  	        String sql;
  	        ResultSet rs=null;
  	        //查询当前病人挂号费用
     		sql = "select count(*) as regcount from t_ghxx " + 
 	    			"where BRBH='"+Main.pat_doc_num+"'";
     		rs = statement.executeQuery(sql);
     		if(rs.next())
     		{
     			int count = rs.getInt("regcount");
     			int col_count = ob_patreg.size();
     			if(count!=col_count || flag_of_changedbyunreg)
     			{
     				ob_patreg.clear();
     				sql = "select reg1.GHBH,reg2.HZMC,reg2.SFZJ,reg1.YSBH,"
     	 	    			+ "doc.YSMC,reg1.GHRC,reg1.THBZ,reg1.GHFY,"
     	 	    			+ "reg1.RQSJ from t_ghxx reg1,t_ksys doc,"
     	 	    			+ "t_hzxx reg2 where reg1.BRBH='"+Main.pat_doc_num+"' "
     	 	    			+ "and doc.YSBH=reg1.YSBH and reg2.HZBH=reg1.HZBH";
     	   	         rs = statement.executeQuery(sql);
     	   	         while(rs.next())
     	   	         {
     	   	        	 regnum = rs.getString("reg1.GHBH");
     	   	             regname = rs.getString("reg2.HZMC");
     	   	             expert = rs.getBoolean("reg2.SFZJ");
     	   	             regname = regname+" "+(expert?"专家号":"普通号")+" ";
     	   	             docnum = rs.getString("reg1.YSBH");
     	   	             docname = rs.getString("doc.YSMC");
     	   	             regcount = rs.getString("reg1.GHRC");
     	   	             regcost = rs.getString("reg1.GHFY");
     	   	             flag_unreg = rs.getBoolean("reg1.THBZ");
     	   	             unreg = flag_unreg?"是":"否";
     	   	             regtime = rs.getString("reg1.RQSJ");
     	   	             ob_patreg.add(new PatientReg(regnum,regname,docnum,docname,regcount,
     	   	            		regcost,unreg,regtime));
     	   	         }
     	   	         table_patreg.setItems(ob_patreg);
     	   	         flag_of_changedbyunreg = false;
     			}
     		}
     		rs.close();
  	         
  	         statement.close();
  	         connection.close();
  	     }catch(SQLException se){
  	         se.printStackTrace();
  	     }
    }
    
    @FXML
    private void on_clearsel_clicked(ActionEvent event)
    {
    	table_patreg.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void on_btn_unreg_clicked(ActionEvent event)
    {
    	
    	ob_unreg = table_patreg.getSelectionModel().getSelectedItems();
    	PatientReg patreg;
    	
    	for(int i=0;i<ob_unreg.size();++i)
    	{
    		patreg = ob_unreg.get(i);
    		String str_unreg = patreg.getUnReg();
    		boolean flag_unreg = str_unreg.equals("是")?true:false;
    		if(flag_unreg)
    		{
    			JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
              		"当前选择号已退号", "警告", JOptionPane.WARNING_MESSAGE);
    			continue;
    		}
    		flag_of_changedbyunreg = true;
    		try
    		{
      	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
      	         statement = connection.createStatement();
      	         String sql = "update t_ghxx set THBZ=1 where GHBH"
      	         		+ "= '"+patreg.getRegNum()+"'";
      	         statement.executeUpdate(sql);
      	         
      	         sql = "update t_ghxx set GHRC=GHRC-1 where " + 
      	         		"HZBH in ( select reg3.HZBH from " + 
      	         		"(select HZBH from t_ghxx " + 
      	         		"where GHBH='"+patreg.getRegNum()+"') reg3)";
      	         statement.executeUpdate(sql);
      	         statement.close();
      	         connection.close();
      	     }catch(SQLException se){
      	         se.printStackTrace();
      	     }
    	}
    	tabunreg_sel_changed(new Event(null));
    }
    
    @FXML
    private void on_listoffice_clicked(Event event)
    {
    	String newoffice = list_office.getSelectionModel().getSelectedItem();
    	int index = list_office.getSelectionModel().getSelectedIndex();
    	flag_of_changedbysel = true;
    	if(index>-1)
    	{
    		sel_off_num = flag_off_cut ? cut_of_list.elementAt(index) : 
    			office_list.elementAt(index);
    	}
    	text_office.setText(newoffice);
    	context_office.hide();
    }
    @FXML
    private void on_text_office_clicked(Event event)
    {
        text_doc.clear();
        text_regtype.clear();
        text_regname.clear();
        text_cost.clear();
    	text_pay.clear();
    	text_charge.clear();
    	text_regnum.clear();
    	context_office.show(Main.primaryStage,
    			Main.primaryStage.getX()+128.0+10,
    			Main.primaryStage.getY()+33.0+130);
    }
    
    @FXML
    private void on_text_doc_clicked(Event event)
    {
        text_regtype.clear();
        text_regname.clear();
        text_cost.clear();
    	text_pay.clear();
    	text_charge.clear();
    	text_regnum.clear();
    	String office = text_office.getText();
    	if(office==null||office.isEmpty())
    	{
    		context_doc.hide();
    		JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
          			"请选择科室", "警告", JOptionPane.WARNING_MESSAGE);
    	}
    	else
    	{
    		ob_doc.clear();
    	   	 try
    	   	 {
    	   		 doc_list.clear();
    	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
    	         statement = connection.createStatement();
    	         String sql = "select YSBH,YSMC from t_ksys where KSBH ="
    	         		+ "'"+sel_off_num+"'";
    	         ResultSet rs = statement.executeQuery(sql);
    	         String doc_name,doc_num;
    	         while(rs.next())
    	         {
    	        	 doc_num = rs.getString("YSBH");
    	        	 doc_name = rs.getString("YSMC");
    	        	 ob_doc.add(doc_name);
    	        	 doc_list.add(doc_num);
    	         }
    	         rs.close();
    	         statement.close();
    	         connection.close();
    	     }catch(SQLException se){
    	         se.printStackTrace();
    	     }
    	   	 list_doc.setItems(ob_doc);
    	     context_doc.show(Main.primaryStage,
    	    	Main.primaryStage.getX()+385.0+10,
    	    	Main.primaryStage.getY()+33.0+130);
    	}
    }
    
    @FXML
    private void on_text_regtype_clicked(Event event)
    {
        text_regname.clear();
        text_cost.clear();
    	text_pay.clear();
    	text_charge.clear();
    	text_regnum.clear();
    	String doctor = text_doc.getText();
    	if(doctor==null||doctor.isEmpty())
    	{
    		context_doc.hide();
    		JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
	          			"请选择医生", "警告", JOptionPane.WARNING_MESSAGE);
    	}
    	else
    	{
    		list_regtype.getItems().clear();
    		try
    		{
      	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
      	         statement = connection.createStatement();
      	         String sql = "select SFZJ from t_ksys where "
      	         		+ "YSBH = '"+sel_doc_num+"'";
      	         ResultSet rs = statement.executeQuery(sql);
      	         if(rs.next())
      	         {
      	        	 boolean expert = rs.getBoolean("SFZJ");
      	             if(expert)
      	             {
      	            	 list_regtype.getItems().add("专家号");
      	            	 list_regtype.getItems().add("普通号");
      	             }
      	             else
      	             {
      	            	 list_regtype.getItems().add("普通号");
      	             }
      	           context_regtype.show(Main.primaryStage,
      	         	    Main.primaryStage.getX()+128.0+10,
      	         	   	Main.primaryStage.getY()+80.0+130);
      	         }
      	         else
      	         {
      	        	JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
      	          			"未查找到对应号种", "警告", JOptionPane.WARNING_MESSAGE);
      	        	 return;
      	         }
      	         rs.close();
      	         statement.close();
      	         connection.close();
      	     }catch(SQLException se){
      	         se.printStackTrace();
      	     }
    	}
    }
    
    @FXML
    private void on_text_regname_clicked(Event event)
    {
        text_cost.clear();
    	text_pay.clear();
    	text_charge.clear();
    	text_regnum.clear();
    	String office = text_office.getText();
    	String expertstr = text_regtype.getText();
    	if(office==null||office.isEmpty()
    		||expertstr==null||expertstr.isEmpty())
    	{
    		context_regname.hide();
    		JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
          			"请选择科室和号种类别", "警告", JOptionPane.WARNING_MESSAGE);
    		return;
    	}
    	int expert = expertstr.equals("专家号") ? 1 :0;
    	ob_regname.clear();
    	reg_list.clear();
		try
		{
	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
	         statement = connection.createStatement();
	         String sql = "select HZBH,HZMC from t_hzxx where "
		         		+ "KSBH = '"+sel_off_num+"' and "
		         		+ "SFZJ = '"+expert+"'";
	         ResultSet rs = statement.executeQuery(sql);
	         String reg_name,reg_num;
	         while(rs.next())
	         {
	        	 reg_num = rs.getString("HZBH");
	        	 reg_name = rs.getString("HZMC");
	        	 reg_list.add(reg_num);
	        	 ob_regname.add(reg_name);
	         }
	         rs.close();
	         statement.close();
	         connection.close();
	         if(ob_regname.isEmpty())
	         {
	        	 JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
 	          			"未查找到对应科室和号种", "警告", JOptionPane.WARNING_MESSAGE);
	         }
	         else
	         {
	        	 list_regname.setItems(ob_regname);
	        	 context_regname.show(Main.primaryStage,
	        		    	Main.primaryStage.getX()+385.0+10,
	        		    	Main.primaryStage.getY()+80.0+130);
	         }
	     }catch(SQLException se){
	         se.printStackTrace();
	     }
    }
    
    @FXML
    private void on_text_cost_clicked(Event event)
    {
    	text_pay.clear();
    	text_charge.clear();
    	text_regnum.clear();
    	String regname = text_regname.getText();
    	if(regname==null||regname.isEmpty())
    	{
    		JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
          			"请输入号种名称", "警告", JOptionPane.WARNING_MESSAGE);
    	}
    	else
    	{
    		try
    		{
    			double reg_cost;
    	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
    	         statement = connection.createStatement();
    	         String sql = "select GHFY from t_hzxx where "
    	         		+ "HZBH = '"+sel_reg_num+"'";
    	         ResultSet rs = statement.executeQuery(sql);
    	         if(rs.next())
    	         {
    	        	 reg_cost = rs.getDouble("GHFY");
    	             text_cost.setText(Double.toString(reg_cost));
    	         }
    	         else
    	         {
    	        	 JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
    	          			"未查找到对应号种名称", "警告", JOptionPane.WARNING_MESSAGE);
    	        	 return;
    	         }
    	         sql = "select YCJE from t_brxx where "
     	         		+ "BRBH = '"+Main.pat_doc_num+"'";
     	         rs = statement.executeQuery(sql);
     	         if(rs.next())
     	         {
     	        	 double prestore = rs.getDouble("YCJE");
     	        	 if(prestore>=reg_cost)
     	        	 {
     	        		text_pay.setText(Double.toString(prestore));
     	        		text_pay.setEditable(false);
     	        		text_charge.setText(Double.toString(prestore-reg_cost));
     	        	 }
     	        	 else
     	        	 {
     	        		 text_pay.setPromptText("请交费"+reg_cost+"元");
     	        		 text_pay.setEditable(true);
     	        	 }
     	         }
    	         rs.close();
    	         statement.close();
    	         connection.close();
    	     }catch(SQLException se){
    	         se.printStackTrace();
    	     }
    	}
    }
    
    @FXML
    private void on_text_pay_clicked(Event event)
    {
    	text_charge.clear();
    	text_regnum.clear();
    	text_pay.setPromptText("");
    }
    
    @FXML
    private void on_text_charge_clicked(Event event)
    {
    	text_regnum.clear();
    	String pays = text_pay.getText();
    	String costs = text_cost.getText();
    	if(pays==null||pays.isEmpty())
    	{
    		JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
         			"请输入交款金额", "警告", JOptionPane.WARNING_MESSAGE);
    	}
    	else
    	{
    		double pay = Double.parseDouble(pays);
    		double cost = Double.parseDouble(costs);
    		if(pay>=cost)
    		{
    			text_charge.setText(Double.toString(pay-cost));
    		}
    		else
    		{
    			JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
             			"交款金额不足", "警告", JOptionPane.WARNING_MESSAGE);
    		}
    	}
    }
    
    @FXML
    private void on_btn_ok_clicked(ActionEvent event) 
    {
    	int max_pat = 0,regcount=0;
    	String regcost = text_cost.getText();
    	int registernum=0;
    	String charge_str = text_charge.getText();
    	if(charge_str==null||charge_str.isEmpty())
    	{
    		JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
         			"请先交费", "警告", JOptionPane.WARNING_MESSAGE);
    		return;
    	}
    	try
		{
    		String currtime=null;
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
	         statement = connection.createStatement();

	         String sql = "select GHRS from t_hzxx where "
	         		+ "HZBH='"+sel_reg_num+"'";
	         ResultSet rs = statement.executeQuery(sql);
	         if(rs.next())
	         {
	        	 max_pat = rs.getInt("GHRS");
	         }
	         
	         sql = "select current_date as currtime";
	         rs = statement.executeQuery(sql);
	         if(rs.next())
	         {
	        	 currtime = rs.getString("currtime");
	        	 currtime += " 00:00:00";
	         }
	         
	         sql = "select count(*) as regcount from t_ghxx "
	         		+ "where HZBH ='"+sel_reg_num+"' and "
	         		+ "RQSJ>='"+currtime+"' and THBZ=0";
	         rs = statement.executeQuery(sql);
	         if(rs.next())
	         {
	        	 regcount = rs.getInt("regcount");
	         }
	         if(regcount>=max_pat)
	          {
	         	JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
	         			"当前挂号人数已满", "警告", JOptionPane.WARNING_MESSAGE);
	          }
	         else
	         {
	        	 sql = "select count(*) as regtotal from t_ghxx";
		         rs = statement.executeQuery(sql);
		         if(rs.next())
		         {
		        	 registernum = rs.getInt("regtotal");
		        	 registernum++;
		         }
		         text_regnum.setText(Integer.toString(registernum));
	        	 
		         sql = "insert into t_ghxx (GHBH,HZBH,"
		         		+ "YSBH,BRBH,GHRC,THBZ,GHFY,RQSJ) "
		         		+ "values ('"+registernum+"','"+sel_reg_num+"','"+sel_doc_num+"',"
		         		+ "'"+Main.pat_doc_num+"','"+(1+regcount)+"','"+0+"','"+regcost+"'"
		         		+ ",'"+df.format(new Date())+"')";
		         statement.executeUpdate(sql);
		         
		         double charge;
		         charge = Double.parseDouble(text_charge.getText());
		         if(text_pay.isEditable())
		         {
		        	 sql = "update t_brxx set YCJE = "
								+ ""+charge+"+YCJE where "
								+ "BRBH= '"+Main.pat_doc_num+"'";
		         }
		         else
		         {
		        	 sql = "update t_brxx set YCJE = "
								+ ""+charge+"where BRBH= '"+Main.pat_doc_num+"'";
		         }
		         statement.executeUpdate(sql);
		         
		         sql = "update t_ghxx set GHRC= '"+(1+regcount)+"' where "
		         		+ "HZBH='"+sel_reg_num+"' and RQSJ>="
		         		+ "'"+currtime+"'";
		         statement.executeUpdate(sql);
		         
		         flag_of_changedbyreg = true;
		         text_office.clear();
		         text_doc.clear();
		         text_regtype.clear();
		         text_regname.clear();
		         text_cost.clear();
		     	text_pay.clear();
		     	text_charge.clear();
		     	text_regnum.clear();
		     	JOptionPane.showMessageDialog(new JFrame().getContentPane(), 
	         			"挂号成功，号数"+registernum, "提示", JOptionPane.INFORMATION_MESSAGE);
	         }
	         rs.close();
	         statement.close();
	         connection.close();
	     }catch(SQLException se){
	         se.printStackTrace();
	     }
    }
    @FXML
    private void on_btn_exit_clicked(ActionEvent event)
    {
    	Event.fireEvent(Main.getPrimaryStage(),
    		new WindowEvent(Main.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
    @FXML
    private void on_btn_clear_clicked(ActionEvent event)
    {
        flag_of_changedbyreg = true;
        text_office.clear();
        text_doc.clear();
        text_regtype.clear();
        text_regname.clear();
        text_cost.clear();
    	text_pay.clear();
    	text_charge.clear();
    	text_regnum.clear();
    }
    @FXML
    private void on_btn_logout_clicked(ActionEvent event)
    {
    	Main.setLoginUi();
    }
}
