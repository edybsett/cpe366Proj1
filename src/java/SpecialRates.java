
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
@Named(value = "specialRates")
@SessionScoped
@ManagedBean
public class SpecialRates implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private Integer rmNum;
    private Date startDate;
    private Date endDate;
    private float priceChange;

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
    
    public List<SpecialRates> getHotelWide() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select startDate, endDate, priceChange from SpecialRates where hotelWide=true");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<SpecialRates> list = new ArrayList<SpecialRates>();

        while (result.next()) {
            SpecialRates rate = new SpecialRates();
            rate.setStartDate(result.getDate("startDate"));
            rate.setEndDate(result.getDate("endDate"));
            rate.setPriceChange(result.getFloat("priceChange"));
            
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
            
            list.add(rate);
        }
        result.close();
        con.close();
        return list;
    }
    
    
}
