/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filechecksum;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author Mundha
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private BorderPane borderPane;
    
    @FXML
    private TextField TextFieldDirectory;
    
    @FXML
    private Button ButtonBrowse;
    
    @FXML
    private void handleButtonBrowseAction(ActionEvent event) {
        System.out.println("You clicked me!");
        //label.setText("Hello World!");
        
        DirectoryChooser dc = new DirectoryChooser();
        //dc.showDialog(null);
        
        File d = dc.showDialog(borderPane.getScene().getWindow());
        
        TextFieldDirectory.setText(d.getAbsolutePath());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        ButtonBrowse.requestFocus();
    }    
    
}
