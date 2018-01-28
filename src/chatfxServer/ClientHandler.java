package chatfxServer;

import chatfx.ChatFX;
import chatfx.ChatMessage;
import chatfx.ChatProperties;
import chatfx.MessageListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frair
 */
/**
 * ClientHandler: service requests of clients
 */
class ClientHandler implements Runnable {
    private String playerName;
    private Socket socket;
    private ArrayList<MessageListener> messageListener;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    boolean timeToExit;
    private boolean iAmReady=false;

    public boolean isiAmReady() {
        return iAmReady;
    }

    public void setiAmReady(boolean iAmReady) {
        this.iAmReady = iAmReady;
    }

    public void setTimeToExit(boolean timeToExit) {
        this.timeToExit = timeToExit;
    }

    ClientHandler(Socket clientSocket) {
        try {
            socket = clientSocket;
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            timeToExit = false;
        } catch (IOException ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, null, ex);
        }
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName=playerName;
    }

    /**
     * sendMsg: sending a message to the client
     */
    public void sendMsg(String cmd, String msg) {
        sendObject(new ChatMessage(cmd, msg));
    }

    private void sendObject(ChatMessage cm) {
        try {
            if (socket.isConnected()) {
                oos.writeObject(cm);
                oos.flush();
                Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "ClientHandler-sendObject: wite to oos {0}: {1}", new Object[]{cm.messageType, cm.message});
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, null, ex);
        }
    }

    /**
     * run: Run listen thread
     */
    @Override
    public void run() {
        ChatMessage cm;
        do {
            try {
                if (socket.isConnected()) {
                    //read object from socket
                    cm = (ChatMessage) ois.readObject();
                    if (cm != null) {                        
                        notifyGameListner(cm.messageType, cm.message);
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, null, ex);
            }
        } while (timeToExit == false);
        //close the socket
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, null, ex);
        }
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "{0}{1}", new Object[]{playerName, ChatProperties.readProperties("CLIENT_DISCONNECTED")});
    }
    
    /**
     * notifyGameListner: Listner wich 
     */
    public void notifyGameListner(String cmd, String message) {
        if (messageListener != null) {
            Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "ClientHandler-notifyGameListner: send {0} - {1}", new Object[]{cmd, message});
            messageListener.forEach((ml) -> {
                ml.gameMessage(this, cmd, message);
            });
        }
    }

    /**
     * setListner: sets the listner
     */
    public void setListner(MessageListener ml) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "ClientHandler-setListner: set");
        if(this.messageListener==null)this.messageListener = new ArrayList<>();
        this.messageListener.add(ml);
    }
    //Listner wich sends to all
}
