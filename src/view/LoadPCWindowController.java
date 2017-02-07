/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import es.upv.inf.Product.Category;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import model.Component;
import model.PC;
import model.wrapper;

/**
 * FXML Controller class
 *
 * @author Yuki
 */
public class LoadPCWindowController implements Initializable {

    @FXML
    private ChoiceBox<String> choiceChoiceBox;
    @FXML
    private TableView<Component> componentsTableView;
    @FXML
    private TableColumn<Component, Category> comType;
    @FXML
    private TableColumn<Component, String> comPro;
    @FXML
    private TableColumn<Component, Integer> comAmount;
    @FXML
    private TableColumn<Component, Double> comPrice;
    @FXML
    private Label totalPrice;
    
    private Stage stage;

    private wrapper marshed;
    
    private ObservableList<Component> addTableC;
    
    private int aux;
    @FXML
    private Button loadBut;
    
    private boolean isPreconfigured;
    
    private String whatIsLeft;
    
    /**
     * Initializes the controller class.
     */
    
    public void initData(Stage stage, boolean isPreconfigured){
        this.stage = stage;
        this.isPreconfigured = isPreconfigured;marshed = new wrapper();
        
        try {
            File file;
            if(isPreconfigured) file = new File("preconfigured.xml");
            else file = new File("savedData.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PC> choices = marshed.getPCList();
        List<String> ch = new ArrayList<>();
        for (int i = 0; i < choices.size(); i++) {
            ch.add(choices.get(i).getName());
        }
        
        ObservableList<String> value = FXCollections.observableArrayList(ch);
        
        choiceChoiceBox.setItems(value);
        
        choiceChoiceBox.setValue(marshed.getPCList().get(0).getName());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comAmount.setCellValueFactory(new PropertyValueFactory<Component, Integer>("stock"));
        comPrice.setCellValueFactory(new PropertyValueFactory<Component, Double>("price"));
        comPro.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        comType.setCellValueFactory(new PropertyValueFactory<Component, Category>("category"));
        
        
        
        choiceChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                for(int i = 0; i<marshed.getPCList().size(); i++){
                    if(marshed.getPCList().get(i).getName().equals(newValue)){
                        aux = i;
                        break;
                    }
                }
                ArrayList<Component> seter = new ArrayList<>();
                for(int i = 0; i<marshed.getPCList().get(aux).getList2().size();i++){
                    seter.add(marshed.getPCList().get(aux).getList2().get(i));
                }
                addTableC = FXCollections.observableArrayList(seter);
                componentsTableView.setItems(addTableC);
                recalculate();
            }
        });
        
    }    

    @FXML
    private void onLoad(ActionEvent event) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            MainWindowController dcCon = myLoader.<MainWindowController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("PC Builder");
            stage.setMinHeight(650);
            stage.setMinWidth(1050);
            stage.initModality(Modality.APPLICATION_MODAL);
            dcCon.reload(aux, marshed);
            dcCon.initData(dcCon, stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }        
        stage.hide();
    }

    @FXML
    private void onBuy(ActionEvent event) {
        if (!isCompleted()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("The Pc is not complete");
            alert.setContentText(whatIsLeft);
            alert.showAndWait();
            return;
        }
        try{
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/BillWindow.fxml"));
        Parent root = (Parent) myLoader.load();
        BillWindowController dcCon = myLoader.<BillWindowController>getController();
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setMinHeight(634);
        stage.setMinWidth(492);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        dcCon.initData(stage, addTableC, null);

        stage.show();
        this.stage.hide();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void recalculate(){
        double price = 0;
        for(int i = 0; i<addTableC.size();i++){
            price += addTableC.get(i).getPrice2();
        }
        totalPrice.setText(String.format("%.0f€", price));
    }
    
    public void disBut(){
        loadBut.setDisable(true);
    }
    
    private boolean isCompleted() {
        whatIsLeft = "This components are missing:\n";
        boolean[] trues = new boolean[15];
        for (int i = 0; i < addTableC.size(); i++) {
            trues[getIdentifier(addTableC.get(i).getCategoryI())] = true;
        }
        boolean aux = true;
        boolean itsAlready = false;
        aux = aux && (trues[1] || trues[2]);
        if (!aux) {
            whatIsLeft += "HDD / SSD\n";
        }
        for (int i = 2; i < trues.length; i++) {

            if (trues[i] == true) {
                switch (i) {
                    case 5:
                        aux = aux && trues[i];
                        break;
                    case 8:
                        aux = aux && trues[i];
                        break;
                    case 9:
                        aux = aux && trues[i];
                        break;
                    case 11:
                        aux = aux && trues[i];
                        break;
                    case 13:
                        aux = aux && trues[i];
                        break;
                    default:
                        break;
                }
            }
                if (trues[i] == false) {
                    switch (i) {
                        case 5:
                            whatIsLeft += "RAM\n";
                            break;
                        case 8:
                            whatIsLeft += "MOTHERBOARD\n";
                            break;
                        case 9:
                            whatIsLeft += "CPU\n";
                            break;
                        case 11:
                            whatIsLeft += "GPU\n";
                            break;
                        case 13:
                            whatIsLeft += "CASE\n";
                            break;
                        default:
                            break;
                    }
                }
            }
            return aux;
        }
    
    private int getIdentifier(Category cat) {
        if (cat.equals(Category.SPEAKER)) {
            return 0;
        }
        if (cat.equals(Category.HDD)) {
            return 1;
        }
        if (cat.equals(Category.HDD_SSD)) {
            return 2;
        }
        if (cat.equals(Category.POWER_SUPPLY)) {
            return 3;
        }
        if (cat.equals(Category.DVD_WRITER)) {
            return 4;
        }
        if (cat.equals(Category.RAM)) {
            return 5;
        }
        if (cat.equals(Category.SCREEN)) {
            return 6;
        }
        if (cat.equals(Category.MULTIREADER)) {
            return 7;
        }
        if (cat.equals(Category.MOTHERBOARD)) {
            return 8;
        }
        if (cat.equals(Category.CPU)) {
            return 9;
        }
        if (cat.equals(Category.MOUSE)) {
            return 10;
        }
        if (cat.equals(Category.GPU)) {
            return 11;
        }
        if (cat.equals(Category.KEYBOARD)) {
            return 12;
        }
        if (cat.equals(Category.CASE)) {
            return 13;
        }
        if (cat.equals(Category.FAN)) {
            return 14;
        } else {
            return 0;
        }
    }
}
