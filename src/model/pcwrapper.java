/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Yuki
 */
public class pcwrapper {
    
    ArrayList<Component> compArr;
    
    public pcwrapper(){}
    @XmlElement(name = "arr")
    public ArrayList<Component> getPCList(){
        return compArr;
    }
    public void setPCList(ArrayList<Component> list){
        compArr = list;
    }
    
}
