
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
 *
 * @author eric
 */
@Named(value = "specialRates")
@SessionScoped
@ManagedBean
public class SpecialRates implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private Integer rmNum;
    private Date date;
    private Date startDate;
    private Date endDate;
    private float price;
    private boolean hotelWide;
    private boolean delete = false;

   private UIInput rmNumUI;
   private UIInput startDateUI;
   private UIInput endDateUI;
   private UIInput priceChangeUI;
   private UIInput deleteUI;
   
   
    public UIInput getrmNumUI() {
        return rmNumUI;
    }

    public void setrmNumUI(UIInput x) {
        this.rmNumUI = x;
    }  
    
     public UIInput getstartDateUI() {
        return startDateUI;
    }

    public void setstartDateUI(UIInput x) {
        this.startDateUI = x;
    }  
    
    public UIInput getendDateUI() {
        return endDateUI;
    }
    

    public void setendDateUI(UIInput x) {
        this.endDateUI = x;
    }
   
    public UIInput getpriceChangeUI() {
        return priceChangeUI;
    }

    public void setpriceChangeUI(UIInput x) {
        this.priceChangeUI = x;
    }
    
   
    public Integer getRmNum() {
        return rmNum;
    }

    public Date getDate() {
        return date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate(){
        return startDate;
    }
    
    public float getPrice() {
        return price;
    }

    public void setRmNum(Integer rmNum) {
        this.rmNum = rmNum;
    }

    public void setDate(Date startDate) {
        this.date = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setPrice(float priceChange) {
        this.price = priceChange;
    }

    public boolean isHotelWide() {
        return hotelWide;
    }

    public boolean isDelete() {
        return delete;
    }
    
    public void setDelete(boolean x) {
        this.delete = x;
    }
    
    
    public void setHotelWide(boolean hotelWide) {
        this.hotelWide = hotelWide;
    }
    
    public List<SpecialRates> getHotelWide() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select date, price from HotelWideRates");
                           //"Select * from SpecialRates");
        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<SpecialRates> list = new ArrayList<SpecialRates>();

        while (result.next()) {
            SpecialRates rate = new SpecialRates();
            rate.setDate(result.getDate("date"));
            //rate.setEndDate(result.getDate("endDate"));
            rate.setPrice(result.getFloat("price"));
            rate.setHotelWide(true);
            list.add(rate);
        }
        result.close();
        con.close();
        return list;
    }
    
    public List<SpecialRates> getSpecific() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select rmNum, date, price from SpecialRates");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<SpecialRates> list = new ArrayList<SpecialRates>();

        while (result.next()) {
            SpecialRates rate = new SpecialRates();
            rate.setRmNum(result.getInt("rmNum"));
            rate.setDate(result.getDate("date"));
            //rate.setEndDate(result.getDate("endDate"));
            
            rate.setPrice(result.getFloat("price"));
            rate.setHotelWide(false);
            list.add(rate);
        }
        result.close();
        con.close();
        return list;
    }
    
    public String deleteSpecialRate() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        if(rmNum != null) {
            statement.executeUpdate("delete from SpecialRates where rmNum=" + rmNum + " date >=" 
                    +  new java.sql.Date(startDate.getTime()) + " and date<=" + new java.sql.Date(endDate.getTime()));
            statement.close();
        }
        else {
            statement.executeUpdate("delete from SpecialRates where date >=" 
                    +  new java.sql.Date(startDate.getTime()) + " and date<=" + new java.sql.Date(endDate.getTime()));
            statement.close();
        }
        con.commit();
        con.close();
        
        return "refresh";
    }
    
    
    /*
    public String addSpecialRateHW() throws ValidatorException, SQLException, ParseException {
        System.out.println("dayssss " + daysBetween(startDate, endDate));
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        
        Statement statement = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement("insert into HotelWideRates(date, price) values (?,?)");
            preparedStatement.setDate(1, new java.sql.Date(startDate.getTime()));
            preparedStatement.setFloat(2, price);
            preparedStatement.executeUpdate();
            

        statement.close();
        con.commit();
        con.close();
        return "refresh";
     
    
    }
    */
    
    
    public String SpecialRate() throws ValidatorException, SQLException, ParseException {
      
        long days2add = daysBetween(startDate, endDate);
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        
        Statement statement = con.createStatement();
        if(delete == false) {
            if(rmNum != null) {
                PreparedStatement preparedStatement = con.prepareStatement("insert into SpecialRates(rmNum, date, price) values (?,?,?)");
                for(int v = 0; v <= days2add; v++) {
                    startDate = DateUtil.addDays(startDate, 1);
                    preparedStatement.setInt(1, rmNum);
                    preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
                    preparedStatement.setFloat(3, price);
                    preparedStatement.executeUpdate();
                }
            }
            else {
                PreparedStatement preparedStatement = con.prepareStatement("insert into HotelWideRates(date, price) values (?,?)");
                for(int v = 0; v <= days2add; v++) {
                        startDate = DateUtil.addDays(startDate, 1);
                        preparedStatement.setDate(1, new java.sql.Date(startDate.getTime()));
                        preparedStatement.setFloat(2, price);
                        preparedStatement.executeUpdate();
                }
            }
        }
        else {
            if(rmNum != null) {
                //String sql = "delete from HotelWideRates where rmNum= + rmNum " + ""
                statement.executeUpdate("delete from SpecialRates where rmNum=" + rmNum + "AND date >= '" 
                    + startDate + "' AND date <= '" + endDate + "';");
                statement.close();
            }
            else {
                String sql = "delete from HotelWideRates where date >= '" + startDate + "' AND date <= '" + endDate + "';";
                statement.executeUpdate(sql);
                statement.close();
            }
        }
        rmNum = null;
        startDate = null;
        endDate = null;
        price = 0;
        statement.close();
        con.commit();
        con.close();
        return "refresh";
     
    }
    

    
    private boolean existsSpecialRate(int rm, Date start, Date end) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from SpecialRates where rmNum = " + rm + " and startDate = " + start + " and endDate = " + end);

        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }
    
    public static long daysBetween(Date startDate, Date endDate) {
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        
        calStart.setTime(startDate);
        calEnd.setTime(endDate);
        
        Calendar date = (Calendar) calStart.clone();  
        long daysBetween = 0;  
        while (date.before(calEnd)) {  
            date.add(Calendar.DAY_OF_MONTH, 1);  
            daysBetween++;  
        }  
        return daysBetween;  
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
