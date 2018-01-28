package chatfxServer;

/**
 *
 * @author frair
 */
import chatfx.ChatFX;
import chatfx.ChatProperties;
import chatfx.MessageListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

class ChatServer implements MessageListener {

    private List<ClientHandler> clients; // list of clients
    GameEngine gameEngine;

    public static void main(String[] args) {
        loggerInit();
        ChatServer chatServer = new ChatServer();
        chatServer.startGame();
        chatServer.Init();
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

    private void startGame() {
        if (gameEngine != null) {
            String pl1 = gameEngine.getPlayer1();
            String pl2 = gameEngine.getPlayer2();
            gameEngine = new GameEngine();
            gameEngine.setPlayer1(pl2);
            gameEngine.setPlayer2(pl1);
        } else {
            gameEngine = new GameEngine();
        }
    }

    public void Init() {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "{0} {1} {2}", new Object[]{ChatProperties.readProperties("SERVER_START"), ChatProperties.readProperties("SERVER_PORT"), ChatProperties.readProperties("SERVER_ADDR")});
        clients = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(Integer.parseInt(ChatProperties.readProperties("SERVER_PORT")))) {
            new Thread(new CommandHandler(server)).start(); // command to server
            while (true) {
                if (clients.size() < 3) {
                    Socket socket = server.accept();
                    Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "#{0}{1}{2}", new Object[]{clients.size(), 1, ChatProperties.readProperties("CLIENT_JOINED")});
                    ClientHandler client = new ClientHandler(socket);
                    client.setListner(this);
                    clients.add(client); // adding new client in the list
                    new Thread(client).start();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(ChatProperties.readProperties("SERVER_STOP"));
    }

    @Override
    synchronized public void gameMessage(Object sender, String cmd, String args) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "We have new message from {0}: {1} - {2}", new Object[]{((ClientHandler) sender).getPlayerName(), cmd, args});

        if (!cmd_makemove(sender, cmd, args)) {
            if (!cmd_setplayername(sender, cmd, args)) {
                if (!cmd_startgame(sender, cmd, args)) {
                    if (!cmd_exitcommand(sender, cmd, args)) {
                        cmd_ready(sender, cmd, args);
                    }
                }
            }
        } else {
            cmd_win(sender, "", "");
            if (clients.get(0).getPlayerName().equalsIgnoreCase(((ClientHandler) sender).getPlayerName())) {
                clients.get(1).sendMsg(ChatProperties.readProperties("YOUMOVE"), "");
                broadcastMsg("msg", clients.get(1).getPlayerName() + " - move");
            } else {
                clients.get(0).sendMsg(ChatProperties.readProperties("YOUMOVE"), "");
                broadcastMsg("msg", clients.get(0).getPlayerName() + " - move");
            }
        }

        if (cmd.equalsIgnoreCase("msg")) {
            broadcastMsg("msg", ((ClientHandler) sender).getPlayerName() + ": " + args);
        }
    }

    /**
     * broadcastMsg: sending a message to all clients
     */
    private void broadcastMsg(String cmd, String msg) {
        Logger.getLogger(ChatFX.class.getName()).log(Level.INFO, "Broadcasting: {0} - {1}", new Object[]{cmd, msg});
        clients.forEach((client) -> {
            client.sendMsg(cmd, msg);
        });
    }

    private boolean cmd_makemove(Object sender, String cmd, String args) {
        if (cmd.equalsIgnoreCase(ChatProperties.readProperties("MAKEMOVE"))) {
            String[] arr = args.split(",");
            int x = Integer.parseInt(arr[0]);
            int y = Integer.parseInt(arr[1]);
            if (gameEngine.playerMakeMove(((ClientHandler) sender).getPlayerName(), x, y) != -1) {
                broadcastMsg(ChatProperties.readProperties("CHECKBTN"), gameEngine.getSymbolByName(((ClientHandler) sender).getPlayerName()) + "|" + args);
            }
            return true;
        }
        return false;
    }

    private boolean cmd_win(Object sender, String cmd, String args) {
        if (gameEngine.checkWin(gameEngine.getSymbolByName(((ClientHandler) sender).getPlayerName())) != '-') {
            broadcastMsg(ChatProperties.readProperties("WIN"), ((ClientHandler) sender).getPlayerName());
            startGame();
            return true;
        }
        return false;
    }

    private boolean cmd_ready(Object sender, String cmd, String args) {
        if (cmd.equalsIgnoreCase(ChatProperties.readProperties("IAMREADY"))) {
            ((ClientHandler) sender).setiAmReady(true);
            broadcastMsg("msg", ((ClientHandler) sender).getPlayerName() + " - ready");
            if (clients.get(0).isiAmReady()
                    && clients.size() > 1
                    && clients.get(1).isiAmReady()) {
                clients.get(0).sendMsg(ChatProperties.readProperties("YOUMOVE"), "");
                broadcastMsg("msg", clients.get(0).getPlayerName() + " - begins");
            }
            return true;
        }
        return false;
    }

    private boolean cmd_setplayername(Object sender, String cmd, String args) {
        if (cmd.equalsIgnoreCase(ChatProperties.readProperties("SETPLAYERNAMECMD"))) {
            ((ClientHandler) sender).setPlayerName(args);
            if (gameEngine != null) {
                if (gameEngine.getPlayer1().isEmpty()) {
                    gameEngine.setPlayer1(args);

                } else {
                    gameEngine.setPlayer2(args);
                }
            }
            return true;
        }
        return false;
    }

    private boolean cmd_startgame(Object sender, String cmd, String args) {
        if (cmd.equalsIgnoreCase(ChatProperties.readProperties("STARTGAME"))) {
            startGame();
            return true;
        }
        return false;
    }

    private boolean cmd_exitcommand(Object sender, String cmd, String args) {
        //Exit command
        if (args.equalsIgnoreCase(ChatProperties.readProperties("EXIT_COMMAND"))) {
            ((ClientHandler) sender).setTimeToExit(true);
            clients.remove((ClientHandler) sender);
            broadcastMsg("msg", ((ClientHandler) sender).getPlayerName() + ": " + args);
            return true;
        }
        return false;
    }

    /**
     * CommandHandler: processing of commands from server console
     */
    class CommandHandler implements Runnable {

        ServerSocket server;
        Scanner scanner;

        CommandHandler(ServerSocket server) {
            this.server = server;
            scanner = new Scanner(System.in);
        }

        @Override
        public void run() {
            String command;
            do {
                command = scanner.nextLine();
            } while (!command.equalsIgnoreCase(ChatProperties.readProperties("EXIT_COMMAND")));
            try {
                server.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
