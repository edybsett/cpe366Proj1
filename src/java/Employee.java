
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
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
public class Employee {
    
    private int id;
    private String name;
    private String password;
    
    public Employee(){}
    
    public Employee(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param id the id to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String toString(){
        return id + ": " + name;
    }
    
    public void changePassword() throws SQLException {
        DBConnect dbCon = new DBConnect();
        Connection con = dbCon.getConnection();
        
        if (con == null) {
            throw new SQLException("could not connect to DB");
        }
        
        
        String q = "update Login set password = ? where id = ?;";
        PreparedStatement ps = con.prepareStatement(q);
        ps.setString(1, password);
        ps.setInt(2, id);
        int result = ps.executeUpdate();
    }
    
    public List<Employee> getEmps() throws SQLException {
        DBConnect dbCon = new DBConnect();
        Connection con = dbCon.getConnection();
        
        if (con == null) {
            throw new SQLException("could not connect to DB");
        }
        
        
        String q = "select id,username,password from Login where title='employee';";
        PreparedStatement ps = con.prepareStatement(q);
        ResultSet result = ps.executeQuery();
        List<Employee> ret = new ArrayList<Employee>();
        while (result.next()) {
            Employee emp = new Employee(result.getInt("id"), 
                    result.getString("username"),
            result.getString("password"));
            ret.add(emp);
        }
        return ret;
  
    }
}
