/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Yuki
 */
public class redo {
    public boolean toErase;
    
    private ArrayList<String> action;
    private ArrayList<Integer> identifier;
    private ArrayList<Component> component;
    
    public redo(){
        action = new ArrayList<>();
        identifier = new ArrayList<>();
        component = new ArrayList<>();
        toErase = false;
    }
    
    public ArrayList<String> getAction(){
        return this.action;
    }
    
    public void setAction(ArrayList<String> action){
        this.action = action;
    }
    
    public ArrayList<Integer> getIdentifier(){
        return identifier;
    }
    
    public void setIdentifier(ArrayList<Integer> identifier){
        this.identifier = identifier;
    }
    
    public ArrayList<Component> getComponent(){
        return this.component;
    }
    
    public void setComponent(ArrayList<Component> component){
        this.component = component;
    }
    
    public void addRedo(String action, Integer inte, Component component){
       this.action.add(action);
       this.component.add(component);
       this.identifier.add(inte);
    }
    
    public void delete(){
        int index = action.size()-1;
        action.remove(index);
        component.remove(index);
        identifier.remove(index);
    }
}
