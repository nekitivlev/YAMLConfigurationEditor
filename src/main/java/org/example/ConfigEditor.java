package org.example;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import org.yaml.snakeyaml.Yaml;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.Map;

public class ConfigEditor extends JFrame {

  private final JTree configTree;
  private final DefaultTreeModel treeModel;
  private final JTextField keyField;
  private final JTextField valueField;
  private final JButton deleteButton;
  private final Map<String, Object> config;

  /**
   * Create the application.
   */
  public ConfigEditor(ArrayList<String> filenames) {
    super("Config Editor");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Load config file(s)
    config = loadConfigFiles(filenames);

    // Create tree view of config
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Yaml File(s)");
    addConfigToNode(config, rootNode);
    treeModel = new DefaultTreeModel(rootNode);
    configTree = new JTree(treeModel);
    configTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    configTree.setShowsRootHandles(true);
    JScrollPane scrollPane = new JScrollPane(configTree);
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    // Create key and value input fields and add button
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(1, 3));
    keyField = new JTextField("Key (must be not-empty)");
    valueField = new JTextField("Value");
    inputPanel.add(keyField);
    inputPanel.add(valueField);
    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addConfigKey());
    inputPanel.add(addButton);
    getContentPane().add(inputPanel, BorderLayout.NORTH);

    // Create delete button
    deleteButton = new JButton("Delete");
    deleteButton.setEnabled(false);
    deleteButton.addActionListener(e -> deleteConfigKey());
    getContentPane().add(deleteButton, BorderLayout.SOUTH);

    // Create save button
    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> saveConfig());
    getContentPane().add(saveButton, BorderLayout.EAST);

    // Add tree selection listener to enable/disable delete button
    configTree.addTreeSelectionListener(e -> {
      TreePath selectedPath = configTree.getSelectionPath();
      deleteButton.setEnabled(selectedPath != null && selectedPath.getPathCount() > 1);
    });

    // Show window
    pack();
    setVisible(true);
  }

  /**
   * Load config file(s) into a map
   */
  private Map<String, Object> loadConfigFiles(ArrayList<String> filenames) {
    Map<String, Object> result = null;
    for (String filename : filenames) {
      String newFilename = filename.replace("\\", "/").replace("[", "").replace("]", "");
      Path path = Paths.get(newFilename);
      try (InputStream inputStream = new FileInputStream(path.toFile())) {
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(inputStream);
        if (result == null) {
          result = config;
        } else {
          result.putAll(config);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * Add config to a tree node
   */
  private void addConfigToNode(Map<String, Object> config, DefaultMutableTreeNode node) {
    Objects.requireNonNull(config, "Config is null");
    for (Map.Entry<String, Object> entry : config.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
      if (value instanceof Map) {
        addConfigToNode((Map<String, Object>) value, childNode);
      }
      node.add(childNode);
    }
  }

  /**
   * Add a key to the config
   */
  private void addConfigKey() {
    String keyPath = keyField.getText();
    if(keyPath.isEmpty()) {
      return;
    }
    ArrayList<String> keys = new ArrayList<>(List.of(keyPath.split("\\.")));
    Map<String, Object> node = config;
    for (int i = 0; i < keys.size() - 1; i++) {
      if (!node.containsKey(keys.get(i))) {
        node.put(keys.get(i), new java.util.LinkedHashMap<>());
      }
      node = (Map<String, Object>) node.get(keys.get(i));
    }
    node.put(keys.get(keys.size() - 1), valueField.getText());
    treeModel.reload();
  }

  /**
   * Delete a key from the config
   */
  private void deleteConfigKey() {
    TreePath selectedPath = configTree.getSelectionPath();
    Objects.requireNonNull(selectedPath, "Selected path is null");
    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
    StringBuilder keyPath = new StringBuilder();
    for (int i = 1; i < selectedPath.getPathCount(); i++) {
      keyPath.append(selectedPath.getPathComponent(i)).append(".");
    }
    keyPath = new StringBuilder(keyPath.substring(0, keyPath.length() - 1));
    ArrayList<String> keys = new ArrayList<>(List.of(keyPath.toString().split("\\.")));
    Map<String, Object> node = config;
    for (int i = 0; i < keys.size() - 1; i++) {
      node = (Map<String, Object>) node.get(keys.get(i));
    }
    node.remove(keys.get(keys.size() - 1));
    treeModel.removeNodeFromParent(selectedNode);
  }

  /**
   * Save the config to a file
   */
  private void saveConfig() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      try (Writer writer = new FileWriter(file)) {
        Yaml yaml = new Yaml();
        yaml.dump(config, writer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
