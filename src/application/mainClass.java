/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.PrincipalMenuWindowController;

/**
 *
 * @author Yuki
 */
public class mainClass extends Application{

    @Override
    public void start(Stage stage) throws Exception{

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/PrincipalMenuWindow.fxml"));
        Parent root = (Parent) myLoader.load();
        PrincipalMenuWindowController dcCon = myLoader.<PrincipalMenuWindowController>getController();
        
        Scene scene = new Scene(root);
        stage.setResizable(false);
        
        stage.setTitle("Main Menu");
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
