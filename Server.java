import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while (!serverSocket.isClosed()){ // make sure sever is open

                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler); // create and assign thres to CH
                thread.start();
            }
        }
        catch (IOException e){

        }
    }

    public void closeServerSocket(){
        try{
            if (serverSocket!=null){ // make sure serverSocket is not null otherwise, it throws null exception
                serverSocket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234); // 1234 is common port among server & client
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
