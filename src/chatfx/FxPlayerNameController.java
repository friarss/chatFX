package chatfx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * playeName Controller.
 */
public class FxPlayerNameController implements Initializable {
    private ChatFX application;
    @FXML
    Button login;
    @FXML
    private TextField tf_plaeyName;
    @FXML
    private TextField tf_serverAddr;
        
    public void setApp(ChatFX application){
        this.application = application;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    @FXML
    public void processLogin(ActionEvent event) {

        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "User: {0}", tf_plaeyName.getText());
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "Server: {0}", tf_serverAddr.getText());
        application.setUserLogin(tf_plaeyName.getText());
        application.setServerAddress(tf_serverAddr.getText());
        application.changeScene();
    }
    
    public void exitApplication() {
        Platform.exit();
    }
}
