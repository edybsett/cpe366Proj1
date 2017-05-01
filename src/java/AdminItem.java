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
/**
 *
 * @author eric
 */
@Named(value = "admin")
@SessionScoped
@ManagedBean
public class AdminItem {
    private int id;
    private String name;
    private String password;
    private String firstName;
    private String lastName;
    
    public AdminItem(){}
    
    public AdminItem(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
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
    
    public List<AdminItem> getAdmins() throws SQLException {
        DBConnect dbCon = new DBConnect();
        Connection con = dbCon.getConnection();
        
        if (con == null) {
            throw new SQLException("could not connect to DB");
        }
        
        String q = "SELECT id, username, password ";
        q       += "FROM Login WHERE title='admin' ";
        
        PreparedStatement ps = con.prepareStatement(q);
        ResultSet result = ps.executeQuery();
        List<AdminItem> ret = new ArrayList<AdminItem>();
        while (result.next()) {
            AdminItem ad = new AdminItem(result.getInt("id"), 
                    result.getString("username"),
            result.getString("password"));
            ret.add(ad);
        }
        return ret;
  
    }
}
