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
public class PC {

    private String name;
    private ArrayList<Component> list2;
    private ArrayList<Integer> ID1;
    private ArrayList<Integer> ID2;
    private ArrayList<Integer> ID3;
    private ArrayList<Integer> ID4;
    private ArrayList<Integer> ID5;
    private ArrayList<Integer> ID6;
    private ArrayList<Integer> ID7;
    private ArrayList<Integer> ID8;
    private ArrayList<Integer> ID9;
    private ArrayList<Integer> ID10;
    private ArrayList<Integer> ID11;
    private ArrayList<Integer> ID12;
    private ArrayList<Integer> ID13;
    private ArrayList<Integer> ID14;
    private ArrayList<Integer> ID15;

    public PC(String opText, ArrayList<Component> list, ArrayList<ArrayList<Integer>> ID) {
        this.list2 = new ArrayList<>();
        ID1 = ID.get(0);
        ID2 = ID.get(1);
        ID3 = ID.get(2);
        ID4 = ID.get(3);
        ID5 = ID.get(4);
        ID6 = ID.get(5);
        ID7 = ID.get(6);
        ID8 = ID.get(7);
        ID9 = ID.get(8);
        ID10 = ID.get(9);
        ID11 = ID.get(10);
        ID12 = ID.get(11);
        ID13 = ID.get(12);
        ID14 = ID.get(13);
        ID15 = ID.get(14);

        this.name = opText;
        this.list2 = list;
    }

    public PC() {
        name = null;
        this.list2 = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public ArrayList<Component> getList2() {
        return list2;
    }

    public void setList2(ArrayList<Component> a) {
        this.list2 = a;
    }

    public ArrayList<ArrayList<Integer>> gimmeID() {
        ArrayList<ArrayList<Integer>> aux = new ArrayList<>();
        aux.add(ID1);
        aux.add(ID2);
        aux.add(ID3);
        aux.add(ID4);
        aux.add(ID5);
        aux.add(ID6);
        aux.add(ID7);
        aux.add(ID8);
        aux.add(ID9);
        aux.add(ID10);
        aux.add(ID11);
        aux.add(ID12);
        aux.add(ID13);
        aux.add(ID14);
        aux.add(ID15);
        return aux;
    }
    

    public ArrayList<Integer> getID1() {
        return ID1;
    }

    public ArrayList<Integer> getID2() {
        return ID2;
    }

    public ArrayList<Integer> getID3() {
        return ID3;
    }

    public ArrayList<Integer> getID4() {
        return ID4;
    }

    public ArrayList<Integer> getID5() {
        return ID5;
    }

    public ArrayList<Integer> getID6() {
        return ID6;
    }

    public ArrayList<Integer> getID7() {
        return ID7;
    }

    public ArrayList<Integer> getID8() {
        return ID8;
    }

    public ArrayList<Integer> getID9() {
        return ID9;
    }

    public ArrayList<Integer> getID10() {
        return ID10;
    }

    public ArrayList<Integer> getID11() {
        return ID11;
    }

    public ArrayList<Integer> getID12() {
        return ID12;
    }

    public ArrayList<Integer> getID13() {
        return ID13;
    }

    public ArrayList<Integer> getID14() {
        return ID14;
    }

    public ArrayList<Integer> getID15() {
        return ID15;
    }

    public void setID1(ArrayList<Integer> ID1) {
        this.ID1 = ID1;
    }

    public void setID2(ArrayList<Integer> ID2) {
        this.ID2 = ID2;
    }

    public void setID3(ArrayList<Integer> ID3) {
        this.ID3 = ID3;
    }

    public void setID4(ArrayList<Integer> ID4) {
        this.ID4 = ID4;
    }

    public void setID5(ArrayList<Integer> ID5) {
        this.ID5 = ID5;
    }

    public void setID6(ArrayList<Integer> ID6) {
        this.ID6 = ID6;
    }

    public void setID7(ArrayList<Integer> ID7) {
        this.ID7 = ID7;
    }

    public void setID8(ArrayList<Integer> ID8) {
        this.ID8 = ID8;
    }

    public void setID9(ArrayList<Integer> ID9) {
        this.ID9 = ID9;
    }

    public void setID10(ArrayList<Integer> ID10) {
        this.ID10 = ID10;
    }

    public void setID11(ArrayList<Integer> ID11) {
        this.ID11 = ID11;
    }

    public void setID12(ArrayList<Integer> ID12) {
        this.ID12 = ID12;
    }

    public void setID13(ArrayList<Integer> ID13) {
        this.ID13 = ID13;
    }

    public void setID14(ArrayList<Integer> ID14) {
        this.ID14 = ID14;
    }

    public void setID15(ArrayList<Integer> ID15) {
        this.ID15 = ID15;
    }
    
}
