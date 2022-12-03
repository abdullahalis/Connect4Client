import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
public class Client extends Thread {
    String ip;
    int portNumber;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    private Consumer<Serializable> callback;
    CFourInfo cInfo;

    Client(Consumer<Serializable> call, int portNumber, String ip){
        callback = call;
        this.portNumber = portNumber;
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            socket= new Socket(ip, portNumber);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        while(true) {
            try {
                cInfo = (CFourInfo) in.readObject();
                callback.accept(cInfo);
            }
            catch(Exception e) {}
        }
    }

    public void send(CFourInfo data) {
        try {
            out.writeObject(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
}

