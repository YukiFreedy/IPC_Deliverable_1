/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yuki
 */
@XmlRootElement
public class wrapper {
    private ArrayList<PC> arr;
    public wrapper(){}
    @XmlElement(name = "arr")
    public ArrayList<PC> getPCList(){
        return arr;
    }
    public void setPCList(ArrayList<PC> list){
        arr = list;
    }
}
