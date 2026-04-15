import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatGUI {
    private JFrame window;
    private JPanel chatHistoryPanel; // Replaced JTextArea with a dynamic Panel
    private JTextField messageInput;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private String username;

    public ChatGUI(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        // 1. The Main Window
        window = new JFrame("P2P Secure Chat - " + username);
        window.setSize(450, 650);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.getContentPane().setBackground(new Color(236, 229, 221)); // WhatsApp Background Color

        // 2. The Chat History Area (Dynamically stacking boxes)
        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(new Color(236, 229, 221));

        // --- THE FIX: The Wrapper Panel ---
        // This panel locks the chat history to the top (NORTH) so it doesn't stretch
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(236, 229, 221));
        wrapperPanel.add(chatHistoryPanel, BorderLayout.NORTH);

        // Put the wrapper inside the scroll pane instead of the chat panel directly
        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null); // Removes ugly default borders
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        window.add(scrollPane, BorderLayout.CENTER);

        // 3. The Bottom Input Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding around input
        bottomPanel.setBackground(Color.WHITE);

        messageInput = new JTextField();
        messageInput.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Inner padding for text
        ));

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(7, 94, 84)); // WhatsApp Dark Green
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        window.add(bottomPanel, BorderLayout.SOUTH);

        // 4. THE FIX: Action Listener for BOTH Button and 'Enter' Key
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = messageInput.getText().trim();
                if (!text.isEmpty()) {
                    // Display our own message (isMe = true)
                    displayMessage(text, true);

                    // Simulate receiving a reply 1 second later (for testing)
                    // You will replace this with your actual network receiving logic later
                    SwingUtilities.invokeLater(() -> {
                        displayMessage("This is a test reply!", false);
                    });

                    messageInput.setText(""); // Clear the input box
                }
            }
        };

        // Attach the exact same action to both the button AND the text field (Enter key)
        sendButton.addActionListener(sendAction);
        messageInput.addActionListener(sendAction);

        // 5. Show the window
        window.setLocationRelativeTo(null); // Center on screen
        window.setVisible(true);
    }

    // --- THE MAGIC METHOD: WhatsApp Style Bubbles ---
    public void displayMessage(String message, boolean isMe) {
        // Create a horizontal row for the message
        JPanel chatRow = new JPanel(new BorderLayout());
        chatRow.setBackground(new Color(236, 229, 221)); // Match background transparently
        chatRow.setBorder(new EmptyBorder(5, 10, 5, 10)); // Spacing between messages

        // 1. Grab the current time and format it (e.g., "05:11 PM")
        String timeString = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

        // 2. Build the new HTML structure: Message on top, Time on bottom right
        String bubbleText = "<html><div style='width: 200px;'>" +
                "<p style='margin-top: 0; margin-bottom: 2px;'>" + message + "</p>" +
                "<p style='text-align: right; margin: 0; font-size: 9px; color: #666666;'>" + timeString + "</p>" +
                "</div></html>";

        // Create the actual text bubble with a custom paintbrush
        JLabel bubble = new JLabel(bubbleText) {
            @Override
            protected void paintComponent(Graphics g) {
                // Upgrade the standard paintbrush to a 2D modern paintbrush
                Graphics2D g2 = (Graphics2D) g.create();

                // Turn on Anti-Aliasing (Smooths out the jagged, pixelated edges)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set the brush color to whatever we set the bubble background to
                g2.setColor(getBackground());

                // Draw the rounded rectangle! (x, y, width, height, cornerRadiusX, cornerRadiusY)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Now tell Java to paint the text on top of our new background
                super.paintComponent(g2);
                g2.dispose(); // Throw away the brush to save memory
            }
        };

        bubble.setFont(new Font("SansSerif", Font.PLAIN, 15));
        // CRITICAL: We must turn this to FALSE so Java doesn't paint its default ugly square behind our curves!
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 15, 10, 15));

        if (isMe) {
            bubble.setBackground(new Color(220, 248, 198)); // Light Green for Me
            chatRow.add(bubble, BorderLayout.EAST); // Push to Right
        } else {
            bubble.setBackground(Color.WHITE); // White for Them
            chatRow.add(bubble, BorderLayout.WEST); // Push to Left
        }

        // Add the row to the main history panel
        chatHistoryPanel.add(chatRow);
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();

        // Auto-scroll to the bottom so we always see the newest message
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public static void main(String[] args) {
        new ChatGUI("Het");
    }
}