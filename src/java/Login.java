
    
import java.io.Serializable;
import java.sql.SQLException;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stanchev
 */
@Named(value = "login")
@SessionScoped
@ManagedBean
public class Login implements Serializable {

    private int lid;
    private String login;
    private String password;
    private String title;
    private UIInput loginUI;
    private DBConnect dbConnect = new DBConnect();
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String ccnum;
    private String cvc;
    private String expdate;
    private String type;
    private String setDestination;
    
    
    public UIInput getLoginUI() {
        return loginUI;
    }

    public void setLoginUI(UIInput loginUI) {
        this.loginUI = loginUI;
    }

    public String getccnum()
    {
        return ccnum;
    }
    public void setccnum(String cc)
    {
        this.ccnum = cc;
    }
    public String getFirstName() {
           return firstName;
    }
    public void setFirstName(String name) {
        this.firstName = name;
    }
    
    public void setLastName(String name){
        this.lastName = name;
    }
    
    public String getLastName(){
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
    
    public int getlid(){
        return lid;
    }
    public void setlid(int lid){
        this.lid = lid;
    }
    public String getLogin() {
        return login;
    }
    
    public String getTitle(){
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String createCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Customer values(?,?,?,?,?)");
        preparedStatement.setInt(1, lid);
        preparedStatement.setString(2, firstName);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, address);
        preparedStatement.setString(5, email);
        preparedStatement.executeUpdate();
        statement.close();
        
        Statement getids = con.createStatement();
        PreparedStatement prepedId = con.prepareStatement("select id from Login");
        ResultSet result = prepedId.executeQuery();
            if (!result.next()) {
                return null;
            }
        lid = result.getInt(1);
        result.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "main";
    }
    
    public String createLogin() throws SQLException, ParseException {
       Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Login(username, password, title) values(?,?,?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, "customer");
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        
        
        Statement getids = con.createStatement();
        PreparedStatement prepedId = con.prepareStatement("SELECT id from Login WHERE username = ? AND password = ?");
        prepedId.setString(1, login);
        prepedId.setString(2, password);
        ResultSet result = prepedId.executeQuery();
            if (!result.next()) {
                return null;
            }
        lid = result.getInt(1);
        result.close();
        con.commit();
        
        
        
        con.close();
        Util.invalidateUserSession();
        return createCustomer();
    }

    public String createEmployee() throws SQLException, ParseException {
       Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Login(username, password, title) values(?,?,?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, "employee");
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "admin";
    }
    
    public String createAdmin() throws SQLException, ParseException {
       Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Login(username, password, title) values(?,?,?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, "admin");
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "admin";
    }
    
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        login = loginUI.getLocalValue().toString();
        password = value.toString();
        setDestination = "";
        Connection con = dbConnect.getConnection();

        if (con == null) {
           throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                    = con.prepareStatement(
                            "select title from Login where username=? and password=?");
        ps.setString(1, login);
        ps.setString(2, password);
        ResultSet result = ps.executeQuery();
        if (!result.next()) {
            return;
        }
        String getTitle = result.getString(1);
        result.close();
        
        con.close();
        
        switch (getTitle) {
            case "admin":
                setDestination = getTitle;
                break;
            case "employee":
                setDestination = getTitle;
                break;
            case "customer":
                setDestination = getTitle;
                break;
            default:
                FacesMessage errorMessage = new FacesMessage("Wrong login/password");
                throw new ValidatorException(errorMessage);
        }
                
    }

    public String go() {
      //  Util.invalidateUserSession();
        return setDestination;
    }
    
    public String register() {
      //  Util.invalidateUserSession();
        return "register";
    }
}
