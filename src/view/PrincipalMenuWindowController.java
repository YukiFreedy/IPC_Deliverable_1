/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import model.marshData;

/**
 * FXML Controller class
 *
 * @author Yuki
 */
public class PrincipalMenuWindowController implements Initializable {

    @FXML
    private Button advLoad;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void onLoadPC(ActionEvent event) {
        marshData erBool = new marshData();
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
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/LoadPCWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            LoadPCWindowController dcCon = myLoader.<LoadPCWindowController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Load PC");
            stage.initModality(Modality.APPLICATION_MODAL);
            dcCon.initData(stage, false);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {

        }

    }

    @FXML
    private void onStepByStep(ActionEvent event) {
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
    }

    @FXML
    private void onLoadPreconfiguredPC(ActionEvent event) {
        marshData erBool = new marshData();
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
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/LoadPCWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            LoadPCWindowController dcCon = myLoader.<LoadPCWindowController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Load Preconfigured PC");
            stage.initModality(Modality.APPLICATION_MODAL);
            dcCon.initData(stage, true);
            dcCon.disBut();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLetsBuild(ActionEvent event) {
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
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
