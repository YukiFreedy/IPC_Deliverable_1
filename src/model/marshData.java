/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yuki
 */
@XmlRootElement
public class marshData {

    private boolean isZero;

    public marshData() {
        isZero = true;
    }

    public marshData(boolean isZero) {
        this.isZero = isZero;
    }
    
    public boolean getIsZero(){
        return isZero;
    }
    
    public void setIsZero(boolean isZero){
        this.isZero = isZero;
    }
}
