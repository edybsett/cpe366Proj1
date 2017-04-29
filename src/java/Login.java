
    
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
import javax.servlet.http.HttpSession;
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
    
    
    public String createLogin() throws SQLException, ParseException {
        return new Form(login, password).createLogin();
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
            FacesMessage errorMessage = new FacesMessage("Wrong login/password");
            throw new ValidatorException(errorMessage);
        }
        String getTitle = result.getString("title");
        result.close();
        
        con.close();
        
        setDestination = getTitle;
        
        if (setDestination == null) {
            
        }
    }
    
    public String logout() {
        Util.invalidateUserSession();
        return "logout";
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
