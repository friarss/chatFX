package chatfxServer;

/**
 *
 * @author frair
 */
public class GameEngine {
    private char[][] gameField;
    public final char PlayerCross='X';
    public final char PlayerZero='O';
    private String Player1="";
    private String Player2="";
    
    GameEngine(){
        gameField = new char[3][3];
        System.out.println("checkWin");
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                gameField[i][j] = '.';
            }
        }
    }
    
    int makeMove(int x,int y,char playerSign){
        if(gameField[x][y]!=PlayerCross && gameField[x][y]!=PlayerZero){
            gameField[x][y] = playerSign;
            return 1;
        }
        return -1;
    }
    
    public char checkWin(char playerSign){
        for(int i=0;i<3;i++){
            if(   gameField[i][0]==playerSign
                &&gameField[i][1]==playerSign
                &&gameField[i][2]==playerSign)
            return playerSign;
        }
        
        for(int i=0;i<3;i++){
            if(   gameField[0][i]==playerSign
                &&gameField[1][i]==playerSign
                &&gameField[2][i]==playerSign)
            return playerSign;
        }
        
        
        if(       gameField[0][0]==playerSign
                &&gameField[1][1]==playerSign
                &&gameField[2][2]==playerSign)
            return playerSign;
        
        if(       gameField[0][2]==playerSign
                &&gameField[1][1]==playerSign
                &&gameField[2][0]==playerSign)
            return playerSign;
        
        return '-';
    }
    
    public int playerMakeMove(String playerName, int x, int y){
        if(playerName.equals(Player1))
            return player1MakeMove(x, y);
        if(playerName.equals(Player2))
            return player2MakeMove(x, y);
        return -1;
    }
    
    public int player1MakeMove(int x, int y){
            return makeMove(x, y, PlayerCross);
    }
    
    public int player2MakeMove(int x, int y){
            return makeMove(x, y, PlayerZero);
    }
    
    public String getPlayer1() {
        return Player1;
    }

    public void setPlayer1(String Player1) {
        this.Player1 = Player1;
    }

    public String getPlayer2() {
        return Player2;
    }

    public void setPlayer2(String Player2) {
        this.Player2 = Player2;
    }
    
    public char getSymbolByName(String playerName){
        if(playerName.equalsIgnoreCase(Player1))return PlayerCross;
        return PlayerZero;
    }
}

