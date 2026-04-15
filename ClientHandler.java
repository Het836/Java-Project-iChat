import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader; // for recieving
    private BufferedWriter bufferedWriter; // for sending
    private String clientUsername;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));  // convert btye stream to char stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
//            broadcastMessage("SERVER: "+clientUsername+" has entered the chat");
            try {
                String encryptedJoinMsg = CryptoUtil.encrypt("SERVER: " + clientUsername + " has entered the chat");
                broadcastMessage(encryptedJoinMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine(); // this is blocking op for other app, so we use diff thread
                if (messageFromClient == null) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                broadcastMessage(messageFromClient);
            } catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler:clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)){ // iterate every user except us
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine(); // other is waiting for readline so we explicatly add newline
                    clientHandler.bufferedWriter.flush(); // filled buffer full enough to send
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClient(){
        clientHandlers.remove(this); // remove current client
//        broadcastMessage("Server: "+clientUsername+" has left the chat");
        try {
            String encryptedLeaveMsg = CryptoUtil.encrypt("SERVER: " + clientUsername + " has left the chat");
            broadcastMessage(encryptedLeaveMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClient();
        try {
            if (bufferedReader!=null){ // avoid null pointer exception
                bufferedReader.close();
            }
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
            if (socket!=null){
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
