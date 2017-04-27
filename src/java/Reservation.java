
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
    private float totalCost;
    private String custFirst;
    private String custLast;
    private float basecost; //FYI this should be set ahead of time,

    
                            //Like when the reservation is created the cost should be set
                            //Do not use the special rates table/reservation transformation table
                            //to set this variable!
    private boolean checkedIn;
    
    private UIInput residUI = new UIInput();

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

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public String getCustFirst() {
        return custFirst;
    }

    public void setCustFirst(String custFirst) {
        this.custFirst = custFirst;
    }

    public String getCustLast() {
        return custLast;
    }

    public void setCustLast(String custLast) {
        this.custLast = custLast;
    }
    
    public String checkIn() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        Statement ps = con.createStatement();
        ps.executeUpdate("update reservation set checkedIn = true where resid = " + resid);
        
        ps.close();
        //con.commit();
        con.close();
        return "refresh";
    }
    
    public String checkOut() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        Statement ps = con.createStatement();
        ps.executeUpdate("update reservation set checkedIn = false where resid = " + resid);
        
        ps.close();
        //con.commit();
        con.close();
        return "refresh";
    }
    
     public String removeReservation() throws SQLException {
        resid = Integer.parseInt(residUI.getLocalValue().toString());
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        Statement ps = con.createStatement();
        ps.executeUpdate("delete from reservation where resid = " + resid);
        
        ps.close();
        //con.commit();
        con.close();
        return "refresh";
    }
    
    public List<Reservation> getReservations() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select r.resid, r.checkedIn, c.lastname, c.firstname, \n" +
                        "  r.startdate, r.enddate, r.roomId, r.basecost, \n" +
                        "  CASE WHEN f.sum = NULL \n" +
                        "    THEN 0 \n" +
                        "  ELSE f.sum \n" +
                        "  END as fees \n" +
                        "from reservation r, customer c, \n" +
                        "    (SELECT sum(price), Reservation.resId FROM Reservation, Fees, ResXFee\n" +
                        "      where Reservation.resId = ResXFee.resId AND\n" +
                        "      feeId = Fees.id group by Reservation.resId) f\n" +
                        "where r.custid = c.cid and r.resid = f.resid;");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Reservation> list = new ArrayList<Reservation>();

        while (result.next()) {
            Reservation rate = new Reservation();
            rate.setResid(result.getInt("resid"));
            rate.setCheckedIn(result.getBoolean("checkedIn"));
           //rate.setCustid(result.getInt("custid"));
            rate.setCustLast(result.getString("lastname"));
            rate.setCustFirst(result.getString("firstname"));
            rate.setStartdate(result.getDate("startdate"));
            rate.setEnddate(result.getDate("enddate"));
            rate.setRoomid(result.getInt("roomId"));
            
            rate.setTotalCost(result.getFloat("basecost") + result.getFloat("fees"));
            list.add(rate);
        }
        result.close();
        con.close();
        return list;
    }
    
}
