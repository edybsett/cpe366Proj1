
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
@Named(value = "specialRates")
@SessionScoped
@ManagedBean
public class SpecialRates implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private Integer rmNum;
    private Date startDate;
    private Date endDate;
    private float priceChange;
    private boolean hotelWide;

   private UIInput rmNumUI;
   private UIInput startDateUI;
   private UIInput endDateUI;
   private UIInput priceChangeUI;
   
   
   
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

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public float getPriceChange() {
        return priceChange;
    }

    public void setRmNum(Integer rmNum) {
        this.rmNum = rmNum;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPriceChange(float priceChange) {
        this.priceChange = priceChange;
    }

    public boolean isHotelWide() {
        return hotelWide;
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
                        "select startDate, endDate, priceChange from SpecialRates where hotelWide=true");
                           //"Select * from SpecialRates");
        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<SpecialRates> list = new ArrayList<SpecialRates>();

        while (result.next()) {
            SpecialRates rate = new SpecialRates();
            rate.setStartDate(result.getDate("startDate"));
            rate.setEndDate(result.getDate("endDate"));
            rate.setPriceChange(result.getFloat("priceChange"));
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
                        "select rmNum, startDate, endDate, priceChange from SpecialRates where hotelWide=false");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<SpecialRates> list = new ArrayList<SpecialRates>();

        while (result.next()) {
            SpecialRates rate = new SpecialRates();
            rate.setRmNum(result.getInt("rmNum"));
            rate.setStartDate(result.getDate("startDate"));
            rate.setEndDate(result.getDate("endDate"));
            rate.setPriceChange(result.getFloat("priceChange"));
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
        statement.executeUpdate("delete from SpecialRates where rmNum=" + rmNum + " and startDate=" 
                +  new java.sql.Date(startDate.getTime()) + " and endDate=" + new java.sql.Date(endDate.getTime()));
        statement.close();
        con.commit();
        con.close();
        
        return "refresh";
    }
    
    public String addSpecialRate(FacesContext context, UIComponent component, Object value) throws ValidatorException, SQLException, ParseException {
    
        DateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-mm-dd");
        
        rmNum = Integer.parseInt(rmNumUI.getLocalValue().toString());
        startDate = formatter.parse(startDateUI.getLocalValue().toString());
        endDate = formatter.parse(endDateUI.getLocalValue().toString());
        priceChange = Float.parseFloat(priceChangeUI.getLocalValue().toString());  
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        
        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("insert into SpecialRates(rmNum, startDate, endDate, hotelWide, priceChange) value (?,?,?,?,?)");
        if (hotelWide == false) {
            preparedStatement.setInt(1, rmNum);
            preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));
            preparedStatement.setBoolean(4, hotelWide);
            preparedStatement.setFloat(5, priceChange);
            preparedStatement.executeUpdate();
            
        }
        else {
            rmNum = 100;
            for (int i = 0; i < 60; i++) {
                int start = (int)i/12 * 100 + 101;
                preparedStatement.setInt(1, start + (i % 12));
                preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
                preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));
                preparedStatement.setBoolean(4, hotelWide);
                preparedStatement.setFloat(5, priceChange);
                preparedStatement.executeUpdate();
            }
        }
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
    
}
