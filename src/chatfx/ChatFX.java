package chatfx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author friar
 */
public class ChatFX extends Application {

    private String serverAddress;
    private String userLogin;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        loggerInit();
        this.stage = stage;
        stage.setTitle(ChatProperties.readProperties("APP_CAPTION"));
        gotoLogin();
        stage.show();
    }

    private void gotoGame() {
        try {
            FxMainFormController gameController = (FxMainFormController) replaceSceneContent(getClass().getResource("FxMainForm.fxml"));
            gameController.setApp(this);
            gameController.setServerAddress(this.serverAddress);
            if (gameController.init()) {
                gameController.setPlayerName(this.userLogin);
                stage.setOnHidden((WindowEvent e) -> {
                    gameController.exitApplication();
                    Platform.exit();
                    System.exit(0);
                });
            }else {
                gameController.showError(ChatProperties.readProperties("NOSERVER"));
                Platform.exit();
                System.exit(0);
            }
        } catch (Exception ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gotoLogin() {
        try {
            FxPlayerNameController login = (FxPlayerNameController) replaceSceneContent(getClass().getResource("FxPlayerName.fxml"));
            login.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(java.net.URL fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(fxml);
        AnchorPane page;
        page = (AnchorPane) loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        return (Initializable) loader.getController();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void changeScene() {
        gotoGame();
    }
    
    private static void loggerInit() {
        if (ChatProperties.readProperties("LOGTO").equalsIgnoreCase("file")) {
            try {
                InputStream input = new FileInputStream("resources/log.properties");
                LogManager.getLogManager().readConfiguration(input);
            } catch (IOException ex) {
                System.out.println("Login properties not found" + ex.getMessage());
            }
        }
    }
    
}
