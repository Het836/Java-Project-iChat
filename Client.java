import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));  // convert btye stream to char stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);

            while (socket.isConnected()){
                String messageToSend = sc.nextLine();
//                bufferedWriter.write(username+": "+messageToSend);
                String encryptedMessage = CryptoUtil.encrypt(username + ": " + messageToSend);
                bufferedWriter.write(encryptedMessage);

                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (Exception e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msfFromGroupChat;

                while (socket.isConnected()){
                    try {
                        msfFromGroupChat = bufferedReader.readLine();
//                        System.out.println(msfFromGroupChat);
                        String decryptedMessage = CryptoUtil.decrypt(msfFromGroupChat);
                        System.out.println(decryptedMessage);
                    } catch (Exception e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your username for group chat");
        String username = sc.nextLine();

        // Ask for the Server's IP
        System.out.println("Enter the Server IP Address (type 'localhost' if testing on the same PC):");
        String ipAddress = sc.nextLine();

        Socket socket = new Socket(ipAddress,1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }

}
