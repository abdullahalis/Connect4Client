import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class GameBoardTest {
    GameBoard board;
    @Test
    void GameBoardConstructor() {
        GameBoard gb = new GameBoard();
    }

    @BeforeEach
    void setup() {
        board = new GameBoard();
    }
    @Test
    void getAndSetGameButtonTest() {
        GameButton button = new GameButton(2, 1);
        button.setColor('r');
        board.setGameButton(button);
        assertEquals(button, board.getGameButton(2, 1));
    }
}
