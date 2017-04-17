
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
    
    public List<Rooms> getSpecific() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from Room");

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
        con.commit();
        con.close();
        return list;
    }
    
    public String updatePrice() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement("update Room set price=? where rmnum=?");
        ps.setFloat(1, basePrice);
        ps.setInt(2, rmNum);
        ResultSet result = ps.executeQuery();
        result.close();
        con.commit();
        con.close();
        return "refresh";
    }
}
