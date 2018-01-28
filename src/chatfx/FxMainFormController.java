package chatfx;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author frair
 */
public class FxMainFormController implements Initializable, MessageListener {

    private ClientThread clientThread;
    private String playerName = "Player";
    private String serverAddress;
    private boolean myTurn = false;
    private ChatFX application;
    @FXML
    private TextField userChat_TF;
    @FXML
    public Button chatSendButton;
    @FXML
    private TextFlow chatTextWindow;
    @FXML
    public Button button00;
    @FXML
    public Button button01;
    @FXML
    public Button button02;
    @FXML
    public Button button10;
    @FXML
    public Button button11;
    @FXML
    public Button button12;
    @FXML
    public Button button20;
    @FXML
    public Button button21;
    @FXML
    public Button button22;
    private Button[][] btns;
    @FXML
    private CheckBox checkReady;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public boolean init() {
        clientThread = new ClientThread(this);
        // connect to the Server
        if (clientThread.Init(this.serverAddress)) {
            this.addSystemTextToFlow(ChatProperties.readProperties("HELLOMESSAGE") + "\n");
            this.btns = new Button[3][3];
            btns[0][0] = button00;btns[0][1] = button01;btns[0][2] = button02;
            btns[1][0] = button10;btns[1][1] = button11;btns[1][2] = button12;
            btns[2][0] = button20;btns[2][1] = button21;btns[2][2] = button22;
            this.cmd_madeturn("", "");
            return true;
        }
        return false;
    }

    public void setApp(ChatFX application) {
        this.application = application;
    }

    private void addOwnTextToFlow(String str) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date());
        Text userText = new Text(strDate + ": " + str + System.lineSeparator());
        userText.setFont(new Font(14));         //Setting font to the text
        userText.setFill(Color.LIGHTCYAN);  //Setting color to the text
        chatTextWindow.getChildren().add(userText);
    }

    private void addCompanionTextToFlow(String str) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date());
        Text userText = new Text(strDate + ": " + str + System.lineSeparator());
        userText.setFont(new Font(14));         //Setting font to the text
        userText.setFill(Color.LIMEGREEN);  //Setting color to the text
        chatTextWindow.getChildren().add(userText);
    }

    private void addSystemTextToFlow(String str) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date());
        Text userText = new Text(strDate + ": " + str + System.lineSeparator());
        userText.setFont(new Font(12));         //Setting font to the text
        userText.setFill(Color.LIGHTCORAL);  //Setting color to the text
        chatTextWindow.getChildren().add(userText);
    }

    private void addTextToServer(String str) {
        String typeOfCmd = "msg";
        String textOfCmd = str;
        if (str.charAt(0) == '/') {
            String[] cmd = str.split(" ");
            typeOfCmd = cmd[0];
            if (cmd.length > 1) {
                textOfCmd = cmd[1];
            }
            applyToClient(typeOfCmd, textOfCmd);
        }
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{typeOfCmd, textOfCmd});
        clientThread.sendMessage(new ChatMessage(typeOfCmd, textOfCmd));
    }

    private void changeStateButton(boolean state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btns[i][j].setDisable(state);
            }
        }
    }

    void applyToClient(String typeOfCmd, String textOfCmd) {
        if (typeOfCmd.equalsIgnoreCase(ChatProperties.readProperties("SETPLAYERNAMECMD"))) {
            playerName = textOfCmd;
        }
    }

    @FXML
    private void inputUserKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.addTextToServer(userChat_TF.getText());
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        this.addTextToServer(userChat_TF.getText());
    }

    @FXML
    private void handleButtonActionPap(ActionEvent event) {
        String str = ((Button) event.getSource()).getId();
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "Button pressed with id = {0}", str);
        if (((Button) event.getSource()).getText().equalsIgnoreCase(".")) {
            cmd_madeturn("", "");
            clientThread.sendMessage(new ChatMessage("/move", str.charAt(str.length() - 2) + "," + str.charAt(str.length() - 1)));
        }
    }

    public void exitApplication() {
        clientThread.sendMessage(new ChatMessage("cmd", ChatProperties.readProperties("EXIT_COMMAND")));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        clientThread.close();
        Platform.exit();
    }

    @Override
    public void gameMessage(Object sender, String typeOfMessage, String textCommand) {
        //message handle
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "We have new message {0}:{1}", new Object[]{typeOfMessage, textCommand});
        if (typeOfMessage.equalsIgnoreCase("msg")) {
            cmd_message(typeOfMessage, textCommand);
        } else {
            //command handle
            if (!cmd_exitcommmand(typeOfMessage, textCommand)) {
                if (!cmd_checkbtn(typeOfMessage, textCommand)) {
                    if (!cmd_win(typeOfMessage, textCommand)) {
                        cmd_myturn(typeOfMessage, textCommand);
                    }
                }
            }

        }
    }

    public void setPlayerName(String playerName) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "Set player name to {0}", playerName);
        this.playerName = playerName;
        if (clientThread != null) {
            clientThread.sendMessage(new ChatMessage("/setname", playerName));
        }
    }

    public void setServerAddress(String serverAddress) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "Set server address to{0}", serverAddress);
        this.serverAddress = serverAddress;
    }

    private boolean cmd_exitcommmand(String typeOfMessage, String textCommand) {
        if (textCommand.equalsIgnoreCase(ChatProperties.readProperties("EXIT_COMMAND"))) //Stop ServerListener thread
        {
            clientThread.setTimeToExit(true);
            return true;
        }
        return false;
    }

    private boolean cmd_checkbtn(String typeOfMessage, String textCommand) {
        if (typeOfMessage.equals(ChatProperties.readProperties("CHECKBTN"))) {
            int i = Integer.parseInt("" + textCommand.charAt(2));
            int j = Integer.parseInt("" + textCommand.charAt(4));
            btns[i][j].setText("" + textCommand.charAt(0));
            return true;
        }
        return false;
    }

    private boolean cmd_myturn(String typeOfMessage, String textCommand) {
        if (typeOfMessage.equals(ChatProperties.readProperties("YOUMOVE"))) {
            changeStateButton(false);
            setMyTurn(true);
            return true;
        }
        return false;
    }

    private boolean cmd_madeturn(String typeOfMessage, String textCommand) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "cmd madeturn");
        changeStateButton(true);
        setMyTurn(false);
        return true;
    }

    private boolean cmd_win(String typeOfMessage, String textCommand) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "cmd_win");
        if (typeOfMessage.equals(ChatProperties.readProperties("WIN"))) {
            Alert alert = new Alert(AlertType.INFORMATION, textCommand + "- wins!!!");
            alert.showAndWait();
            initBtn();
            return true;
        }
        return false;
    }

    private boolean cmd_message(String typeOfMessage, String textCommand) {
        //Send message to GUI throug MessageListener
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "cmd_message: {0} - {1}", new Object[]{typeOfMessage, textCommand});
        if (typeOfMessage.equalsIgnoreCase("msg")) {
            if (textCommand.contains(playerName)) {
                this.addOwnTextToFlow("message from " + textCommand);
            } else {
                this.addCompanionTextToFlow("message from " + textCommand);
            }
            return true;
        }
        return false;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    @FXML
    private void checkReadyHandle(ActionEvent event) {
        if (((CheckBox) event.getSource()).isArmed()) {
            clientThread.sendMessage(new ChatMessage(ChatProperties.readProperties("IAMREADY"), ""));
        }
    }
    
    public void showError(String errorMsg){
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMsg);
        alert.showAndWait();
    }

    private void initBtn() {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "Init Buttons");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btns[i][j].setDisable(false);
                btns[i][j].setText(".");
            }
        }
    }
}
