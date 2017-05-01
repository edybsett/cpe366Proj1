/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jason
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Named(value = "admindirectory")
@SessionScoped
@ManagedBean
public class Admin {

    
    public String showInfo() {
        return "showEmployeeInfo";
    }
    
    public String showAdminInfo() {
        return "showAdminInfo";
    }
    
    public String addEmployee(){
        return "addEmployee";
    }
    
    public String addAdmin() {
        return "addAdmin";
    }
    
    public String changeBasePrices() {
        return "changeBasePrices";
    }
    
    public String showEmployeeList(){
        return "showEmployeeList";
    }
    
    public String showAdminList(){
        return "showAdminList";
    }
    
    public String showSpecialRates(){
        return "showSpecialRates";
    }
}
