package org.example;


import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileSelectionWindow extends JFrame {

  private final JFileChooser fileChooser;

  /**
   * Create the application.
   */
  public FileSelectionWindow() {
    super("File Selection Window");

    // create the file chooser
    fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.setFileFilter(new FileNameExtensionFilter("YAML files", "yaml"));

    // create the button to open the file chooser
    JButton openButton = new JButton("Open");
    openButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int result = fileChooser.showOpenDialog(FileSelectionWindow.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          // get the selected files
          ArrayList<File> files = new ArrayList<>(List.of(fileChooser.getSelectedFiles()));
          // create a new ConfigEditor window and load the selected files
          ConfigEditor editor = new ConfigEditor(getFilePaths(files));
          editor.setVisible(true);
          setVisible(false);
        }
      }
    });

    // add the button to the window
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(openButton);
    add(panel);

    // set window properties
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 200);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  /**
   * Get the absolute paths of the selected files
   */
  private ArrayList<String> getFilePaths(ArrayList<File> files) {
    ArrayList<String> filePaths = new ArrayList<>();
    for (File file : files) {
      filePaths.add(file.getAbsolutePath());
    }
    return filePaths;
  }

  public static void main(String[] args) {
    new FileSelectionWindow();
  }


}
