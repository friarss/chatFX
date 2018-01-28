/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatfxServer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author frair
 */
public class GameEngineTest {
    
    public GameEngineTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of makeMove method, of class GameEngine.
     */
    @Test
    public void testMakeMove() {
        System.out.println("makeMove");
        byte x = 0;
        byte y = 0;
        GameEngine instance = new GameEngine();
        int expResult = 1;
        int result = instance.player1MakeMove(x, y);
        assertEquals(expResult, result);
        
        System.out.println("makeMove");
        x = 0;
        y = 0;
        instance = new GameEngine();
        instance.player1MakeMove(0, 0);
        expResult = -1;
        result = instance.player2MakeMove(x, y);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSimulateGameStrDiag() {
        System.out.println("SimulateGame - straight diagonal wins");
        GameEngine instance = new GameEngine();
        instance.setPlayer1("Human");//'X'
        instance.setPlayer2("Dog");//'O'
        
        assertEquals(1, instance.player1MakeMove(0, 0));
        assertEquals(-1, instance.player2MakeMove(0, 0));
        
        assertEquals(1, instance.player1MakeMove(1, 1));
        assertEquals(1, instance.player2MakeMove(0, 1));
        
        assertEquals(1, instance.player1MakeMove(2, 2));
        assertEquals(1, instance.player2MakeMove(0, 2));

        char expResult = instance.PlayerCross;//'X'
        char result = instance.checkWin(instance.PlayerCross);
        assertEquals(expResult, result);        
    }
    
    @Test
    public void testSimulateGameHor() {
        System.out.println("SimulateGame - horizontal wins");
        GameEngine instance = new GameEngine();
        instance.setPlayer1("Human");//'X'
        instance.setPlayer2("Dog");//'O'
        
        assertEquals(1, instance.player1MakeMove(0, 0));
        assertEquals(1, instance.player2MakeMove(1, 0));
        
        assertEquals(1, instance.player1MakeMove(0, 1));
        assertEquals(1, instance.player2MakeMove(1, 1));
        
        assertEquals(1, instance.player1MakeMove(0, 2));
        assertEquals(1, instance.player2MakeMove(1, 2));

        char expResult = instance.PlayerCross;//'X'
        char result = instance.checkWin(instance.PlayerCross);
        assertEquals(expResult, result);        
    }
    
    @Test
    public void testSimulateGameVer() {
        System.out.println("SimulateGame - vertical wins");
        GameEngine instance = new GameEngine();
        instance.setPlayer1("Human");//'X'
        instance.setPlayer2("Dog");//'O'
        
        assertEquals(1, instance.player1MakeMove(0, 0));
        assertEquals(1, instance.player2MakeMove(0, 2));
        
        assertEquals(1, instance.player1MakeMove(1, 0));
        assertEquals(1, instance.player2MakeMove(1, 2));
        
        assertEquals(1, instance.player1MakeMove(2, 0));
        assertEquals(1, instance.player2MakeMove(2, 2));

        char expResult = instance.PlayerCross;//'X'
        char result = instance.checkWin(instance.PlayerCross);
        assertEquals(expResult, result);        
    }
    
    @Test
    public void testSimulateGameInvertDiag() {
        System.out.println("SimulateGame - invert diagonal wins");
        GameEngine instance = new GameEngine();
        instance.setPlayer1("Human");//'X'
        instance.setPlayer2("Dog");//'O'
        
        assertEquals(1, instance.player1MakeMove(0, 2));
        assertEquals(1, instance.player1MakeMove(1, 1));        
        assertEquals(1, instance.player1MakeMove(2, 0));

        char expResult = instance.PlayerCross;//'X'
        char result = instance.checkWin(instance.PlayerCross);
        assertEquals(expResult, result);        
    }
}
