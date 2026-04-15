import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SecureChatClient {
    // --- UI Variables ---
    private JFrame window;
    private JPanel chatHistoryPanel;
    private JTextField messageInput;
    private JButton sendButton;
    private JScrollPane scrollPane;

    // --- Network Variables ---
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // 1. The Constructor: Sets up network, then builds UI, then starts listening
    public SecureChatClient(Socket socket, String username) {
        this.socket = socket;
        this.username = username;

        try {
            // Setup Data Streams
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Build the GUI
            initUI();

            // Send our username to the server immediately upon connecting
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Start the background listening thread
            listenForNetwork();

        } catch (IOException e) {
            closeApp();
        }
    }

    // 2. The UI Builder
    private void initUI() {
        window = new JFrame("iChat - " + username);
        window.setSize(450, 650);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.getContentPane().setBackground(new Color(236, 229, 221));

        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(new Color(236, 229, 221));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(236, 229, 221));
        wrapperPanel.add(chatHistoryPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        window.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        messageInput = new JTextField();
        messageInput.setFont(new Font("SansSerif", Font.PLAIN, 16));

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(7, 94, 84));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        window.add(bottomPanel, BorderLayout.SOUTH);

        // --- BUTTON ACTION: Send to Network ---
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = messageInput.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        // 1. Show it on our own screen
                        displayMessage(text, true);

                        // 2. Encrypt and send to the server
                        String fullMsg = username + ": " + text;
                        String encryptedMessage = CryptoUtil.encrypt(fullMsg);
                        bufferedWriter.write(encryptedMessage);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        // 3. Clear the box
                        messageInput.setText("");
                    } catch (Exception ex) {
                        closeApp();
                    }
                }
            }
        };

        sendButton.addActionListener(sendAction);
        messageInput.addActionListener(sendAction);

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    // 3. The Chat Bubble Builder (Left/Right)
    public void displayMessage(String message, boolean isMe) {
        JPanel chatRow = new JPanel(new BorderLayout());
        chatRow.setBackground(new Color(236, 229, 221));
        chatRow.setBorder(new EmptyBorder(5, 10, 5, 10));

        String timeString = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String bubbleText = "<html><div style='width: 200px;'>" +
                "<p style='margin-top: 0; margin-bottom: 2px;'>" + message + "</p>" +
                "<p style='text-align: right; margin: 0; font-size: 9px; color: #666666;'>" + timeString + "</p>" +
                "</div></html>";

        JLabel bubble = new JLabel(bubbleText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bubble.setFont(new Font("SansSerif", Font.PLAIN, 15));
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 15, 10, 15));

        if (isMe) {
            bubble.setBackground(new Color(220, 248, 198));
            chatRow.add(bubble, BorderLayout.EAST);
        } else {
            bubble.setBackground(Color.WHITE);
            chatRow.add(bubble, BorderLayout.WEST);
        }

        updatePanel(chatRow);
    }

    // 4. The Server Notification Builder (Centered, Blue)
    public void showNotification(String message) {
        // FlowLayout centers the item inside the row
        JPanel alertRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        alertRow.setBackground(new Color(236, 229, 221));

        JLabel alertBubble = new JLabel(message) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        alertBubble.setFont(new Font("SansSerif", Font.BOLD, 12));
        alertBubble.setForeground(Color.WHITE);
        alertBubble.setBackground(new Color(135, 158, 171)); // Soft Blue/Grey
        alertBubble.setOpaque(false);
        alertBubble.setBorder(new EmptyBorder(5, 15, 5, 15));

        alertRow.add(alertBubble);
        updatePanel(alertRow);
    }

    // Helper to refresh the UI
    private void updatePanel(JPanel rowToAdd) {
        chatHistoryPanel.add(rowToAdd);
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    // 5. The Network Listener Thread
    public void listenForNetwork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String encryptedMsgFromNetwork;
                while (socket.isConnected()) {
                    try {
                        encryptedMsgFromNetwork = bufferedReader.readLine();
                        if (encryptedMsgFromNetwork == null) {
                            closeApp();
                            break;
                        }

                        // Decrypt the incoming message
                        String decryptedMessage = CryptoUtil.decrypt(encryptedMsgFromNetwork);

                        // THE ROUTER: Is it a server alert or a normal message?
                        if (decryptedMessage.startsWith("SERVER:")) {
                            // Strip out the "SERVER:" part and show the alert
                            String cleanAlert = decryptedMessage.replace("SERVER:", "").trim();
                            showNotification(cleanAlert);
                        } else {
                            displayMessage(decryptedMessage, false);
                        }

                    } catch (Exception e) {
                        closeApp();
                        break;
                    }
                }
            }
        }).start();
    }

    // Safely close resources
    public void closeApp() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 6. The Main Launcher (Upgraded GUI Login)
    public static void main(String[] args) {
        // 1. Create a mini-panel to hold our login inputs
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField usernameField = new JTextField(15);
        JTextField ipField = new JTextField("localhost", 15);

        // Add labels and text boxes to the panel
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Server IP Address:"));
        loginPanel.add(ipField);

        // 2. Show a sleek popup window instead of the terminal
        int result = JOptionPane.showConfirmDialog(null, loginPanel,
                "iChat Secure Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // 3. If the user clicks "OK", launch the app
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String ipAddress = ipField.getText().trim();

            // Validate inputs so the app doesn't crash
            if (username.isEmpty() || ipAddress.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username and IP cannot be empty!", "Login Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop the launch
            }

            try {
                // Connect to the network
                Socket socket = new Socket(ipAddress, 1234);
                // Launch the main Chat window
                new SecureChatClient(socket, username);
            } catch (IOException e) {
                // Show a visual error if the server is offline!
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to the server at " + ipAddress + "\nMake sure the Server is running!",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}