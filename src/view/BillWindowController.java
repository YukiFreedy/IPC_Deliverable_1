/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import es.upv.inf.Product.Category;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Component;

/**
 * FXML Controller class
 *
 * @author Yuki
 */
public class BillWindowController implements Initializable {

    @FXML
    private Label totalSinTax;
    @FXML
    private Label totalConTax;
    @FXML
    private TableView<Component> receiptTable;
    @FXML
    private TableColumn<Component, Category> comType;
    @FXML
    private TableColumn<Component, String> comPro;
    @FXML
    private TableColumn<Component, Integer> comAmount;
    @FXML
    private TableColumn<Component, Double> comPrice;

    private ObservableList<Component> addTableC;

    private Stage stage;

    private MainWindowController old;

    private Node table;
    @FXML
    private Button edit;
    @FXML
    private Label dataLabel;

    /**
     * Initializes the controller class.
     */
    public void initData(Stage stage, ObservableList<Component> addTableC, MainWindowController old) {
        this.stage = stage;
        this.old = old;
        this.addTableC = addTableC;
        receiptTable.setItems(this.addTableC);
        double price = 0;
        for (int i = 0; i < addTableC.size(); i++) {
            price += addTableC.get(i).getPrice();
        }
        totalSinTax.setText(String.format("%.0f€", price));
        totalConTax.setText(String.format("%.0f€", (price * 1.21)));
        if (old == null) {
            edit.setDisable(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comAmount.setCellValueFactory(new PropertyValueFactory<Component, Integer>("stock"));
        comPrice.setCellValueFactory(new PropertyValueFactory<Component, Double>("price"));
        comPro.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        comType.setCellValueFactory(new PropertyValueFactory<Component, Category>("category"));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        dataLabel.setText(dateFormat.format(date));
    }

    @FXML
    private void onMainMenu(ActionEvent event) {
        this.stage.hide();
        if (this.old != null) {
            this.old.hidea();
        }
    }

    @FXML
    private void onPrint(ActionEvent event) {
        Printer printer = Printer.getDefaultPrinter();
        Stage dialogStage = new Stage(StageStyle.DECORATED);
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job != null) {
            boolean showDialog = job.showPageSetupDialog(dialogStage);
            if (showDialog) {
                boolean success = job.printPage(table);
                if (success) {
                    job.endJob();
                }
            }
        }

    }

    @FXML
    private void onEdit(ActionEvent event) {
        this.stage.hide();
    }

}
