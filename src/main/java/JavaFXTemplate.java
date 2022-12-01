import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.HashMap;

public class JavaFXTemplate extends Application {

	Text portText, ipText;
	TextField portField, ipField;
	Button submit;
	VBox portSelect;
	Scene startScene;
	BorderPane startPane;
	ListView clientMessage;
	Client clientConnection;
	int portNumber;
	String ip;
	EventHandler<ActionEvent> myHandler;
	GameButton board[][];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}



	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Choose a Port and IP Address");
		Text welcome = new Text("Welcome to Connect Four :)");
		welcome.setStyle("-fx-font-size: 40px;\n" +
				"    -fx-font-family: 'Didact Gothic';\n" +
				"    -fx-text-align: center;");
		Image image = new Image("connect4.jpg");
		ImageView iView = new ImageView(image);
		iView.setFitHeight(300);
		iView.setFitWidth(200);
		iView.setPreserveRatio(true);

		// display port field and text
		this.portText = new Text("Choose a port number");
		portText.setStyle("-fx-font-size: 40px;\n" +
				"    -fx-font-family: 'Didact Gothic';\n" +
				"    -fx-text-align: center;");

		this.portField = new TextField();
		portField.setStyle("-fx-max-width: 100px;\n" +
				"    -fx-text-alignment: center;");

		// display ip text and field
		this.ipText = new Text("Choose an IP Address");
		ipText.setStyle("-fx-font-size: 40px;\n" +
				"    -fx-font-family: 'Didact Gothic';\n" +
				"    -fx-text-align: center;");

		this.ipField = new TextField();
		ipField.setStyle("-fx-max-width: 100px;\n" +
				"    -fx-text-alignment: center;");
		this.submit = new Button("Submit");
		submit.setStyle("-fx-font-size: 20px;\n" +
				"    -fx-font-weight: bold;\n" +
				"    -fx-background-color: DARKCYAN;\n" +
				"    -fx-font-family: 'Didact Gothic';");

		this.portSelect = new VBox(welcome, iView, portText, portField, ipText, ipField, submit);
		portSelect.setAlignment(Pos.CENTER);
		portSelect.setSpacing(10);

		// set up button handler
		myHandler = new EventHandler<ActionEvent>() {
			public void handle(final ActionEvent event) {
				GameButton x = (GameButton) event.getSource();

				// make sure it is client's turn and they click a valid button
				if (clientConnection.cInfo.turn && x.getColor() == 'n') {
					int playerNum = clientConnection.cInfo.playerNum;
					if (playerNum == 1) {
						x.setStyle("-fx-background-color: CRIMSON");
						x.setColor('r');
					}
					else {
						x.setStyle("-fx-background-color: YELLOW");
						x.setColor('y');
					}
					int moveRow = x.getRow();
					int moveCol = x.getCol();
					if (moveRow != 0) {
						board[moveCol][moveRow - 1].setDisable(false);
					}

					Boolean won = checkWin(board, x);
					// send move information
					clientMessage.getItems().add("Sending back: playernum: " + playerNum + " won: "
							+ won + " moveRow: " + moveRow + " moveCol: " + moveCol);
					CFourInfo moveInfo = new CFourInfo(true, playerNum, false, won,
														moveRow, moveCol, true);
					clientConnection.send(moveInfo);
				}
			}
		};

		this.submit.setOnAction(e-> {
			this.portNumber = Integer.parseInt(portField.getText());
			this.ip = ipField.getText();

			// handle info that get sent from server
			this.clientConnection = new Client(data-> {
				Platform.runLater(()-> {
					clientConnection.cInfo = (CFourInfo) data;
					CFourInfo cInfo = clientConnection.cInfo;

					clientMessage.getItems().add("Got: playernum: " + clientConnection.cInfo.playerNum + " won: "
							+ clientConnection.cInfo.won + " moveRow: " + clientConnection.cInfo.moveRow + " moveCol: "
							+ clientConnection.cInfo.moveCol + " turn: " + clientConnection.cInfo.turn + " gameOver: "
							+ clientConnection.cInfo.gameOver);


					// game hasn't started
					if (!cInfo.gameStarted) {
						// only one player has joined
						if (!cInfo.twoPlayers) {
							clientMessage.getItems().add("Waiting for second player to join...");
						}
						// two players have joined
						else {
							clientMessage.getItems().add("Player found! Starting the game.");
						}
					}
					// it is player's turn
					if (cInfo.turn) {
						// update board with other player's turn
						int lastMoveCol = cInfo.moveCol;
						int lastMoveRow = cInfo.moveRow;
						clientMessage.getItems().add("Opponent made a move: row " + lastMoveRow
										+ ", column " + lastMoveCol);

						// update board with other player's move
						if (cInfo.playerNum == 1) {
							board[lastMoveCol][lastMoveRow].setColor('y');
							board[lastMoveCol][lastMoveRow].setStyle("-fx-background-color: YELLOW");
						}
						else {
							board[lastMoveCol][lastMoveRow].setColor('r');
							board[lastMoveCol][lastMoveRow].setStyle("-fx-background-color: CRIMSON");
						}
						if (lastMoveRow != 0) {
							board[lastMoveCol][lastMoveRow - 1].setDisable(false);
						}
					}
					// it's not player's turn (
					else if (cInfo.twoPlayers){
						clientMessage.getItems().add("It's the other player's turn");
					}

					// if the game is over
					if (cInfo.gameOver) {
						if (cInfo.won) {
							clientMessage.getItems().add("You won!");
						}
						else {
							clientMessage.getItems().add("You lost");
						}
					}

//					// check if game is over
//					if (clientConnection.cInfo.gameOver) {
//						if (clientConnection.cInfo.won) {
//							clientMessage.getItems().add("You won!");
//						}
//						else {
//							clientMessage.getItems().add("You lost");
//						}
//					}
//					else {
//						// if only one player joined
//						if (!clientConnection.cInfo.twoPlayers && !clientConnection.cInfo.gameStarted) {
//							clientMessage.getItems().add("Waiting for second player to join...");
//						}
//						// if two players have joined
//						else if (clientConnection.cInfo.twoPlayers && clientConnection.cInfo.gameStarted
//								&& clientConnection.cInfo.moveRow == -1) {
//							clientMessage.getItems().add("Player found! Starting the game.");
//						}
//						// if move has gotten sent from other player
//						if (clientConnection.cInfo.turn) {
//							clientMessage.getItems().add("It's your turn");
//							int lastMoveRow = clientConnection.cInfo.moveRow;
//							int lastMoveCol = clientConnection.cInfo.moveCol;
//
//							// if player 2 made last move
//							if (clientConnection.cInfo.playerNum == 1) {
//								board[lastMoveCol][lastMoveRow].setColor('y');
//								board[lastMoveCol][lastMoveRow].setStyle("-fx-background-color: YELLOW");
//								if (lastMoveRow != 0) {
//									board[lastMoveCol][lastMoveRow - 1].setDisable(false);
//								}
//							}
//							// if player 1 made last move
//							else if (clientConnection.cInfo.playerNum == 2){
//								board[lastMoveCol][lastMoveRow].setColor('r');
//								board[lastMoveCol][lastMoveRow].setStyle("-fx-background-color: CRIMSON");
//								if (lastMoveRow != 0) {
//									board[lastMoveCol][lastMoveRow - 1].setDisable(false);
//								}
//							}
//						}
//						else {
//							clientMessage.getItems().add("It's the other player's turn");
//						}
//					}

				});
			},portNumber, ip);

			clientConnection.start();

			gameScreen(primaryStage);
			primaryStage.setTitle("Connect Four");
		});

		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(portSelect);
		startPane.setStyle("-fx-background-color: DODGERBLUE");
		startScene = new Scene(startPane, 800,800);

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(startScene);
		primaryStage.show();
	}

	// display game screen
	public void gameScreen(Stage primaryStage) {
		final boolean[] p1Turn = {true};
		final boolean[] p2Turn = {false};
		GridPane grid = new GridPane();
		board = new GameButton[7][6];
		clientMessage = new ListView();
		/* event handler for each move
		 * player 1 is yellow
		 * player 2 is red      */

		for(int col = 0; col < 7; col++){
			for(int row = 0; row < 6; row++){
				GameButton tempBtn = new GameButton(col, row);
				tempBtn.setDisable(true);
				grid.add(tempBtn, col, row);
				tempBtn.setOnAction(myHandler);
				board[col][row] = tempBtn;
			}
		}
		for (int col = 0; col < 7; col++){
			board[col][5].setDisable(false);
		}
		grid.setVgap(5);
		grid.setHgap(5);

		VBox game = new VBox(30, grid, clientMessage);
		game.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(0), Insets.EMPTY)));
		game.setAlignment(Pos.CENTER);
		game.setMargin(grid, new Insets(100, 100, 100, 175));

		Scene scene = new Scene(game, 700,700);
		scene.getStylesheets().add("GameButtonStyle.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// win screen
	void winScreen(Stage primaryStage) {
		clientMessage.getItems().add("YOU WONNNNN");
	}
	void loseScreen(Stage primaryStage) {
		clientMessage.getItems().add("YOU LOSEEEE");
	}

	// Win checking
	public boolean checkVerticalWin(GameButton[][] board, int colMax, char color, int buttonRow, int buttonCol) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going up from curr button

			if (buttonRow - i < 0) {
				break;
			}
			if (board[buttonCol][buttonRow - i].getColor() == color) {
				count++;
			} else {
				break;
			}
		}
		for(int i = 1; i<4; i++){
			//going up from curr button
			if(buttonRow+i > colMax){

				break;
			}
			if(board[buttonCol][buttonRow+i].getColor() == color){
				count++;
			}
			else{
				break;
			}
		}
		if(count >=3){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean checkHorizontalWin(GameButton[][] board, int rowMax, char color, int buttonCol, int buttonRow) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going left from curr button
			if (buttonCol - i < 0) {
				//System.out.println((buttonCol - i));
				break;
			}
			if (board[buttonCol- i][buttonRow].getColor() == color) {
				count++;
			}
			else {
				//System.out.println("broke at" + (buttonCol-i) + "," + buttonRow);
				break;
			}
		}
		for(int i = 1; i<4; i++){
			//going right from curr button
			if(buttonCol+i > rowMax){
				//System.out.println((buttonCol+i));
				break;
			}
			if(board[buttonCol+i][buttonRow].getColor() == color){
				count++;
			}
			else{
				//System.out.println("broke at" + (buttonCol+1) +","+ buttonRow);
				break;
			}
		}
		if(count >=3){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean checkAscendingWin(GameButton[][] board, int rowMax, int colMax, char color, int currRow, int currCol) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going up from curr button
			if (currRow+i > rowMax|| currCol - i < 0) {
				break;
			}
			if (board[currCol-i][currRow + i].getColor() == color) {
				count++;
			} else {
				break;
			}
		}
		for(int i = 1; i<4; i++){
			//going down from curr button
			if(currRow - i < 0 || currCol +i > colMax){
				break;
			}
			if(board[currCol+i][currRow-i].getColor() == color){
				count++;
			}
			else{
				break;
			}
		}
		if(count >=3){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean checkDescendingWin(GameButton[][]board, int rowMax, int colMax, char color, int currRow, int currCol) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going up from curr button
			if (currRow - i < 0 || currCol - i < 0) {
				break;
			}
			if (board[currCol-i][currRow - i].getColor() == color) {
				count++;
			} else {
				break;
			}
		}
		for(int i = 1; i<4; i++){
			//going down from curr button
			if(currRow+i > rowMax|| currCol +i > colMax){
				break;
			}
			if(board[currCol+i][currRow+i].getColor() == color){
				count++;
			}
			else{
				break;
			}
		}
		if(count >=3){
			return true;
		}
		else{
			return false;
		}
	}

	// uses players most recent move to efficiently check for a win
	public boolean checkWin(GameButton[][] board, GameButton button) {
		int rowMax = 5;
		int colMax = 6;
		char color = button.getColor();
		int buttonRow = button.getRow();
		int buttonCol = button.getCol();


		if (checkVerticalWin(board, rowMax, color, buttonRow, buttonCol) || checkHorizontalWin(board, colMax, color, buttonCol, buttonRow)
				|| checkAscendingWin(board, rowMax, colMax, color, buttonRow, buttonCol) ||
				checkDescendingWin(board, rowMax, colMax, color, buttonRow, buttonCol)) {
			return true;
		}
		else {
			return false;
		}

	}

	public void markVerticalWin(GameButton[][] board, int colMax, char color, int buttonRow, int buttonCol) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going up from curr button

			if (buttonRow - i < 0) {
				break;
			}
			if (board[buttonCol][buttonRow - i].getColor() == color) {
				board[buttonCol][buttonRow - i].setText("X");
				//board[buttonCol][buttonRow-i].setStyle("-fx-background-color: CHARTREUSE");
				count++;
			} else {
				break;
			}
		}
		for(int i = 1; i<4; i++){
			//going up from curr button
			if(buttonRow+i > colMax){

				break;
			}
			if(board[buttonCol][buttonRow+i].getColor() == color){
				board[buttonCol][buttonRow+i].setStyle("-fx-background-color: CHARTREUSE");
				count++;
			}
			else{
				break;
			}
		}
	}
	public void markHorizontalWin(GameButton[][] board, int rowMax, char color, int buttonCol, int buttonRow) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going left from curr button
			if (buttonCol - i < 0) {
				//System.out.println((buttonCol - i));
				break;
			}
			if (board[buttonCol- i][buttonRow].getColor() == color) {
				board[buttonCol-i][buttonRow].setStyle("-fx-background-color: CHARTREUSE");
			}
			else {
				//System.out.println("broke at" + (buttonCol-i) + "," + buttonRow);
				break;
			}
		}
		for(int i = 1; i<4; i++){
			//going right from curr button
			if(buttonCol+i > rowMax){
				//System.out.println((buttonCol+i));
				break;
			}
			if(board[buttonCol+i][buttonRow].getColor() == color){
				board[buttonCol+i][buttonRow].setStyle("-fx-background-color: CHARTREUSE");
			}
			else{
				//System.out.println("broke at" + (buttonCol+1) +","+ buttonRow);
				break;
			}
		}
	}
}
