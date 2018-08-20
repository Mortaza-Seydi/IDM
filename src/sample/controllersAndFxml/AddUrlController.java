/*
 *  In The Name of ALLAH
 *
 *  Written by: Mortaza Seydi - Zanjan University - Spring 2018
 *
 *  Internet Download Manager (IDM)
 *
 *  Github : https://github.com/Mortaza-Seydi
 *
 */

package sample.controllersAndFxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import sample.downloadCore.DownloadedFile;
import sample.downloadCore.GetFileInfo;

import java.io.File;
import java.io.IOException;

import java.net.URL;

public class AddUrlController
{
    @FXML private TextField urlField;
    @FXML private TextField directory;

    private Controller controller;

    public void setController(Controller controller)
    {
        this.controller = controller;
    }

    @FXML public void showFileChooser() // Select The File Path
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Directory");
        File file = directoryChooser.showDialog(null);

        if (file != null)
            directory.setText(file.getAbsolutePath());
    }

    @FXML public void startDownload(ActionEvent actionEvent)
    {
        doYourJob(actionEvent, 0);
    }

    @FXML public void addDownloadToList(ActionEvent actionEvent)
    {
        doYourJob(actionEvent, 1);
    }

    private void doYourJob(ActionEvent actionEvent, int operation) // Start Download Or Add It On Table To Download Later
    {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

        try
        {
            URL url = new URL(urlField.getText());

            if (urlField.getText().equals(""))
                throw new IOException("Url is Empty");

            if (directory.getText().equals(""))
                throw new IOException("directory is Empty");

            /*
             * Get File Details
             */
            String[] fileDetails = GetFileInfo.GetFileInformation(url);
            String details = "Name : " + fileDetails[0] + "\nSize : " + fileDetails[1];
            String path = directory.getText() + "/" + fileDetails[0];

            path = path.replace("\\", "/");

            DownloadedFile downloadedFile = new DownloadedFile(fileDetails[0], fileDetails[1], 0, url.toString(), path);

            /*
             * Show Alert
             */
            Alert alert = new Alert(Alert.AlertType.INFORMATION, details, ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK)
            {
                if (operation == 0)
                    controller.requestForDownload(downloadedFile);
                else
                    controller.requestForAdd(downloadedFile);
            }
        }

        catch (IOException e) // Show Errors
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK)
            {
                stage.show();
            }
        }

        finally
        {
            urlField.setText("");
            directory.setText("");
        }
    }
}
