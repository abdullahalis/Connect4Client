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
				"    -fx-background-color: GAINSBORO;\n" +
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

					Boolean won = clientConnection.checkWin(board, x);

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
						if (lastMoveRow != -1) {
							clientMessage.getItems().add("Opponent made a move: row " + lastMoveRow
									+ ", column " + lastMoveCol);
						}
						clientMessage.getItems().add("Your turn");


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
					boolean boardFilled = true;
					for(int i = 0; i < 6; i++){
						if(board[i][0].getColor() == 'n'){
							boardFilled = false;
						}
					}
					if(boardFilled){
						makeTieScene(primaryStage);
					}
					// if the game is over
					if (cInfo.gameOver) {
						if (cInfo.won) {
							clientMessage.getItems().add("You won!");
							int lastMoveCol = cInfo.moveCol;
							int lastMoveRow = cInfo.moveRow;
							GameButton x = board[lastMoveCol][lastMoveRow];

							if(clientConnection.checkWin(board, x)){
								for(int c = 0; c < 7; c++){
									for(int r = 0; r < 6; r++){
										board[c][r].setDisable(true);
									}
								}
								if(clientConnection.checkVerticalWin(board, 5, x.getColor(), x.getRow(), x.getCol())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markVerticalWin(board, 5, x.getColor(), x.getRow(), x.getCol());
									System.out.println("vert win");
								}
								else if(clientConnection.checkHorizontalWin(board, 6, x.getColor(), x.getCol(), x.getRow())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markHorizontalWin(board, 6, x.getColor(), x.getCol(), x.getRow());
									System.out.println("horiz win");
								}
								else if(clientConnection.checkAscendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markAscendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol());
									System.out.println("asc win");
								}
								else if(clientConnection.checkDescendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markDescendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol());
									System.out.println("desc win");
								}
								for(int c = 0; c < 7; c++){
									for(int r = 0; r < 6; r++){
										board[c][r].setDisable(true);
									}
								}
								x.setStyle("-fx-background-color: CHARTREUSE");
								markDescendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol());
								PauseTransition pause = new PauseTransition(Duration.seconds(3));
								pause.setOnFinished(ev ->
										makeWinScene(primaryStage, x.getColor()));
								pause.play();
							}
						}
						else {
							clientMessage.getItems().add("You lost");
							clientMessage.getItems().add("You won!");
							int lastMoveCol = cInfo.moveCol;
							int lastMoveRow = cInfo.moveRow;
							GameButton x = board[lastMoveCol][lastMoveRow];

							if(clientConnection.checkWin(board, x)){
								for(int c = 0; c < 7; c++){
									for(int r = 0; r < 6; r++){
										board[c][r].setDisable(true);
									}
								}
								if(clientConnection.checkVerticalWin(board, 5, x.getColor(), x.getRow(), x.getCol())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markVerticalWin(board, 5, x.getColor(), x.getRow(), x.getCol());
									System.out.println("vert win");
								}
								else if(clientConnection.checkHorizontalWin(board, 6, x.getColor(), x.getCol(), x.getRow())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markHorizontalWin(board, 6, x.getColor(), x.getCol(), x.getRow());
									System.out.println("horiz win");
								}
								else if(clientConnection.checkAscendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markAscendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol());
									System.out.println("asc win");
								}
								else if(clientConnection.checkDescendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol())){
									x.setStyle("-fx-background-color: CHARTREUSE");
									markDescendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol());
									System.out.println("desc win");
								}
								for(int c = 0; c < 7; c++){
									for(int r = 0; r < 6; r++){
										board[c][r].setDisable(true);
									}
								}
								x.setStyle("-fx-background-color: CHARTREUSE");
								markDescendingWin(board, 5, 6, x.getColor(), x.getRow(), x.getCol());
								PauseTransition pause = new PauseTransition(Duration.seconds(3));
								pause.setOnFinished(ev ->
										makeWinScene(primaryStage, x.getColor()));
								pause.play();
							}
						}
					}

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

	public void markVerticalWin(GameButton[][] board, int colMax, char color, int buttonRow, int buttonCol) {
		int count = 0;
		for(int i = 1; i < 4; i++) {
			//going up from curr button

			if (buttonRow - i < 0) {
				break;
			}
			if (board[buttonCol][buttonRow - i].getColor() == color) {
				//board[buttonCol][buttonRow - i].setText("X");
				board[buttonCol][buttonRow-i].setStyle("-fx-background-color: CHARTREUSE");
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
		for (int i = 1; i < 4; i++) {
			//going left from curr button
			if (buttonCol - i < 0) {
				//System.out.println((buttonCol - i));
				break;
			}
			if (board[buttonCol - i][buttonRow].getColor() == color) {
				board[buttonCol - i][buttonRow].setStyle("-fx-background-color: CHARTREUSE");
			} else {
				//System.out.println("broke at" + (buttonCol-i) + "," + buttonRow);
				break;
			}
		}
		for (int i = 1; i < 4; i++) {
			//going right from curr button
			if (buttonCol + i > rowMax) {
				//System.out.println((buttonCol+i));
				break;
			}
			if (board[buttonCol + i][buttonRow].getColor() == color) {
				board[buttonCol + i][buttonRow].setStyle("-fx-background-color: CHARTREUSE");
			} else {
				//System.out.println("broke at" + (buttonCol+1) +","+ buttonRow);
				break;
			}
		}

	}

	public void markAscendingWin(GameButton[][] board, int rowMax, int colMax, char color, int currRow, int currCol) {
		int count = 0;
		for (int i = 1; i < 4; i++) {
			//going up from curr button
			if (currRow + i > rowMax || currCol - i < 0) {
				break;
			}
			if (board[currCol - i][currRow + i].getColor() == color) {
				board[currCol - i][currRow + i].setStyle("-fx-background-color: CHARTREUSE");
			} else {
				break;
			}
		}
		for (int i = 1; i < 4; i++) {
			//going down from curr button
			if (currRow - i < 0 || currCol + i > colMax) {
				break;
			}
			if (board[currCol + i][currRow - i].getColor() == color) {
				board[currCol + i][currRow - i].setStyle("-fx-background-color: CHARTREUSE");
			} else {
				break;
			}
		}
	}

	public void markDescendingWin(GameButton[][] board, int rowMax, int colMax, char color, int currRow, int currCol) {
		int count = 0;
		for (int i = 1; i < 4; i++) {
			//going up from curr button
			if (currRow - i < 0 || currCol - i < 0) {
				break;
			}
			if (board[currCol - i][currRow - i].getColor() == color) {
				board[currCol - i][currRow - i].setStyle("-fx-background-color: CHARTREUSE");
			} else {
				break;
			}
		}
		for (int i = 1; i < 4; i++) {
			//going down from curr button
			if (currRow + i > rowMax || currCol + i > colMax) {
				break;
			}
			if (board[currCol + i][currRow + i].getColor() == color) {
				board[currCol + i][currRow + i].setStyle("-fx-background-color: CHARTREUSE");
			} else {
				break;
			}
		}

	}
	void makeWinScene(Stage primaryStage, char color){
		Text winText = new Text();
		if(clientConnection.cInfo.won){
			winText.setText("You Win!!!!!");
		}
		else {
			winText.setText("You lost :(");
		}
		winText.setStyle("-fx-font-size: 3em");
		Button playAgain = new Button("Play Again");
		Button quitBtn = new Button("Quit");
		quitBtn.setOnAction(e-> Platform.exit());
		playAgain.setOnAction(e->quit(primaryStage));
		VBox box = new VBox(10, winText, playAgain, quitBtn);
		box.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(0), Insets.EMPTY)));
		box.setAlignment(Pos.CENTER);

		Scene scene = new Scene(box, 700,700);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	void makeTieScene(Stage primaryStage){
		Text tieText = new Text("Neither Player Wins!");
		tieText.setStyle("-fx-font-size: 3em");
		Button playAgain = new Button("Play Again");
		Button quitBtn = new Button("Quit");
		quitBtn.setOnAction(e-> Platform.exit());
		playAgain.setOnAction(e->quit(primaryStage));
		VBox box = new VBox(10, tieText, playAgain, quitBtn);
		box.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(0), Insets.EMPTY)));
		box.setAlignment(Pos.CENTER);

		Scene scene = new Scene(box, 700,700);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public void quit(Stage primaryStage) {
		try {
			gameScreen(primaryStage);
			CFourInfo empty = new CFourInfo();
			empty.restart = true;
			clientConnection.out.writeObject(empty);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}