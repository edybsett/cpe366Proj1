
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
    private float basecost; 
    private boolean checkedIn;
    private int resIdForBill;
    private String bed;
    private String view;
    
    private UIInput residUI = new UIInput();
    private List<SpecialRates> sRates = new ArrayList<SpecialRates>();
   
   
    /**
     * @return the sRates
     */
    public List<SpecialRates> getsRates() {
        return sRates;
    }

    /**
     * @param sRates the sRates to set
     */
    public void setsRates(List<SpecialRates> sRates) {
        this.sRates = sRates;
    }

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
        if (startdate != null)
            return startdate;
        else
            return new Date(System.currentTimeMillis());
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
        //this.startdate = DateUtil.addDays(startdate, 1);
    }

    public Date getEnddate() {
        if (enddate != null)
            return enddate;
        else
            return DateUtil.addDays(new Date(System.currentTimeMillis()), 1);
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
        //this.enddate = DateUtil.addDays(enddate, 1);
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
    
        /**
     * @return the bed
     */
    public String getBed() {
        return bed;
    }

    /**
     * @param bed the bed to set
     */
    public void setBed(String bed) {
        this.bed = bed;
    }

    /**
     * @return the view
     */
    public String getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(String view) {
        this.view = view;
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
        addBillDB();
        return "bill";
    }
    
    public String goBack() {
        return "back";
    }
    
    public String rescheckout() {
        return "checkout";
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
                        "select r.resid, r.checkedIn, c.lastname, r.custid, c.firstname, \n" +
                        "  r.startdate, r.enddate, r.roomId, r.basecost, b.ccnum,\n" +
                        "  CASE WHEN f.sum = NULL \n" +
                        "    THEN 0 \n" +
                        "  ELSE f.sum \n" +
                        "  END as fees \n" +
                        "from reservation r, customer c, banking b,\n" +
                        "    (SELECT sum(price), Reservation.resId FROM Reservation, Fees, ResXFee\n" +
                        "      where Reservation.resId = ResXFee.resId AND\n" +
                        "      feeId = Fees.id group by Reservation.resId) f\n" +
                        "where r.custid = c.cid and r.resid = f.resid and b.cid = r.custid and r.resid = " + resIdForBill);

        //get customer data from database
        ResultSet result = ps.executeQuery();
        List<Reservation> list = new ArrayList<Reservation>();
        String creditcard = "";
        float totalcost = 0;
        int custId = 0;
        
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
            creditcard = result.getString("ccnum");
            totalcost = result.getFloat("basecost") + result.getFloat("fees");
            custId = result.getInt("custid");
            list.add(rate);
            
        }
        
        result.close();
        con.close();
        return list;
    
    }
    
    public void addBillDB() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select r.resid, r.checkedIn, c.lastname, r.custid, c.firstname, \n" +
                        "  r.startdate, r.enddate, r.roomId, r.basecost, b.ccnum,\n" +
                        "  CASE WHEN f.sum = NULL \n" +
                        "    THEN 0 \n" +
                        "  ELSE f.sum \n" +
                        "  END as fees \n" +
                        "from reservation r, customer c, banking b,\n" +
                        "    (SELECT sum(price), Reservation.resId FROM Reservation, Fees, ResXFee\n" +
                        "      where Reservation.resId = ResXFee.resId AND\n" +
                        "      feeId = Fees.id group by Reservation.resId) f\n" +
                        "where r.custid = c.cid and r.resid = f.resid and b.cid = r.custid and r.resid = " + resIdForBill);

        //get customer data from database
        ResultSet result = ps.executeQuery();
        String creditcard = "";
        float totalcost = 0;
        int custId = 0;
        
        while (result.next()) {
            
            creditcard = result.getString("ccnum");
            totalcost = result.getFloat("basecost") + result.getFloat("fees");
            custId = result.getInt("custid");
            
        }
        
        result.close();
        con.close();
        addBillToDB(creditcard, totalcost, custId);
    
    }
    
    public void addBillToDB(String creditcard, float cost, int custid) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        PreparedStatement preparedStatement = con.prepareStatement("Insert into Bill values(?,?,?,?)");
        preparedStatement.setString(1, creditcard);
        preparedStatement.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
        preparedStatement.setFloat(3, cost);
        preparedStatement.setInt(4, custid);
        preparedStatement.executeUpdate();
        con.commit();
        con.close();
    }
    
    public void setAvailableRoom() throws SQLException {
        PreparedStatement ps;
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        /* GET a the first room available with the specified types */
        String query = "SELECT rmNum, view, bed, price FROM Room\n";
        query       += "WHERE rmNum NOT IN ";
        query       += "(SELECT roomid AS rmnum ";
        query       += "FROM Reservation WHERE ";
        query       += "GREATEST(startdate, ?) <= LEAST(enddate, ?))";
        query       += "AND view=? AND bed=? ";
        query       += "ORDER BY rmNum";
        ps = con.prepareStatement(query);
        ps.setDate(1, new java.sql.Date(startdate.getTime()));
        ps.setDate(2, new java.sql.Date(enddate.getTime()));
        ps.setString(3, view);
        ps.setString(4, bed);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            roomid = rs.getInt("rmNum");
        }
        con.commit();
        con.close();
    }
    
    public void setCost() throws SQLException {
        String query;
        PreparedStatement ps;
        basecost = 0;
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        /* NOW get the price of the room including special rates for each day*/
        Date endp1 = DateUtil.addDays(enddate, 1);
        for(Date curdate = startdate; !curdate.equals(endp1); curdate = DateUtil.addDays(curdate, 1)) {
            /* SEARCH for a SpecialPrice first */
            query = "SELECT price FROM SpecialRates ";
            query += "WHERE rmNum = ? AND date = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, roomid);
            ps.setDate(2, new java.sql.Date(curdate.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                float cost = rs.getFloat("price");
                basecost += cost;
                SpecialRates sr = new SpecialRates();
                sr.setDate(curdate);
                sr.setPrice(cost);
                sRates.add(sr);
                continue;
            }
            /* Now CHECK for a HotelWideRate */
            query = "SELECT price FROM HotelWideRates ";
            query += "WHERE date = ?";
            ps = con.prepareStatement(query);
            ps.setDate(1, new java.sql.Date(curdate.getTime()));
            rs = ps.executeQuery();
            if (rs.next()) {
                float cost = rs.getFloat("price");
                basecost += cost;
                SpecialRates sr = new SpecialRates();
                sr.setDate(curdate);
                sr.setPrice(cost);
                sRates.add(sr);
                continue;
            }
            /* Now we get the base price if there are no special ones */
            query = "SELECT price FROM Room ";
            query += "WHERE rmNum = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, roomid);
            rs = ps.executeQuery();
            if (rs.next()) {
                basecost += rs.getFloat("price");
            }
            else {
                throw new SQLException("Unknown error!");
            }
        }
        con.commit();
        con.close();
                
    }
    
    public String makeReservation() throws SQLException {
        Connection con = dbConnect.getConnection();
        PreparedStatement ps;
        String query;
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        query = "INSERT INTO Reservation(roomid, custid, startdate, enddate, basecost) ";
        query += "VALUES(?,?,?,?,?)";
        ps = con.prepareStatement(query);
        ps.setInt(1, roomid);
        ps.setInt(2, custid);
        // For some reason, startdate and enddate go in one day behind.
        // Just gonna add one day and hope that fixes it.
        Date nstart = DateUtil.addDays(startdate, 1);
        Date nend   = DateUtil.addDays(enddate, 1);
        ps.setDate(3, new java.sql.Date(nstart.getTime()));
        ps.setDate(4, new java.sql.Date(nend.getTime()));
        ps.setFloat(5, basecost);
        ps.executeUpdate();
        
        /* Now get the Reservation id */
        query = "SELECT resid from Reservation ";
        query += "WHERE roomid=? AND custid=? ";
        query += "AND startdate = ? AND enddate = ? ";
        query += "AND basecost = ?";
        ps = con.prepareStatement(query);
        ps.setInt(1, roomid);
        ps.setInt(2, custid);
        ps.setDate(3, new java.sql.Date(nstart.getTime()));
        ps.setDate(4, new java.sql.Date(nend.getTime()));
        ps.setFloat(5, basecost);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            int resid = rs.getInt("resid");
            /* Now insert a 0 fee */
            query = "INSERT INTO ResXFee(resid, feeid) ";
            query += "VALUES(?, 0)";
            ps = con.prepareStatement(query);
            ps.setInt(1, resid);
            ps.executeUpdate();
        }
        con.commit();
        con.close();
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
        
        String query = "SELECT rmNum, view, bed, price FROM Room\n";
        query       += "WHERE rmNum NOT IN ";
        query       += "(SELECT roomid AS rmnum ";
        query       += "FROM Reservation WHERE ";
        query       += "GREATEST(startdate, ?) < LEAST(enddate, ?))";
        query       += "ORDER BY rmNum";
        PreparedStatement ps = con.prepareStatement(query);
        
        // See getters for why I did this
        startdate = getStartdate();
        enddate   = getEnddate();
        
        ps.setDate(1, new java.sql.Date(startdate.getTime()));
        ps.setDate(2, new java.sql.Date(enddate.getTime()));
        ResultSet result = ps.executeQuery();
        
        List<Rooms> list = new ArrayList<Rooms>();

        while (result.next()) {
            Rooms room = new Rooms();
            room.setView(result.getString("view"));            
            room.setBasePrice(result.getFloat("price"));
            room.setBed(result.getString("bed"));
            room.setRmNum(result.getInt("rmNum"));
            list.add(room);
        }
        /* SET the room this person is going to get */
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
