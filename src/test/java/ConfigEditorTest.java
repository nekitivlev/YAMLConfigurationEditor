import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import org.example.ConfigEditor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigEditorTest {

  private ConfigEditor configEditor;

  @BeforeEach
  public void setup() {
    ArrayList<String> filenames = new ArrayList<>(Arrays.asList("config1.yaml", "config2.yaml"));
    configEditor = new ConfigEditor(filenames);
  }

  @Test
  public void testLoadConfigFiles()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method loadConfigFilesMethod = ConfigEditor.class.getDeclaredMethod("loadConfigFiles",
        ArrayList.class);
    loadConfigFilesMethod.setAccessible(true);

    ArrayList<String> filenames = new ArrayList<>(Arrays.asList("config1.yaml"));

    Map<String, Object> expectedConfig = new HashMap<>();
    expectedConfig.put("key1", "value1");
    expectedConfig.put("key2", "value2");

    Map<String, Object> actualConfig = (Map<String, Object>) loadConfigFilesMethod.invoke(
        configEditor, filenames);

    Assertions.assertEquals(expectedConfig, actualConfig);
  }

  @Test
  public void testAddConfigToNode()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method addConfigToNodeMethod = ConfigEditor.class.getDeclaredMethod("addConfigToNode",
        Map.class, DefaultMutableTreeNode.class);
    addConfigToNodeMethod.setAccessible(true);

    Map<String, Object> config = new HashMap<>();
    config.put("key1", "value1");
    config.put("key2", new HashMap<>());

    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");

    addConfigToNodeMethod.invoke(configEditor, config, rootNode);

    DefaultMutableTreeNode key1Node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
    DefaultMutableTreeNode key2Node = (DefaultMutableTreeNode) rootNode.getChildAt(1);

    Assertions.assertEquals("key1", key1Node.getUserObject());
    Assertions.assertEquals("key2", key2Node.getUserObject());
  }

  @Test
  public void testAddConfigKey()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    Method addConfigKeyMethod = ConfigEditor.class.getDeclaredMethod("addConfigKey");
    addConfigKeyMethod.setAccessible(true);

    Field keyField = ConfigEditor.class.getDeclaredField("keyField");
    keyField.setAccessible(true);
    keyField.set(configEditor, new JTextField("key1.key2"));

    Field valueField = ConfigEditor.class.getDeclaredField("valueField");
    valueField.setAccessible(true);
    valueField.set(configEditor, new JTextField("value"));

    Map<String, Object> config = new HashMap<>();
    Field configField = ConfigEditor.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(configEditor, config);

    addConfigKeyMethod.invoke(configEditor);

    Map<String, Object> expectedConfig = new HashMap<>();
    expectedConfig.put("key1", new HashMap<String, Object>() {{
      put("key2", "value");
    }});

    Assertions.assertEquals(expectedConfig, config);
  }

  @Test
  public void testSaveConfig()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    Method saveConfigMethod = ConfigEditor.class.getDeclaredMethod("saveConfig");
    saveConfigMethod.setAccessible(true);

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(new File("config.yaml"));
    Field configField = ConfigEditor.class.getDeclaredField("config");
    configField.setAccessible(true);
    configField.set(configEditor, new HashMap<String, Object>() {{
      put("key1", "value1");
      put("key2", "value2");
    }});

    saveConfigMethod.invoke(configEditor);

    Assertions.assertEquals(new File("config.yaml"), fileChooser.getSelectedFile());
  }
}
