/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Yuki
 */
public class NoobCartWindowController implements Initializable {

    @FXML
    private Label motherboardItem;
    @FXML
    private Label ramItem;
    @FXML
    private Label cpuItem;
    @FXML
    private Label hardDiskItem;
    @FXML
    private Label gpuItem;
    @FXML
    private Label caseItem;

    private Stage stage;
    
    private String[] descriptions;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void adding(int i, String description){
        switch(i){
            case 0: motherboardItem.setText(description); break;
            case 1: cpuItem.setText(description); break;
            case 2: ramItem.setText(description); break;
            case 3: hardDiskItem.setText(description); break;
            case 4: gpuItem.setText(description); break;
            case 5: caseItem.setText(description); break;
            default: break;
        }
    }
    
    public void initData(Stage stage, String[] descriptions){
        this.stage = stage;
        this.descriptions = descriptions;
        for(int i =0; i<this.descriptions.length;i++){
            if(this.descriptions[i] != null){
                adding(i, this.descriptions[i]);
            }
        }
    }
    
    public void initData(Stage stage){
        this.stage = stage;
    }
    
    public void hidea(){
        this.stage.hide();
    }
}
