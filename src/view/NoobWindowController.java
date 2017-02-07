/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
public class NoobWindowController implements Initializable {

    @FXML
    private TableView<Component> choiceTableView;
    @FXML
    private Label motherboardLabel;
    @FXML
    private Label cpuLabel;
    @FXML
    private Label ramLabel;
    @FXML
    private Label gpuLabel;
    @FXML
    private Label hardDiskLabel;
    @FXML
    private Label caseLabel;
    @FXML
    private Button viewCart;
    @FXML
    private Button next;
    @FXML
    private Button buy;
    @FXML
    private Button back;
    @FXML
    private TableColumn<Component, String> nameColumn;
    @FXML
    private TableColumn<Component, Double> priceColoumn;

    private ObservableList<Component> addTableC;

    private wrapper wrap;

    private int state;

    private Stage stage;

    private int selected;

    private Stage staging;

    private NoobCartWindowController ncwc;

    private ArrayList<Component> array;

    private String[] descriptions;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        array = new ArrayList<>();
        descriptions = new String[6];
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/NoobCartWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            ncwc = myLoader.<NoobCartWindowController>getController();
            Scene scene = new Scene(root);
            staging = new Stage();
            staging.setScene(scene);
            staging.setTitle("Cart");
            staging.setResizable(false);
            staging.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ncwc.initData(staging);
        
        selected = 0;
        state = 0;
        wrap = new wrapper();
        try {
            File file = new File("pcwrapper.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(wrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            wrap = (wrapper) jaxbUnmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        refresh(state);

        priceColoumn.setCellValueFactory(new PropertyValueFactory<Component, Double>("price"));
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
    }

    @FXML
    private void onViewCart(ActionEvent event) {
        staging.hide();
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/NoobCartWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            ncwc = myLoader.<NoobCartWindowController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            ncwc.initData(stage, descriptions);
            stage.setTitle("Cart");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onNext(ActionEvent event) {
        if (choiceTableView.getSelectionModel().getSelectedIndex() == -1) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Not Item Selected");
            alert.setHeaderText("There is no item selected");
            alert.setContentText("Please, select an item before pressing next");
            alert.showAndWait();
            return;
        }

        if (state == array.size()) {
            array.add(choiceTableView.getSelectionModel().getSelectedItem());
        } else {
            array.set(state, choiceTableView.getSelectionModel().getSelectedItem());
        }
        ncwc.adding(state, choiceTableView.getSelectionModel().getSelectedItem().getDescription());
        descriptions[state] = choiceTableView.getSelectionModel().getSelectedItem().getDescription();
        state++;
        System.out.println(state);
        if (state != 6) {
            refresh(state);
        } else {
            buy.setDisable(false);
            next.setDisable(true);
            caseLabel.setDisable(true);
        }

    }

    @FXML
    private void onBuy(ActionEvent event) {
        wrapper wrapper = new wrapper();
        ArrayList<PC> pcs = new ArrayList<>();
        PC pc = new PC();
        pc.setList2(array);
        pcs.add(pc);
        wrapper.setPCList(pcs);
        stage.hide();
        ncwc.hidea();
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
            dcCon.initData(dcCon, stage);
            dcCon.reload2(0, wrapper);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onMenu(ActionEvent event) {
        this.stage.hide();
        this.staging.hide();
    }

    @FXML
    private void onBack(ActionEvent event) {
        state--;
        refresh(state);
    }

    public void refresh(int s) {
        if (s == 0) {
            back.setDisable(true);
        } else {
            back.setDisable(false);
        }
        if (s == 6) {
            next.setDisable(true);
            buy.setDisable(false);
        } else {
            next.setDisable(false);
            buy.setDisable(true);
        }

        switch (s) {
            case 0:
                block();
                motherboardLabel.setDisable(false);
                break;
            case 1:
                block();
                cpuLabel.setDisable(false);
                break;
            case 2:
                block();
                ramLabel.setDisable(false);
                break;
            case 3:
                block();
                gpuLabel.setDisable(false);
                break;
            case 4:
                block();
                hardDiskLabel.setDisable(false);
                break;
            case 5:
                block();
                caseLabel.setDisable(false);
                break;
            default:
                block();
                break;
        }

        int i = s * 3;
        ArrayList<Component> aux = wrap.getPCList().get(selected).getList2();

        ArrayList<Component> loQueMetes = new ArrayList<>();

        loQueMetes.add(aux.get(i));
        loQueMetes.add(aux.get(i + 1));
        loQueMetes.add(aux.get(i + 2));
        addTableC = FXCollections.observableArrayList(loQueMetes);

        choiceTableView.setItems(addTableC);
    }

    public void initData(Stage stage, int selected) {
        this.stage = stage;
        this.selected = selected;
        this.staging = staging;
    }

    private void block() {
        motherboardLabel.setDisable(true);
        cpuLabel.setDisable(true);
        ramLabel.setDisable(true);
        gpuLabel.setDisable(true);
        hardDiskLabel.setDisable(true);
        caseLabel.setDisable(true);
    }

    public void hidea() {
        ncwc.hidea();
    }
}
