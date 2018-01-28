package chatfx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author frair
 */
public class ClientThread {

    private ServerListener serverListener;
    private Socket socket;
    private boolean isConncted;
    private ArrayList<MessageListener> messageListener;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientThread(MessageListener messageListener) {
        if (this.messageListener == null) {
            this.messageListener = new ArrayList<>();
        }
        this.messageListener.add(messageListener);
        isConncted = false;
    }

    void runThread() {
        serverListener = new ServerListener();
        new Thread(serverListener).start(); // start Server listener
    }

    /**
     * Connect to the Server
     *
     * @param serverAddress
     * @return true - if connection created
     */
    public boolean Init(String serverAddress) {
        if (isConncted) {
            return true;
        }
        try {
            socket = new Socket(serverAddress, Integer.parseInt(ChatProperties.readProperties("SERVER_PORT")));
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            this.runThread();
            isConncted = true;
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Close connection to the Server
     */
    public void close() {
        try {
            if (isConncted) {
                socket.close();
                isConncted = false;
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(ChatMessage cm) {
        if (oos != null) {
            try {
                oos.writeObject(cm);
                oos.flush();
                Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "ClientThread-sendObject: write to oos {0}: {1}", new Object[]{cm.messageType, cm.message});
            } catch (IOException ex) {
                Logger.getLogger(ChatFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setTimeToExit(boolean timeToExit) {
        serverListener.timeToExit = timeToExit;
    }

    /**
     * ServerListener: get messages from Server
     */
    class ServerListener implements Runnable {

        //if true stop ServerListener thread
        private boolean timeToExit;

        @Override
        public void run() {
            timeToExit = false;
            ChatMessage chatMessage;
            do {
                try {
                    if (socket.isConnected()) {
                        //read object from socket
                        chatMessage = (ChatMessage) ois.readObject();
                        if (chatMessage != null) {
                            notifyGameListner(chatMessage.messageType, chatMessage.message);
                        }
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("ClientThread: " + ex.getMessage());
                }
            } while (timeToExit == false);
        }
    }

    /**
     * notifyGameListner: Listner wich sends to all
     *
     * @param typeOfMessage
     * @param textCommand
     */
    public void notifyGameListner(String typeOfMessage, String textCommand) {
        if (messageListener != null) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "ClientThread-notifyGameListner: wite to oos {0}: {1}", new Object[]{typeOfMessage, textCommand});
            //sending from JavaFX thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageListener.forEach((ml) -> {
                        ml.gameMessage(this, typeOfMessage, textCommand);
                    });

                }
            });
        }
    }

    /**
     * setListner: sets the listner
     *
     * @param messageListener
     */
    public void setListner(MessageListener messageListener) {
        if (this.messageListener == null) {
            this.messageListener = new ArrayList<>();
        }
        this.messageListener.add(messageListener);
    }
}
