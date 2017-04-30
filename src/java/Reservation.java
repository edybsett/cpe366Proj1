
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
import java.util.Calendar;
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
 * Deals with all reservations and part of billing.
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
    private Date UIstart;
    private Date UIend;
    
                            //Like when the reservation is created the cost should be set
                            //Do not use the special rates table/reservation transformation table
                            //to set this variable!
    private boolean checkedIn;
    private int resIdForBill;
    
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
    
    public Date getUIend() {
        return UIend;
    }

    public void setUIend(Date enddate) {
        this.UIend = enddate;
    }
    
    public Date getUIstart() {
        return UIstart;
    }

    public void setUIstart(Date startdate) {
        this.UIstart = startdate;
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
        resIdForBill = 0;
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        Statement ps = con.createStatement();
        ps.executeUpdate("update reservation set checkedIn = false where resid = " + resid);
        resIdForBill = resid; 
        ps.close();
        //con.commit();
        con.close();
        return "bill";
    }
    
    public String goBack() {
        return "back";
    }
    

    public List<Fees> showFees() throws SQLException {  //Has to do with the reservations more than it has to do for fees.
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select f.name, f.price from fees f, resxfee r\n" +
                        "where r.feeid = f.id and f.id != 0 and r.resid = " + resIdForBill);
        ResultSet result = ps.executeQuery();
        List<Fees> list = new ArrayList<Fees>();
        while (result.next()) {
            Fees fee = new Fees();
            fee.setName(result.getString("name"));
            fee.setPrice(result.getFloat("price"));
            list.add(fee);
        }
        return list;
    }
    
    public List<Reservation> showBillEmployeeSide() throws SQLException {
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
                        "where r.custid = c.cid and r.resid = f.resid and r.resid = " + resIdForBill);

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
    
    public String makeReservation() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        

        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement("Insert into Reservation(roomid, custid, startdate, enddate, basecost, fees) values(?,?,?,?,?,?)");
        preparedStatement.setInt(1, roomid);
        preparedStatement.setInt(2, custid);
        preparedStatement.setDate(3, new java.sql.Date(startdate.getTime()));
        preparedStatement.setDate(4, new java.sql.Date(enddate.getTime()));
        preparedStatement.setFloat(5, 100);
        preparedStatement.setFloat(6, 0);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        
  
        con.close();
        Util.invalidateUserSession();
        return "made";
    }
    
     public String removeReservation() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        Statement ps = con.createStatement();
        ps.executeUpdate("delete from resxfee where resid = " + resid);
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
    
    
    
    public String findAvailable(){
        return "refresh";
    }
    
    public List<Rooms> getAvailableRooms() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        PreparedStatement ps = con.prepareStatement("select rmNum, view, price, bed from  Room WHERE rmnum NOT IN  (select roomid AS rmnum FROM Reservation WHERE ((? <= enddate AND ? >= startDate) OR (? >= startdate AND ? <= enddate) OR (startdate >= ? AND startdate <= ?))) ORDER BY rmnum");
        
        if (UIstart == null)
        {
            UIstart = new Date(System.currentTimeMillis());
            UIend = new Date(System.currentTimeMillis());
        }
        ps.setDate(1, new java.sql.Date(DateUtil.addDays(UIstart, 1).getTime()));
        ps.setDate(2, new java.sql.Date(DateUtil.addDays(UIstart, 1).getTime()));
        ps.setDate(3, new java.sql.Date(DateUtil.addDays(UIend, 1).getTime()));
        ps.setDate(4, new java.sql.Date(DateUtil.addDays(UIend, 1).getTime()));
        ps.setDate(5, new java.sql.Date(DateUtil.addDays(UIstart, 1).getTime()));                
        ps.setDate(6, new java.sql.Date(DateUtil.addDays(UIend, 1).getTime()));
        ResultSet result = ps.executeQuery();
        
        List<Rooms> list = new ArrayList<Rooms>();

        while (result.next()) {
            Rooms room = new Rooms();
            room.setRmNum(result.getInt("rmNum"));
            room.setView(result.getString("view"));
            //rate.setEndDate(result.getDate("endDate"));
            
            room.setBasePrice(result.getFloat("price"));
            room.setBed(result.getString("bed"));
            list.add(room);
        }
        result.close();
        con.close();
        return list;
    }
    
        public static class DateUtil  {
        public static Date addDays(Date date, int days)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days); //minus number would decrement the days
            return cal.getTime();
        }
    }
    
}
