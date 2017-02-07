/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import es.upv.inf.Product;
import es.upv.inf.Product.Category;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yuki
 */
@XmlRootElement
public class Component {

    private int quantity;

    private String descriptionI;
    private double priceI;
    private int identifier;
    private Category categoryI;

    private final StringProperty category = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final DoubleProperty price2 = new SimpleDoubleProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty available = new SimpleIntegerProperty();

    private boolean removable;

    public Component(Product product, int identifier) {
        this.identifier = identifier;
        descriptionI = product.getDescription();
        priceI = product.getPrice();
        categoryI = product.getCategory();

        this.category.setValue(product.getCategory().toString());
        this.description.setValue(product.getDescription());
        this.stock.setValue(quantity);
        this.price.setValue(product.getPrice());
        this.quantity = 1;
        double aux = product.getPrice();
        this.price2.setValue(aux);
        removable = true;
    }

    public Component(int quantity, Product product, int identifier) {
        this.quantity = quantity;

        this.identifier = identifier;

        descriptionI = product.getDescription();
        priceI = product.getPrice();
        categoryI = product.getCategory();

        this.category.setValue(product.getCategory().toString());
        this.description.setValue(product.getDescription());
        this.stock.setValue(quantity);
        this.price.setValue(product.getPrice());
        double aux = product.getPrice();
        this.price2.setValue(aux);
        removable = true;
    }

    public Component(int quantity, Product product, int identifier, boolean removable) {
        this.quantity = quantity;

        this.identifier = identifier;

        descriptionI = product.getDescription();
        priceI = product.getPrice();
        categoryI = product.getCategory();

        this.category.setValue(product.getCategory().toString());
        this.description.setValue(product.getDescription());
        this.stock.setValue(quantity);
        this.price.setValue(product.getPrice());
        double aux = product.getPrice();
        this.price2.setValue(aux);
        this.removable = removable;
    }

    public Component() {
        quantity = 1;
        identifier = 0;
        removable = true;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int q) {
        this.quantity = q;
    }

    public String getCategory() {
        return category.get();
    }

    public void getCategory(String c) {
        category.set(c);
    }

    public StringProperty categoryProperty() {
        return new SimpleStringProperty(categoryI.toString());
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String c) {
        description.set(c);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Double getPrice() {
        return price.get();
    }

    public void setPrice(Double c) {
        double aux = c * 100;
        int i = (int) aux;
        aux = (double) i;
        aux = aux / 100;
        price.set(aux);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public void setAvailable(int a) {
        this.available.setValue(a);
    }

    public int getAvailable() {
        return available.get();
    }

    public IntegerProperty availableProperty() {
        return available;
    }

    public void setCategoryI(Category a) {
        categoryI = a;
    }

    public Category getCategoryI() {
        return categoryI;
    }

    public void setDescriptionI(String a) {
        descriptionI = a;
    }

    public String getDescriptionI() {
        return descriptionI;
    }

    public void setPriceI(double a) {
        priceI = a;
    }

    public double getPriceI() {
        return priceI;
    }

    public int getIdentifier() {
        return identifier;
    }

    public double getPrice2() {
        return price2.get();
    }

    public void setPrice2(double c) {
        price2.set(c);
    }

    public DoubleProperty price2Property() {
        return price2;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isRemovable() {
        return this.removable;
    }
    
    public void setIdentifier(int id){
        identifier = id;
    }

    public void applyConfig(ArrayList<ArrayList<Integer>> ID, int category){
        available.set(ID.get(category).get(identifier));
        this.category.set(getCategory());
        this.description.set(descriptionI);
        this.price.set(priceI);
        this.price2.set(priceI/quantity);
    }
    
    public boolean getRemovable(){
        return this.removable;
    }
}
