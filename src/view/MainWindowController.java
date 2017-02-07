/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import es.upv.inf.Database;
import es.upv.inf.Product;
import es.upv.inf.Product.Category;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import model.Component;
import model.PC;
import model.marshData;
import model.redo;
import model.undo;
import model.wrapper;

/**
 * FXML Controller class
 *
 * @author Yuki
 */
public class MainWindowController implements Initializable {

    @FXML
    private ChoiceBox<Object> searchCho;
    @FXML
    private TextField nameField;
    @FXML
    private TextField minPrice;
    @FXML
    private TextField maxPrice;
    @FXML
    private RadioButton stockBut;
    @FXML
    private Button remBut;
    @FXML
    private Button addBut;
    @FXML
    private TableView<Component> listTable;
    @FXML
    private TableColumn<Component, String> proPro;
    @FXML
    private TableColumn<Component, Double> proPrice;
    @FXML
    private TableColumn<Component, Integer> proAva;
    @FXML
    private TableView<Component> addTable;
    @FXML
    private TableColumn<Component, Category> comType;
    @FXML
    private TableColumn<Component, String> comPro;
    @FXML
    private TableColumn<Component, Double> comPrice;
    @FXML
    private TableColumn<Component, Integer> comAmount;

    private Database db;

    private ArrayList<Component> pc;

    private String name;

    private ObservableList<Component> listTableC;

    private ObservableList<Component> addTableC;

    private String input;

    @FXML
    private Label total;

    @FXML
    private TextField amountTF;

    private ArrayList<ArrayList<Integer>> ID;

    private PC MD;

    private undo undo;

    private redo redo;

    private boolean can;

    private wrapper marshed;

    private marshData erBool;

    private MainWindowController dcCon;

    private Stage stage;

    private String whatIsLeft;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = new Database();
        pc = new ArrayList<>();
        ID = new ArrayList<>();
        name = "";
        initID();
        undo = new undo();
        redo = new redo();
        can = false;
        addTableC = FXCollections.observableArrayList(pc);

        addTable.setItems(addTableC);

        searchCho.setValue(Category.CASE);

        searchCho.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                refresh((Category) newValue);
            }
        }
        );

        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refresh((Category) searchCho.getValue());
            }
        });

        minPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refresh((Category) searchCho.getValue());
            }
        });

        maxPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refresh((Category) searchCho.getValue());
            }
        });

        proPro.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        proPrice.setCellValueFactory(new PropertyValueFactory<Component, Double>("price2"));
        proAva.setCellValueFactory(new PropertyValueFactory<Component, Integer>("available"));

        comAmount.setCellValueFactory(new PropertyValueFactory<Component, Integer>("stock"));
        comPrice.setCellValueFactory(new PropertyValueFactory<Component, Double>("price"));
        comPro.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        comType.setCellValueFactory(new PropertyValueFactory<Component, Category>("category"));

        searchCho.setItems(FXCollections.observableArrayList(
                Category.CASE,
                Category.CPU, Category.MOTHERBOARD, Category.RAM,
                Category.GPU, Category.HDD, Category.HDD_SSD, new Separator(),
                Category.DVD_WRITER, Category.FAN,
                Category.KEYBOARD,
                Category.MOUSE, Category.MULTIREADER, Category.POWER_SUPPLY,
                Category.SCREEN, Category.SPEAKER
        ));
        refresh(Category.CASE);

        addBut.disableProperty().bind(
                Bindings.equal(-1,
                        listTable.getSelectionModel().selectedIndexProperty()));
        remBut.disableProperty().bind(
                Bindings.equal(-1,
                        addTable.getSelectionModel().selectedIndexProperty()));

        can = true;

        erBool = new marshData();

        try {
            File file = new File("auxi.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void clickOnNewPC(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New PC");
        dialog.setHeaderText("You are about to create a new PC");
        dialog.setContentText("Enter the PC name:");
        Optional<String> result = dialog.showAndWait();
        String auxName;
        if (result.isPresent()) {
            auxName = result.get();
            if (!auxName.equals("") && !addTableC.isEmpty()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Your actual PC is going to be erase!");
                alert.setContentText("Do you want to save it before it happens?");
                Optional<ButtonType> result2 = alert.showAndWait();
                if (result2.isPresent()) {
                    if (name.equals("")) {
                        TextInputDialog dialog2 = new TextInputDialog(""); // Default value
                        dialog2.setTitle("Save PC");
                        dialog2.setHeaderText("You are about to save a new PC");
                        dialog2.setContentText("Enter the PC name:");
                        Optional<String> result3 = dialog2.showAndWait();
                        if (result3.isPresent() && !result3.get().isEmpty()) {
                            name = result3.get();
                            save();
                            clear();
                            name = auxName;
                        }
                    }
                } else {
                    name = auxName;
                    clear();
                }
            }

        }

    }

    @FXML
    private void clickOnOpenPC(ActionEvent event) {
        try {
            File file = new File("auxi.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (erBool.getIsZero()) {
            return;
        }

        marshed = new wrapper();

        try {
            File file = new File("savedData.xml");
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

        ChoiceDialog<String> dialog = new ChoiceDialog<>(ch.get(0), ch);
        dialog.setTitle("Load PC");
        dialog.setHeaderText("Which PC you want to load?");
        dialog.setContentText("Choose a PC:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            int res = 0;
            for (int i = 0; i < choices.size(); i++) {
                if (choices.get(i).getName().equals(result.get())) {
                    res = i;
                    break;
                }
            }
            reload(res, marshed);
            refresh((Category) searchCho.getValue());
        }

    }

    @FXML
    private void onSave(ActionEvent event) {
        if (addTableC.isEmpty()) {
            return;
        }
        if (name.equals("")) {
            TextInputDialog dialog2 = new TextInputDialog(""); // Default value
            dialog2.setTitle("Save PC");
            dialog2.setHeaderText("You are about to save a new PC");
            dialog2.setContentText("Enter the PC name:");
            Optional<String> result3 = dialog2.showAndWait();
            if (result3.isPresent() && !result3.get().isEmpty()) {
                try {
                    File file = new File("auxi.xml");
                    JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!erBool.getIsZero()) {
                    name = result3.get();
                    if (isNotRepeated(result3.get())) {
                        save();
                        return;
                    }
                    System.out.println("I'm on it");
                    name = result3.get();
                    List<PC> choices = marshed.getPCList();
                    for (int i = 0; i < choices.size(); i++) {
                        if (choices.get(i).getName().equals(name)) {
                            save(i);
                            return;
                        }
                    }

                } else {
                    ArrayList<Component> list = new ArrayList<>();
                    for (int i = 0; i < addTableC.size(); i++) {
                        list.add(addTableC.get(i));
                    }
                    name = result3.get();
                    PC pcAux = new PC(name, list, ID);
                    ArrayList<PC> listPC = new ArrayList<>();
                    listPC.add(pcAux);
                    wrapper wrap = new wrapper();
                    wrap.setPCList(listPC);
                    try {
                        File file = new File("savedData.xml");
                        JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        jaxbMarshaller.marshal(wrap, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    erBool.setIsZero(false);
                    try {
                        File file = new File("auxi.xml");
                        JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
                        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        jaxbMarshaller.marshal(erBool, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            marshed = new wrapper();

            try {
                File file = new File("auxi.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (erBool.getIsZero()) {
                save();
                return;
            }
            try {
                File file = new File("savedData.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<PC> choices = marshed.getPCList();
            int id = -1;
            for (int i = 0; i < choices.size(); i++) {
                if (name.equals(choices.get(i).getName())) {
                    save(i);
                    return;
                }
            }
            if (id == -1) {
                save();
            }
        }
    }

    @FXML
    private void onSaveAs(ActionEvent event) {
        if (addTableC.isEmpty()) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(""); // Default value
        dialog.setTitle("Name Input");
        dialog.setHeaderText("You are about to create a new PC");
        dialog.setContentText("Enter the PC's name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            name = result.get();
            save();
        }
    }

    @FXML
    private void clickOnOpenPreconfiguredPC(ActionEvent event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("The Actual PC will be erased if it is not saved");
        alert.setContentText("Do you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            wrapper preconfigured = new wrapper();
            try {
                File file = new File("preconfigured.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                preconfigured = (wrapper) jaxbUnmarshaller.unmarshal(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<PC> choices = preconfigured.getPCList();
            List<String> ch = new ArrayList<>();
            for (int i = 0; i < choices.size(); i++) {
                ch.add(choices.get(i).getName());
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(ch.get(0), ch);
            dialog.setTitle("Load Preconfigured PC");
            dialog.setHeaderText("Which PC you want to load?");
            dialog.setContentText("Choose a PC:");
            Optional<String> results = dialog.showAndWait();

            if (results.isPresent()) {
                int res = 0;
                for (int i = 0; i < choices.size(); i++) {
                    if (choices.get(i).getName().equals(results.get())) {
                        res = i;
                        break;
                    }
                }
                reload(res, preconfigured);
                name = "";
            }
        }
    }

    @FXML
    private void undo(ActionEvent event
    ) {
        undo.toErase = true;
        if (undo.getAction().isEmpty()) {
            return;
        }
        String action = undo.getAction().get(undo.getComponent().size() - 1);
        System.out.println(action);
        redo.addRedo(action, undo.getIdentifier().get(undo.getIdentifier().size() - 1), undo.getComponent().get(undo.getComponent().size() - 1));
        if (action.equals("add")) {
            Component aux = undo.getComponent().get(undo.getComponent().size() - 1);
            aux.setAvailable(aux.getAvailable() + undo.getIdentifier().get(undo.getComponent().size() - 1));
            aux.setQuantity(aux.getQuantity() - undo.getIdentifier().get(undo.getComponent().size() - 1));
            aux.setStock(aux.getQuantity());
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) + undo.getIdentifier().get(undo.getAction().size() - 1));
        }
        if (action.equals("add+")) {
            Component aux = undo.getComponent().get(undo.getComponent().size() - 1);
            addTableC.remove(aux);
            aux.setAvailable(aux.getAvailable() + undo.getIdentifier().get(undo.getComponent().size() - 1));
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) + undo.getIdentifier().get(undo.getAction().size() - 1));
        }
        if (action.equals("rem")) {
            Component aux = undo.getComponent().get(undo.getComponent().size() - 1);
            aux.setAvailable(aux.getAvailable() - undo.getIdentifier().get(undo.getComponent().size() - 1));
            aux.setQuantity(aux.getQuantity() + undo.getIdentifier().get(undo.getComponent().size() - 1));
            aux.setStock(aux.getQuantity());
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) - undo.getIdentifier().get(undo.getAction().size() - 1));
        }
        if (action.equals("rem+")) {
            Component aux = undo.getComponent().get(undo.getComponent().size() - 1);
            addTableC.add(aux);
            aux.setAvailable(aux.getAvailable() - undo.getIdentifier().get(undo.getComponent().size() - 1));
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) - undo.getIdentifier().get(undo.getAction().size() - 1));
        }
        undo.delete();
    }

    @FXML
    private void redo(ActionEvent event
    ) {
        if (undo.toErase == true) {
            undo.toErase = false;
            undo = new undo();
        }
        redo.toErase = true;
        if (redo.getAction().isEmpty()) {
            return;
        }
        String action = redo.getAction().get(redo.getComponent().size() - 1);
        if (action.equals("rem")) {
            Component aux = redo.getComponent().get(redo.getComponent().size() - 1);
            aux.setAvailable(aux.getAvailable() + redo.getIdentifier().get(redo.getComponent().size() - 1));
            aux.setQuantity(aux.getQuantity() - redo.getIdentifier().get(redo.getComponent().size() - 1));
            aux.setStock(aux.getQuantity());
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) + redo.getIdentifier().get(redo.getAction().size() - 1));
        }
        if (action.equals("rem+")) {
            Component aux = redo.getComponent().get(redo.getComponent().size() - 1);
            addTableC.remove(aux);
            aux.setAvailable(aux.getAvailable() + redo.getIdentifier().get(redo.getComponent().size() - 1));
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) + redo.getIdentifier().get(redo.getAction().size() - 1));
        }
        if (action.equals("add")) {
            Component aux = redo.getComponent().get(redo.getComponent().size() - 1);
            aux.setAvailable(aux.getAvailable() - redo.getIdentifier().get(redo.getComponent().size() - 1));
            aux.setQuantity(aux.getQuantity() + redo.getIdentifier().get(redo.getComponent().size() - 1));
            aux.setStock(aux.getQuantity());
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) - redo.getIdentifier().get(redo.getAction().size() - 1));
        }
        if (action.equals("add+")) {
            Component aux = redo.getComponent().get(redo.getComponent().size() - 1);
            addTableC.add(aux);
            aux.setAvailable(aux.getAvailable() - redo.getIdentifier().get(redo.getComponent().size() - 1));
            ArrayList<Integer> list = ID.get(getIdentifier((Category) aux.getCategoryI()));
            list.set(aux.getIdentifier(), list.get(aux.getIdentifier()) - redo.getIdentifier().get(redo.getAction().size() - 1));
        }
        redo.delete();
    }

    @FXML
    private void clearCart(ActionEvent event
    ) {
        //confirmation window
        while (!addTableC.isEmpty()) {
            ArrayList<Integer> list = ID.get(getIdentifier((Category) addTableC.get(0).getCategoryI()));
            int identifier = addTableC.get(0).getIdentifier();
            list.set(identifier, list.get(identifier) + addTableC.get(0).getStock());
            renew4Clear(list, identifier, addTableC.get(0));
            addTableC.remove(0);
        }
    }

    @FXML
    private void clickOnHelp(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Start Guide");
        alert.setHeaderText("Your Actual Pc is going to be erased");
        alert.setContentText("Do you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
        } else {
            return;
        }
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/ChoicePCWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            ChoicePCWindowController dcCon = myLoader.<ChoicePCWindowController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Load Preconfigured PC");
            stage.initModality(Modality.APPLICATION_MODAL);
            dcCon.initData(stage);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.stage.hide();
    }

    private void refresh(Category cat) {
        ArrayList<Component> aux = new ArrayList<>();
        List<Product> aux2 = new ArrayList<>();
        db = new Database();
        if (nameField.getText().isEmpty()) {
            if (minPrice.getText().isEmpty() && maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryAndPrice((Category) searchCho.getValue(), 0, 10000, stockBut.isPressed());
            }
            if (!minPrice.getText().isEmpty() && maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryAndPrice((Category) searchCho.getValue(), Double.parseDouble(minPrice.getText()), 10000, stockBut.isPressed());
            }
            if (minPrice.getText().isEmpty() && !maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryAndPrice((Category) searchCho.getValue(), 0, Double.parseDouble(maxPrice.getText()), stockBut.isPressed());
            }
            if (!minPrice.getText().isEmpty() && !maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryAndPrice((Category) searchCho.getValue(), Double.parseDouble(minPrice.getText()), Double.parseDouble(maxPrice.getText()), stockBut.isPressed());
            }
        } else {
            if (minPrice.getText().isEmpty() && maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryAndDescription((Category) searchCho.getValue(), nameField.getText(), stockBut.isPressed());
            }
            if (!minPrice.getText().isEmpty() && maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryDescriptionAndPrice((Category) searchCho.getValue(), nameField.getText(), Double.parseDouble(minPrice.getText()), 10000, stockBut.isPressed());
            }
            if (minPrice.getText().isEmpty() && !maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryDescriptionAndPrice((Category) searchCho.getValue(), nameField.getText(), 0, Double.parseDouble(maxPrice.getText()), stockBut.isPressed());
            }
            if (!minPrice.getText().isEmpty() && !maxPrice.getText().isEmpty()) {
                aux2 = db.getProductByCategoryDescriptionAndPrice((Category) searchCho.getValue(), nameField.getText(), Double.parseDouble(minPrice.getText()), Double.parseDouble(maxPrice.getText()), stockBut.isPressed());
            }
        }

        for (int i = 0; i < aux2.size(); i++) {
            aux.add(new Component(1, aux2.get(i), i));
            aux.get(i).setAvailable(ID.get(getIdentifier(cat)).get(aux.get(i).getIdentifier()));
        }

        //initID();
        listTableC = FXCollections.observableList(aux);
        listTable.setItems(listTableC);
        if (can && stockBut.isSelected()) {
            justAvailables();
        }
    }

    @FXML
    private void onRemove(ActionEvent event) {
        if (stockBut.isSelected()) {
            fAvailables();
        }
        Component aux = addTable.getSelectionModel().getSelectedItem();
        if (amountTF.getText().equals("")) {
            amountTF.setText("1");
        }
        if (!aux.isRemovable() && aux.getQuantity() <= Integer.parseInt(amountTF.getText())) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Important Item");
            alert.setHeaderText("It is an important Component");
            alert.setContentText("Do you really want to delete it?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (undo.toErase == true) {
                    undo.toErase = false;
                    undo = new undo();
                }
                if (redo.toErase == true) {
                    redo.toErase = false;
                    redo = new redo();
                }

                if (aux.getStock() - Integer.parseInt(amountTF.getText()) <= 0) {
                    undo.addUndo("rem+", (Integer.parseInt(amountTF.getText())), aux);
                    ArrayList<Integer> list = ID.get(getIdentifier(aux.getCategoryI()));
                    int identifier = addTable.getSelectionModel().getSelectedItem().getIdentifier();
                    list.set(identifier, list.get(addTable.getSelectionModel().getSelectedItem().getIdentifier()) + aux.getStock());
                    if (aux.getCategoryI().equals((Category) searchCho.getValue())) {
                        renew2(list, identifier);
                    }
                    aux.setRemovable(true);
                    addTableC.remove(aux);
                } else {
                    undo.addUndo("rem", (Integer.parseInt(amountTF.getText())), aux);
                    int i = addTable.getSelectionModel().getSelectedIndex();
                    addTableC.get(i).setQuantity(addTableC.get(i).getQuantity() - Integer.parseInt(amountTF.getText()));
                    addTableC.get(i).setStock(addTableC.get(i).getQuantity());
                    addTableC.get(i).setPrice(addTableC.get(i).getPrice2() * addTableC.get(i).getQuantity());
                    //ID.get(getIdentifier((Category) searchCho.getValue())).get(addTable.getSelectionModel().getSelectedItem().getIdentifier());
                    ArrayList<Integer> list = ID.get(getIdentifier(aux.getCategoryI()));
                    int identifier = addTable.getSelectionModel().getSelectedItem().getIdentifier();
                    list.set(identifier, list.get(addTable.getSelectionModel().getSelectedItem().getIdentifier()) + Integer.parseInt(amountTF.getText()));
                    if (aux.getCategoryI().equals((Category) searchCho.getValue())) {
                        renew2(list, identifier);
                    }
                }
            }
        } else {
            if (undo.toErase == true) {
                undo.toErase = false;
                undo = new undo();
            }
            if (redo.toErase == true) {
                redo.toErase = false;
                redo = new redo();
            }

            if (aux.getStock() - Integer.parseInt(amountTF.getText()) <= 0) {
                undo.addUndo("rem+", (Integer.parseInt(amountTF.getText())), aux);
                ArrayList<Integer> list = ID.get(getIdentifier(aux.getCategoryI()));
                int identifier = addTable.getSelectionModel().getSelectedItem().getIdentifier();
                list.set(identifier, list.get(addTable.getSelectionModel().getSelectedItem().getIdentifier()) + aux.getStock());
                if (aux.getCategoryI().equals((Category) searchCho.getValue())) {
                    renew2(list, identifier);
                }
                addTableC.remove(aux);
            } else {
                undo.addUndo("rem", (Integer.parseInt(amountTF.getText())), aux);
                int i = addTable.getSelectionModel().getSelectedIndex();
                addTableC.get(i).setQuantity(addTableC.get(i).getQuantity() - Integer.parseInt(amountTF.getText()));
                addTableC.get(i).setStock(addTableC.get(i).getQuantity());
                addTableC.get(i).setPrice(addTableC.get(i).getPrice2() * addTableC.get(i).getQuantity());
                //ID.get(getIdentifier((Category) searchCho.getValue())).get(addTable.getSelectionModel().getSelectedItem().getIdentifier());
                ArrayList<Integer> list = ID.get(getIdentifier(aux.getCategoryI()));
                int identifier = addTable.getSelectionModel().getSelectedItem().getIdentifier();
                list.set(identifier, list.get(addTable.getSelectionModel().getSelectedItem().getIdentifier()) + Integer.parseInt(amountTF.getText()));
                if (aux.getCategoryI().equals((Category) searchCho.getValue())) {
                    renew2(list, identifier);
                }
            }
        }
        if (can == false) {
            can = true;
            stockBut.setSelected(true);
        }
        refreshTotal();
        justAvailables();
    }

    @FXML
    private void onAdd(ActionEvent event) {
        if (undo.toErase == true) {
            undo.toErase = false;
            undo = new undo();
        }
        if (redo.toErase == true) {
            redo.toErase = false;
            redo = new redo();
        }
        if (amountTF.getText().equals("")) {
            amountTF.setText("1");
        }
        Component aux = listTable.getSelectionModel().getSelectedItem();
        if (aux.getAvailable() - Integer.parseInt(amountTF.getText()) < 0) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("No Enough Stock!");
            alert.setContentText("There are not enough avaiable items for this product");
            alert.showAndWait();
            return;
        }
        ArrayList<Component> as = new ArrayList<>();
        for (int i = 0; i < addTableC.size(); i++) {
            as.add(addTableC.get(i));
        }
        boolean bool = false;
        int i = 0;
        while (!as.isEmpty()) {
            if (aux.getDescriptionI().equals(as.get(0).getDescriptionI())) {
                bool = true;
                break;
            } else {
                i++;
                as.remove(0);
            }
        }
        if (bool) {
            undo.addUndo("add", (Integer.parseInt(amountTF.getText())), aux);
            addTableC.get(i).setQuantity(addTableC.get(i).getQuantity() + Integer.parseInt(amountTF.getText()));
            addTableC.get(i).setStock(addTableC.get(i).getQuantity());
            addTableC.get(i).setPrice(addTableC.get(i).getPrice2() * addTableC.get(i).getQuantity());
            ArrayList<Integer> list = ID.get(getIdentifier((Category) searchCho.getValue()));
            list.set(listTable.getSelectionModel().getSelectedItem().getIdentifier(), list.get(listTable.getSelectionModel().getSelectedItem().getIdentifier()) - Integer.parseInt(amountTF.getText()));
            renew(list, listTable.getSelectionModel().getSelectedItem().getIdentifier());
        } else {
            undo.addUndo("add+", (Integer.parseInt(amountTF.getText())), aux);
            aux.setStock(Integer.parseInt(amountTF.getText()));
            aux.setPrice(aux.getPrice2() * Integer.parseInt(amountTF.getText()));
            addTableC.add(aux);
            ArrayList<Integer> list = ID.get(getIdentifier((Category) searchCho.getValue()));
            list.set(listTable.getSelectionModel().getSelectedItem().getIdentifier(), list.get(listTable.getSelectionModel().getSelectedItem().getIdentifier()) - Integer.parseInt(amountTF.getText()));
            renew(list, listTable.getSelectionModel().getSelectedItem().getIdentifier());
        }
        refreshTotal();
        if (stockBut.isSelected()) {
            justAvailables();
        }
    }

    @FXML
    private void onBuy(ActionEvent event) {
        if (!isCompleted()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("The Pc is not complete");
            alert.setContentText(whatIsLeft);
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/BillWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            BillWindowController dcCon = myLoader.<BillWindowController>getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setMinHeight(634);
            stage.setMinWidth(492);
            dcCon.initData(stage, addTableC, this.dcCon);
            stage.show();
        } catch (Exception e) {

        }
    }

    private void refreshTotal() {
        double aux = 0;
        for (int i = 0; i < addTableC.size(); i++) {
            aux += addTableC.get(i).getPrice();
        }

        aux = (int) (aux * 100);
        aux = aux / 100;

        total.setText(Double.toString(aux));
    }

    private void clear() {
        for (int i = 0; i < addTableC.size(); i++) {
            Component com = addTableC.get(i);
            com.setAvailable(com.getAvailable() - com.getQuantity());
            ID.get(getIdentifier(com.getCategoryI())).set(com.getIdentifier(), com.getAvailable());
            com.setQuantity(0);
            com.setStock(com.getQuantity());
        }
        refreshTotal();
    }

    private void save(int id) {
        wrapper wrap = new wrapper();
        try {
            File file = new File("auxi.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (erBool.getIsZero()) {
            ArrayList<Component> list = new ArrayList<>();
            for (int i = 0; i < addTableC.size(); i++) {
                list.add(addTableC.get(i));
            }

            PC pcAux = new PC(name, list, ID);
            ArrayList<PC> listPC = new ArrayList<>();
            listPC.add(pcAux);
            wrap.setPCList(listPC);
        } else {
            marshed = new wrapper();

            try {
                File file = new File("savedData.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<PC> choices = marshed.getPCList();

            ArrayList<Component> list = new ArrayList<>();
            for (int i = 0; i < addTableC.size(); i++) {
                list.add(addTableC.get(i));
            }

            PC pcAux = new PC(name, list, ID);

            choices.set(id, pcAux);

            wrap.setPCList(choices);
        }
        try {
            File file = new File("savedData.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(wrap, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        wrapper wrap = new wrapper();
        try {
            File file = new File("auxi.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (erBool.getIsZero()) {
            ArrayList<Component> list = new ArrayList<>();
            for (int i = 0; i < addTableC.size(); i++) {
                list.add(addTableC.get(i));
            }

            PC pcAux = new PC(name, list, ID);
            ArrayList<PC> listPC = new ArrayList<>();
            listPC.add(pcAux);
            wrap.setPCList(listPC);
        } else {
            marshed = new wrapper();

            try {
                File file = new File("savedData.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<PC> choices = marshed.getPCList();

            ArrayList<Component> list = new ArrayList<>();
            for (int i = 0; i < addTableC.size(); i++) {
                list.add(addTableC.get(i));
            }
            PC pcAux = new PC(name, list, ID);

            choices.add(pcAux);

            wrap.setPCList(choices);
        }
        try {
            File file = new File("savedData.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(wrap, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (erBool.getIsZero()) {
            erBool.setIsZero(false);
            try {
                File file = new File("auxi.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(erBool, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void onPlus(ActionEvent event) {
        if (amountTF.getText().equals("")) {
            amountTF.setText("1");
        }
        amountTF.setText(Integer.toString(Integer.parseInt(amountTF.getText()) + 1));
    }

    @FXML
    private void onMinus(ActionEvent event) {
        if (amountTF.getText().equals("")) {
            amountTF.setText("1");
        }
        if (amountTF.getText().equals("1") || amountTF.getText().equals("")) {
            return;
        }
        amountTF.setText(Integer.toString(Integer.parseInt(amountTF.getText()) - 1));
    }

    private void initID() {
        ID = new ArrayList<>();
        if (MD == null) {
            ArrayList<Integer> aux = new ArrayList<>();
            List<Product> pr;
            pr = db.getProductByCategory(Category.SPEAKER);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.HDD);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.HDD_SSD);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.POWER_SUPPLY);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.DVD_WRITER);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.RAM);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.SCREEN);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.MULTIREADER);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.MOTHERBOARD);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.CPU);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.MOUSE);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.GPU);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.KEYBOARD);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.CASE);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
            aux = new ArrayList<>();
            pr = db.getProductByCategory(Category.FAN);
            for (int i = 0; i < pr.size(); i++) {
                aux.add(pr.get(i).getStock());
            }
            ID.add(aux);
        } else {
            ID = MD.gimmeID();
        }
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

    private void renew(ArrayList<Integer> list, int identifier) {
        int i = list.get(identifier);
        listTable.getSelectionModel().getSelectedItem().setAvailable(i);
    }

    private void renew2(ArrayList<Integer> list, int identifier) {
        int i = list.get(identifier);
        for (int s = 0; i < listTableC.size(); s++) {
            if (listTableC.get(s).getIdentifier() == identifier) {
                listTableC.get(s).setAvailable(i);
                break;
            }
        }
    }

    @FXML
    private void onNumber(KeyEvent event) {
        input = event.getCharacter();
        if (!"0123456789".contains(event.getCharacter())) {
            event.consume();
        }
    }

    private void renew4Clear(ArrayList<Integer> list, int identifier, Component component) {
        int i = list.get(identifier);
        component.setAvailable(i);
    }

    @FXML
    private void onJustAvailables(ActionEvent event) {
        justAvailables();
    }

    private void justAvailables() {
        if (stockBut.isSelected()) {
            ArrayList<Component> aux = new ArrayList<>();
            for (int i = 0; i < listTableC.size(); i++) {
                int id = listTableC.get(i).getIdentifier();
                if (ID.get(getIdentifier((Category) searchCho.getValue())).get(id) != 0) {
                    aux.add(listTableC.get(i));
                }
            }
            listTableC = FXCollections.observableArrayList(aux);
            listTable.setItems(listTableC);
        } else {
            refresh((Category) searchCho.getValue());
        }
    }

    private void fAvailables() {
        if (!stockBut.isSelected()) {
            return;
        }
        stockBut.setSelected(false);
        can = false;
        justAvailables();
    }

    public void reload(int i, wrapper marshed) {
        ArrayList<PC> pcs = marshed.getPCList();
        ArrayList<Component> compi = pcs.get(i).getList2();
        this.name = pcs.get(i).getName();
        this.ID = pcs.get(i).gimmeID();
        if (ID == null) {
            initID();
        }
        ObservableList<Component> obser = FXCollections.observableArrayList();
        for (int s = 0; s < compi.size(); s++) {
            searchCho.setValue(compi.get(s).getCategoryI());
            refresh((Category) searchCho.getValue());
            for (int j = 0; j < listTableC.size(); j++) {
                if (listTableC.get(j).getDescriptionI().equals(compi.get(s).getDescriptionI())) {
                    Component comp = listTableC.get(j);
                    comp.setAvailable(compi.get(s).getAvailable());
                    comp.setIdentifier(compi.get(s).getIdentifier());
                    comp.setQuantity(compi.get(s).getQuantity());
                    comp.setPrice(compi.get(s).getPrice());
                    comp.setPrice2(compi.get(s).getPrice2());
                    comp.setPriceI(compi.get(s).getPriceI());
                    comp.setStock(compi.get(s).getStock());
                    comp.setRemovable(compi.get(s).getRemovable());
                    obser.add(comp);
                    break;
                }
            }
        }
        addTableC = obser;
        addTable.setItems(addTableC);
        refresh((Category) searchCho.getValue());
        refreshTotal();
    }

    public void reload2(int i, wrapper marshed) {
        ArrayList<PC> pcs = marshed.getPCList();
        ArrayList<Component> compi = pcs.get(i).getList2();
        this.name = pcs.get(i).getName();
        this.ID = null;
        if (ID == null) {
            initID();
        }
        ObservableList<Component> obser = FXCollections.observableArrayList();
        for (int s = 0; s < compi.size(); s++) {
            searchCho.setValue(compi.get(s).getCategoryI());
            refresh((Category) searchCho.getValue());
            for (int j = 0; j < listTableC.size(); j++) {
                if (listTableC.get(j).getDescriptionI().equals(compi.get(s).getDescriptionI())) {
                    Component comp = listTableC.get(j);
                    comp.setAvailable(compi.get(s).getAvailable());
                    comp.setIdentifier(compi.get(s).getIdentifier());
                    comp.setQuantity(compi.get(s).getQuantity());
                    comp.setPrice(compi.get(s).getPrice());
                    comp.setPrice2(compi.get(s).getPrice2());
                    comp.setPriceI(compi.get(s).getPriceI());
                    comp.setStock(compi.get(s).getStock());
                    comp.setRemovable(compi.get(s).getRemovable());
                    obser.add(comp);
                    break;
                }
            }
        }
        addTableC = obser;
        addTable.setItems(addTableC);
        refresh((Category) searchCho.getValue());
        refreshTotal();
    }

    private boolean isNotRepeated(String name) {
        marshed = new wrapper();

        try {
            File file = new File("savedData.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PC> choices = marshed.getPCList();
        for (int i = 0; i < choices.size(); i++) {
            if (choices.get(i).getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    @FXML
    private void onRemovePC(ActionEvent event) {
        try {
            File file = new File("auxi.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (erBool.getIsZero()) {
            return;
        }

        marshed = new wrapper();

        try {
            File file = new File("savedData.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<PC> pclist = marshed.getPCList();
        ArrayList<String> ch = new ArrayList<>();
        for (int i = 0; i < pclist.size(); i++) {
            ch.add(pclist.get(i).getName());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(ch.get(0), ch);
        dialog.setTitle("Remove PC");
        dialog.setHeaderText("Which PC you want to remove?");
        dialog.setContentText("Choose a PC (it cannot be undone):");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            int res = 0;
            for (int i = 0; i < pclist.size(); i++) {
                if (pclist.get(i).getName().equals(result.get())) {
                    res = i;
                    break;
                }
            }
            pclist.remove(res);
            wrapper wrap = new wrapper();
            wrap.setPCList(pclist);

            if (pclist.isEmpty()) {
                erBool.setIsZero(true);
                try {
                    File file = new File("auxi.xml");
                    JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(erBool, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                name = "";
            }

            try {
                File file = new File("savedData.xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(wrap, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void onNameChange(ActionEvent event) {
        try {
            File file = new File("auxi.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(marshData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            erBool = (marshData) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (erBool.getIsZero()) {
            return;
        }

        marshed = new wrapper();

        try {
            File file = new File("savedData.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            marshed = (wrapper) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<PC> pclist = marshed.getPCList();
        ArrayList<String> ch = new ArrayList<>();
        for (int i = 0; i < pclist.size(); i++) {
            ch.add(pclist.get(i).getName());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(ch.get(0), ch);
        dialog.setTitle("Change Name");
        dialog.setHeaderText("Which PC you want to change its name?");
        dialog.setContentText("Select the PC you want to change the name:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            TextInputDialog dialogs = new TextInputDialog("");
            dialog.setTitle("Change Name");
            dialog.setContentText("Input the new name of the PC:");
            Optional<String> results = dialogs.showAndWait();

            if (results.isPresent()) {
                int res = 0;
                for (int i = 0; i < pclist.size(); i++) {
                    if (pclist.get(i).getName().equals(result.get())) {
                        res = i;
                        break;
                    }
                }
                if (pclist.get(res).getName().equals(this.name)) {
                    name = results.get();
                }
                pclist.get(res).setName(results.get());
                wrapper wrap = new wrapper();
                wrap.setPCList(pclist);
                try {
                    File file = new File("savedData.xml");
                    JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(wrap, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void clickOnAddPreconfiguredPC(ActionEvent event) {

        wrapper preconfigured = new wrapper();
        try {
            File file = new File("preconfigured.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            preconfigured = (wrapper) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PC> choices = preconfigured.getPCList();
        List<String> ch = new ArrayList<>();
        for (int i = 0; i < choices.size(); i++) {
            ch.add(choices.get(i).getName());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(ch.get(0), ch);
        dialog.setTitle("Load Preconfigured PC");
        dialog.setHeaderText("Which PC you want to load?");
        dialog.setContentText("Choose a PC:");
        Optional<String> results = dialog.showAndWait();

        if (results.isPresent()) {
            int res = 0;
            for (int i = 0; i < choices.size(); i++) {
                if (choices.get(i).getName().equals(results.get())) {
                    res = i;
                    break;
                }
            }
            ArrayList<PC> pcs = preconfigured.getPCList();
            ArrayList<Component> compi = pcs.get(res).getList2();
            //this.name = pcs.get(res).getName();
            this.ID = pcs.get(res).gimmeID();
            ObservableList<Component> obser = FXCollections.observableArrayList();
            for (int s = 0; s < compi.size(); s++) {
                boolean done = false;
                searchCho.setValue(compi.get(s).getCategoryI());
                refresh((Category) searchCho.getValue());
                for (int i = 0; i < addTableC.size(); i++) {
                    if (addTableC.get(i).getDescription().equals(compi.get(s))) {
                        done = true;
                        Component comp = addTableC.get(i);
                        comp.setAvailable(compi.get(s).getAvailable() - comp.getQuantity());
                        comp.setIdentifier(compi.get(s).getIdentifier());
                        comp.setQuantity(compi.get(s).getQuantity() + comp.getQuantity());
                        comp.setPrice(compi.get(s).getPrice());
                        comp.setPrice2(compi.get(s).getPrice2() * comp.getQuantity());
                        comp.setPriceI(compi.get(s).getPriceI());
                        comp.setStock(compi.get(s).getStock());
                        comp.setRemovable(compi.get(s).getRemovable());
                        break;
                    }
                }
                if (!done) {
                    for (int j = 0; j < listTableC.size(); j++) {
                        if (listTableC.get(j).getDescriptionI().equals(compi.get(s).getDescriptionI())) {
                            Component comp = listTableC.get(j);
                            comp.setAvailable(compi.get(s).getAvailable());
                            comp.setIdentifier(compi.get(s).getIdentifier());
                            comp.setQuantity(compi.get(s).getQuantity());
                            comp.setPrice(compi.get(s).getPrice());
                            comp.setPrice2(compi.get(s).getPrice2());
                            comp.setPriceI(compi.get(s).getPriceI());
                            comp.setStock(compi.get(s).getStock());
                            comp.setRemovable(compi.get(s).getRemovable());
                            obser.add(comp);
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < obser.size(); i++) {
                addTableC.add(obser.get(i));
            }
            refresh((Category) searchCho.getValue());
            refreshTotal();

        }
    }

    public void initData(MainWindowController dcCon, Stage stage) {
        this.dcCon = dcCon;
        this.stage = stage;
    }

    public void hidea() {
        this.stage.hide();
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
}
