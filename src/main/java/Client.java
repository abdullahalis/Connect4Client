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
}

