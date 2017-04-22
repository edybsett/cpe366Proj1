
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
/**
 *
 * @author eric
 */
@Named(value = "reservation")
@SessionScoped
@ManagedBean
public class Reservation implements Serializable {
    
    private DBConnect dbConnect = new DBConnect();
    private int resid;
    private int roomid;
    private int custid;
    private Date startdate;
    private Date enddate;
    private float basecost; //FYI this should be set ahead of time,
                            //Like when the reservation is created the cost should be set
                            //Do not use the special rates table/reservation transformation table
                            //to set this variable!
    private boolean checkedIn;
    
    private UIInput residUI;

    public int getResid() {
        return resid;
    }

    public void setResid(int resid) {
        this.resid = resid;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public int getCustid() {
        return custid;
    }

    public void setCustid(int custid) {
        this.custid = custid;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public float getBasecost() {
        return basecost;
    }

    public void setBasecost(float basecost) {
        this.basecost = basecost;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public UIInput getResidUI() {
        return residUI;
    }

    public void setResidUI(UIInput residUI) {
        this.residUI = residUI;
    }
    
    public String checkIn() throws SQLException {
        resid = Integer.parseInt(residUI.getLocalValue().toString());
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        Statement ps = con.createStatement();
        ps.executeUpdate("update reservation set checkedIn = true where resid = " + resid);
        
        ps.close();
        con.commit();
        con.close();
        return "refresh";
    }
    
}
