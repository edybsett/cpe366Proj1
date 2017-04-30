
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
/**
 *
 * @author eric
 */
@Named(value = "rooms")
@SessionScoped
@ManagedBean
public class Rooms {
    private DBConnect dbConnect = new DBConnect();
    private Integer rmNum;
    private String view;
    private String bed;
    private float basePrice;
    private boolean change; 

    public boolean isChange() {
        return change;
    }
    
    public void setChange(boolean x) {
        this.change = x;
    }    
    
    public Integer getRmNum() {
        return rmNum;
    }

    public void setRmNum(Integer rmNum) {
        this.rmNum = rmNum;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }
    
    public List<Rooms> getRooms() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from Room ORDER BY rmnum");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Rooms> list = new ArrayList<Rooms>();

        while (result.next()) {
            Rooms room = new Rooms();
            room.setRmNum(result.getInt("rmnum"));
            room.setView(result.getString("view"));
            room.setBed(result.getString("bed"));
            room.setBasePrice(result.getFloat("price"));
            list.add(room);
        }
        result.close();
        con.close();
        return list;
    }
    
    public String updatePrice() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        if(change == true) {
        PreparedStatement ps
                = con.prepareStatement("update Room set price=? where rmnum=? OR view=? OR bed=?");
            ps.setFloat(1, basePrice);
            ps.setInt(2, rmNum==null? 0 : rmNum);
            ps.setString(3, view);
            ps.setString(4, bed);
            ps.executeUpdate();
        }
        else {
            if(rmNum != null) {
              
                if(view.compareTo("") != 0 || bed.compareTo("") !=0) {
                    String sql = "Update Room set price = " + basePrice + " where ";
                    if(view.compareTo("") != 0 ){
                        sql = sql + " view = '" + view + "' AND ";
                    }
                    else {
                        sql = sql + " view SIMILAR to '%' AND "; 
                    }
                    if(bed.compareTo("") != 0) {
                        sql = sql + " bed = '" + bed + "' OR ";
                    }
                    else {
                        sql = sql + " bed SIMILAR to '%' OR ";
                    }
                    if(rmNum != null) {
                        sql = sql + " rmNum = " + rmNum + ";";
                    }
                    else {
                        sql = sql + "rmNum = 0;";
                    }
                    Statement statement = con.createStatement();
                    System.out.println(sql);
                    statement.executeUpdate(sql);
                }
                else {
                    String sql = "Update Room set price = " + basePrice + "where rmNum = " + rmNum + ";";
                    Statement statement = con.createStatement();
                    System.out.println(sql);
                    statement.executeUpdate(sql);
                }
            }
            else {
                if(view != null || bed != null) {
                    String sql = "Update Room set price = " + basePrice + " where ";
                    if(view.compareTo("") != 0 ){
                        sql = sql + " view = '" + view + "' AND ";
                    }
                    else {
                        sql = sql + " view SIMILAR to '%' AND "; 
                    }
                    if(bed.compareTo("") != 0) {
                        sql = sql + " bed = '" + bed + "';";
                    }
                    else {
                        sql = sql + " bed SIMILAR to '%';";
                    }
                    Statement statement = con.createStatement();
                    System.out.println(sql);
                    statement.executeUpdate(sql);
                }
            }
        }
        con.commit();
        con.close();
        return "refresh";
    }
}
