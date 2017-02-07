/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Yuki
 */
public class ChoicePCWindowController implements Initializable {

    @FXML
    private RadioButton gamingChoice;
    @FXML
    private RadioButton casualChoice;
    @FXML
    private RadioButton designerChoice;
    
    private Stage stage;

    private final ToggleGroup group = new ToggleGroup();
    
    private NoobWindowController dcCon;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gamingChoice.setToggleGroup(group);
        casualChoice.setToggleGroup(group);
        designerChoice.setToggleGroup(group);
    }    

    @FXML
    private void onCancel(ActionEvent event) {
        stage.hide();
    }

    @FXML
    private void onStart(ActionEvent event) {
        if(!gamingChoice.isSelected() && !casualChoice.isSelected() && !designerChoice.isSelected()) return;
        
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/NoobWindow.fxml"));
            Parent root = (Parent) myLoader.load();
            dcCon = myLoader.<NoobWindowController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Load Preconfigured PC");
            stage.initModality(Modality.APPLICATION_MODAL);
            dcCon.initData(stage, getNumber());
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    dcCon.hidea();
                }
            });
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        stage.hide();
    }
    
    public void initData(Stage stage){
        this.stage = stage;
    }

    @FXML
    private void onGaming(ActionEvent event) {
       gamingChoice.setSelected(true);
       
    }

    @FXML
    private void onDesigner(ActionEvent event) {
        designerChoice.setSelected(true);
    }

    @FXML
    private void onCasual(ActionEvent event) {
        casualChoice.setSelected(true);
    }
    
    private int getNumber(){
        if(gamingChoice.isSelected()) return 0;
        if(casualChoice.isSelected()) return 1;
        if(designerChoice.isSelected()) return 2;
        else return -1;
    }
    
}
