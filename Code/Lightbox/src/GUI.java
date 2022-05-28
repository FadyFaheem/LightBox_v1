import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI extends Main {

    public static JButton buttonSetup(String buttonText, int fontSize, int x, int y, int width, int height, boolean enabled){
        JButton button = new JButton();
        button.setText(buttonText); // sets Button Text
        button.setBounds(x,y,width,height);  // Creates bounds for button
        button.setFont(new Font("Myriad", Font.PLAIN, fontSize)); // Sets font to Myriad (because it looks better)
        button.setForeground(Color.BLACK); // Sets text font to white
        button.setFocusPainted(false); // removes ugly box around text
        button.setBackground(Color.decode("#ff8719")); // Changes color button to orange >:)
        button.addMouseListener(hoverColor); // Changes the button pressed color to a darker orange instead of Metal L&F
        button.setEnabled(enabled);
        return button;
    }

    static MouseAdapter hoverColor = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent m){
            UIManager.put("Button.select", Color.decode("#ff8719").darker());
        }
    };

    public static JLabel labelSetup(String text, int fontSize, int x, int y, int width, int height, boolean enabled){
        JLabel newLabel = new JLabel(text, SwingConstants.CENTER);
        newLabel.setBounds(x,y,width,height);
        newLabel.setFont(new Font("Myriad", Font.PLAIN, fontSize));
        newLabel.setForeground(Color.white);
        newLabel.setEnabled(enabled);
        return newLabel;
    }
}
