import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    private JButton startButton;

    public Main() {
        GUISetup();
    }

    public void GUISetup(){


        // CONTAINER SETUP

        Container MainWindow = getContentPane(); //need this to make JFrame work well
        MainWindow.setLayout(null); //do not use any layout managers
        MainWindow.setBackground(Color.white); //make the background of the window dark gray
        setDefaultCloseOperation(EXIT_ON_CLOSE); //actually end the program when clicking the close button
        setTitle("NTexcal Lightbox");//text for the window's title bar
        setResizable(false);//don't allow the user to resize the window
        setSize(1024,600);//set the size of the window to half the screen width and half the screen height//where to position the top left corner of the window

        // BUTTON SETUP

        startButton = GUI.buttonSetup("Start", 60, 700, 145, 200, 100); // Initates Button for setup and adds text
        startButton.addActionListener(this);// Adds ActionListener so buttons are *clickable*
        MainWindow.add(startButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        Main bob = new Main();
        bob.setVisible(true);
    }

}
