# Config Editor

Config Editor is a Java application that allows users to edit YAML configuration files through a graphical user interface (GUI). It provides a tree view of the YAML file(s) and allows users to add, delete, and modify key-value pairs in the configuration.

## Features

- Load one or more YAML files into the application.
- Display the loaded configuration in a tree view.
- Add new key-value pairs to the configuration.
- Delete existing key-value pairs from the configuration.
- Save the modified configuration back to a YAML file.

## Prerequisites

- Java Development Kit (JDK) 11 or later.
- Apache Maven 3.8.1 or later.

## Getting Started

1. Clone the repository: `git clone https://github.com/example/config-editor.git`
2. Navigate to the project directory: `cd config-editor`
3. Build the project using Maven: `mvn clean package`
4. Run the application: `java -jar target/config-editor-1.0.0.jar`
5. The File Selection Window will open.
6. Click the "Open" button to select one or more YAML files.
7. The Config Editor window will open, displaying the loaded configuration.
8. Use the GUI controls to edit the configuration.
9. Click the "Save" button to save the modified configuration to a YAML file.

## Dependencies

- [snakeyaml](https://bitbucket.org/asomov/snakeyaml): A YAML parser and emitter for Java.
- [junit](https://junit.org/junit4/): A testing framework for Java.