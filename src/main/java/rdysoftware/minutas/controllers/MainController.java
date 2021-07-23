/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  java.io.File
 *  java.io.FilenameFilter
 *  java.io.PrintStream
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.net.URL
 *  java.util.Collection
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.ResourceBundle
 */
package rdysoftware.minutas.controllers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import rdysoftware.minutas.ImageReader;

public class MainController
        implements Initializable {
    private ObservableList<String> list = FXCollections.observableArrayList();
    private String[] filesNames;
    private String path = null;
    @FXML
    private TextField textFieldNovoNome;
    @FXML
    private ScrollPane scroollPane;
    @FXML
    private Label labelQuantArquivos;
    @FXML
    private Label labelDirectory;
    @FXML
    private Button button;
    @FXML
    private BorderPane anchorPane;
    @FXML
    private ListView<String> listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        this.configuringDirectoryChooser(directoryChooser);
        this.listView.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> {
            int index = this.listView.getSelectionModel().getSelectedIndex() + 1;
            this.loadImage((String) new_val);
            System.out.println(new_val);
            this.listView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F2) {
                    this.textFieldNovoNome.setText(new_val.replaceFirst("[.][^.]+$", ""));
                    this.textFieldNovoNome.requestFocus();
                    this.textFieldNovoNome.setOnKeyPressed(event1 -> {
                        String novoNome = this.textFieldNovoNome.getText();
                        if (event1.getCode() == KeyCode.ENTER) {
                            this.renameFile(new_val, novoNome);
                            this.listView.requestFocus();
                            this.listView.getSelectionModel().select(index);
                            System.out.println(index);
                        }
                    });
                } else if (event.getCode() == KeyCode.F9) {
                    this.deleteFile(new_val);
                }
            });
        });
        this.anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                this.loadData();
            }
        });
        this.button.setOnAction(event -> {
            File dir = directoryChooser.showDialog(null);
            if (dir != null) {
                this.labelDirectory.setText(dir.getAbsolutePath());
                this.path = dir.getAbsolutePath() + "/";
                this.loadData();
            } else {
                this.labelDirectory.setText(null);
            }
        });
    }

    private void loadData() {
        if (this.path == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Confirmacao");
            alert.setContentText("Selecione a pasta antes!");
            alert.show();
            return;
        }
        int quantArquivos = 0;
        this.list.removeAll(this.list);
        this.listView.getItems().clear();
        File file = new File(this.path);
        FilenameFilter filter = (f, name) -> name.endsWith(".jpg");
        this.filesNames = file.list(filter);
        for (String items : (String[]) Objects.requireNonNull((Object) this.filesNames)) {
            this.list.add(items);
            ++quantArquivos;
        }
        this.listView.getItems().addAll(this.list);
        this.labelQuantArquivos.setText(quantArquivos + " arquivos");
        this.listView.getSelectionModel().select(0);
    }

    private void deleteFile(String nameFile) {
        File file = new File(this.path + nameFile);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirma\u00e7\u00e3o");
        alert.setHeaderText("Voce esta prestes a excluir o arquivo " + nameFile);
        alert.setContentText("Continuar?");
        boolean b = false;
        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            b = file.delete();
        }
        if (b) {
            System.out.println("Exclu\u00eddo com sucesso!");
            this.loadData();
        } else {
            System.out.println("Erro ao excluir arquivo!");
        }
    }

    private void renameFile(String fileName, String newFileName) {
        File newFile;
        File file = new File(this.path + fileName);
        boolean b = file.renameTo(newFile = new File(this.path + newFileName + ".jpg"));
        if (b) {
            System.out.println("Renomeado com sucesso!");
            this.loadData();
        } else {
            System.out.println("Erro ao renomear arquivo!");
        }
    }

    private void loadImage(String fileName) {
        Image img = new Image("file:///" + this.path + fileName);
        ImageView imageView1 = new ImageView(img);
        imageView1.setImage(img);
        this.scroollPane.setContent(imageView1);

        System.out.println(
                ImageReader.extractImage("file:///"+path +"image.jpeg"));
    }

    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Selecione a pasta");
        directoryChooser.setInitialDirectory(new File(System.getProperty((String) "user.home")));
    }
}

