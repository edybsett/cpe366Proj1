
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

@Named(value = "customer")
@SessionScoped
@ManagedBean
public class Customer implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    private DBConnect dbConnect = new DBConnect();
    private Integer cid;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    
    private String view;
    private String bed;
    private int resid;
    private int roomid;
    private int custid;
    private Date startdate;
    private Date enddate;


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
    
    
    public Integer getCustomerID() throws SQLException {
        if (cid == null) {
            Connection con = dbConnect.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            PreparedStatement ps
                    = con.prepareStatement(
                            "select max(cid)+1 from customer");
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                return null;
            }
            cid = result.getInt(1);
            result.close();
            con.close();
        }
        return cid;
    }

    public void setCustomerID(Integer customerID) {
        this.cid = customerID;
    }

    public String getFirstName() {
           return firstName;
    }
    public void setFirstName(String name) {
        this.firstName = name;
    }
    
    public void setLastName(String name){
        this.lastName = name;
    }
    
    public String getLastName(){
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String createCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Customer values(?,?,?,?,?)");
        preparedStatement.setInt(1, cid);
        preparedStatement.setString(2, firstName);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, address);
        preparedStatement.setString(5, email);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "main";
    }

    public String deleteCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        statement.executeUpdate("Delete from Customer where cid = " + cid);
        statement.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "main";
    }

    public String showCustomer() {
        return "showCustomer";
    }

    public Customer getCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from customer where cid = " + cid);

        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        firstName = result.getString("firstName");
        lastName = result.getString("lastName");
        address = result.getString("address");
        email = result.getString("email");
        return this;
    }
    
    public List<Customer> getReservations() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        List<Customer> list = new ArrayList<Customer>();
        PreparedStatement ps
                = con.prepareStatement(
                        "select resid, roomid, startdate, enddate from reservation where " + "custId = " + login.getLid() + ";");        
        

        //get customer data from database
        ResultSet result = ps.executeQuery();

     
        while (result.next()) {
            Customer cust = new Customer();

            cust.setResid(result.getInt("resid"));
            cust.setRoomid(result.getInt("roomid"));
            cust.setStartdate(result.getDate("startdate"));
            cust.setEnddate(result.getDate("enddate"));
            PreparedStatement ps2 = con.prepareStatement("select bed, view from room where rmnum = " + cust.getRoomid() + ";");
            ResultSet result2 = ps2.executeQuery();
            while(result2.next()){
                cust.setBed(result2.getString("bed"));
                cust.setView(result2.getString("view"));
            }
            result2.close();
            //store all data into a List
            list.add(cust);
        }
        System.out.println("list " + list.size() + "cust id " + login.getLid());
        result.close();
        con.close();
        return list;
    }

    public List<Customer> getCustomerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select cid, firstName, lastName, address, email from customer order by cid");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Customer> list = new ArrayList<Customer>();

        while (result.next()) {
            Customer cust = new Customer();

            cust.setCustomerID(result.getInt("cid"));
            cust.setFirstName(result.getString("firstName"));
            cust.setLastName(result.getString("lastName"));
            cust.setAddress(result.getString("address"));
            cust.setEmail(result.getString("email"));

            //store all data into a List
            list.add(cust);
        }
        result.close();
        con.close();
        return list;
    }

    public void customerIDExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {

        if (!existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validateCustomerID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        int id = (Integer) value;
        if (id < 0) {
            FacesMessage errorMessage = new FacesMessage("ID must be positive");
            throw new ValidatorException(errorMessage);
        }
        if (existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID already exists");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsCustomerId(int id) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from customer where cid = " + id);

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
    
    public String makeReservation(){
        return "reservation";
    }
}
