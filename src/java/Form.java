
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Austin
 */
@Named(value = "employee")
@SessionScoped
@ManagedBean
public class Form {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String ccn;
    private Date expiration;
    private Integer crc;
    private String ccnType;
    private int    id;
    private DBConnect dbConnect = new DBConnect();
    
    public Form() {}
    
    public Form(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
       /**
     * @return the address
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param address the address to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
     /**
     * @param creditCrc the crc number to set
     */
    public void setCrc(Integer creditCrc){
        this.crc = creditCrc;
    }
    
    /**
     * @return the crc code
     */
    public Integer getCrc(){
        return crc;
    }
    
    /**
     * @param credit the credit card number to set
     */
    public void setCcn(String credit){
        this.ccn = credit;
    }
    
    /**
     * @return the Credit card number
     */
    public String getCcn(){
        return ccn;
    }
    
    
    
    /**
     * @return the Credit Card type
     */
    public String getCcnType() {
        return ccnType;
    }

    /**
     * @param type the Credit card type to set
     */
    public void setCcnType (String type) {
        this.password = type;
    }
    
    /**
     * @param date the expiration date to set
     */
    public void setExpiration(Date date){
        this.expiration = date;
    }
    
    /**
     * @return the expiration
     */
    public Date getExpiration(){
        return expiration;
    }
    
    
    public String createEmployee() throws SQLException, ParseException {
       Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Login(username, password, title) values(?,?,?)");
        preparedStatement.setString(1, getUsername());
        preparedStatement.setString(2, getPassword());
        preparedStatement.setString(3, "employee");
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
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
        preparedStatement.setString(1, getUsername());
        preparedStatement.setString(2, getPassword());
        preparedStatement.setString(3, "admin");
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "admin";
    }
      
      public String createCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Customer values(?,?,?,?,?)");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, getFirstName());
        preparedStatement.setString(3, getFirstName());
        preparedStatement.setString(4, getAddress());
        preparedStatement.setString(5, getEmail());
        preparedStatement.executeUpdate();
        statement.close();
        
        Statement getids = con.createStatement();
        PreparedStatement prepedId = con.prepareStatement("select id from Login");
        ResultSet result = prepedId.executeQuery();
            if (!result.next()) {
                return null;
            }
        id = result.getInt(1);
        result.close();
        con.commit();
        
        
        PreparedStatement bankStatement = con.prepareStatement("Insert into Banking values(?, ?, ?, ?, ?)");
        bankStatement.setInt(1,id);
        bankStatement.setString(2, ccn);
        bankStatement.setInt(3, crc);
        bankStatement.setDate(4, new java.sql.Date(expiration.getTime()));
        bankStatement.setString(5, "Visa");
        bankStatement.executeUpdate();
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
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, "customer");
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        
        Statement getids = con.createStatement();
        PreparedStatement prepedId = con.prepareStatement("SELECT id from Login WHERE username = ? AND password = ?");
        prepedId.setString(1, username);
        prepedId.setString(2, password);
        ResultSet result = prepedId.executeQuery();
            if (!result.next()) {
                return null;
            }
        id = result.getInt(1);
        result.close();
        con.commit();
        
        
        
        con.close();
        Util.invalidateUserSession();
        return createCustomer();
    }
    
    public void checkUsernameTaken(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement getids = con.createStatement();
        PreparedStatement prepedId = con.prepareStatement("SELECT id from Login WHERE username = ?");
        prepedId.setString(1, username);
        ResultSet result = prepedId.executeQuery();
        // Invalid username if it exists
        if (result.next()) {
            FacesMessage errorMessage = new FacesMessage("Username taken");
            throw new ValidatorException(errorMessage);
        }
    }
    
    
   


}
