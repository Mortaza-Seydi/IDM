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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import sample.DatabaseManager;
import sample.downloadCore.DownloadedFile;
import sample.downloadCore.Downloader;

import java.sql.SQLException;
import java.util.ArrayList;

public class Controller
{
    @FXML private TableView<DownloadedFile> table;
    private ObservableList<DownloadedFile> list;

    @FXML private TextArea statusBar;
    @FXML private TextArea statusBar1;

    @FXML private TableColumn<DownloadedFile, String> filename;
    @FXML private TableColumn<DownloadedFile, String> size;
    @FXML private TableColumn<DownloadedFile, Integer> status;
    @FXML private TableColumn<DownloadedFile, String> url;
    @FXML private TableColumn<DownloadedFile, String> savedDir;

    private Stage addUrlStage = new Stage();

    public void setStage(Scene scene)
    {
        addUrlStage.setTitle("Add Url");
        addUrlStage.setScene(scene);
        addUrlStage.setResizable(false);
    }

    public void initialize()
    {
        /*
         * Set The Cell Value Factory
         */
        filename.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        url.setCellValueFactory(new PropertyValueFactory<>("url"));
        savedDir.setCellValueFactory(new PropertyValueFactory<>("savedPath"));

        try
        {
            /*
             * Add Downloads From Database To Table
             */
            ArrayList<DownloadedFile> downloads;
            downloads = DatabaseManager.read();
            list = FXCollections.observableArrayList(downloads);
        }

        catch (SQLException e)
        {
            statusBar1.setText(e.getMessage() + " Sql status : " + e.getSQLState()); // Show Exception
        }

        table.setItems(list);

    }

    @FXML public void ShowAddUrlWindow()
    {
        addUrlStage.show();
    }

    void requestForDownload(DownloadedFile downloadedFile)
    {
        list.add(downloadedFile);
        download(downloadedFile, true);
    }

    private void download(DownloadedFile downloadedFile, boolean firstRun) // Start Download Using Thread
    {
        Downloader downloader = new Downloader(downloadedFile, firstRun);
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        downloader.setOnRunning((event) -> table.refresh());

        Thread thread = new Thread(downloader);
        thread.setDaemon(true);
        thread.start();

        statusBar.textProperty().bind(downloader.messageProperty());
    }

    void requestForAdd(DownloadedFile downloadedFile)
    {
        list.add(downloadedFile);
    }

    public void reStartDownload()
    {
        DownloadedFile downloadedFile = table.getSelectionModel().getSelectedItem();
        download(downloadedFile, false);
    }

    public void stageClosed()
    {
        try
        {
            for (DownloadedFile download : list)
                if (download.getStatus() != 100)
                {
                    download.setStatus(0);
                    download.getDownloader().pause(); // Pause Running Downloads
                }

            DatabaseManager.update(list);
        }
        catch (SQLException e)
        {
            statusBar1.setText(e.getMessage() + " Sql status : " + e.getSQLState());
        }
    }

    public void deleteFromTable()
    {
        DownloadedFile download = table.getSelectionModel().getSelectedItem();

        if (download.getDownloader().isRunning())
            statusBar1.setText("Can Not Delete Running Download");

        else
            list.remove(download);
    }

    public void requestForResume()
    {
        DownloadedFile downloadedFile = table.getSelectionModel().getSelectedItem();
        if (downloadedFile.getDownloader().isPause())
        {
            downloadedFile.getDownloader().resume();
            statusBar1.setText("Download Resumed");
        }

        else
            download(downloadedFile, false);
    }

    public void requestForPause()
    {
        DownloadedFile downloadedFile = table.getSelectionModel().getSelectedItem();

        if (!downloadedFile.getDownloader().isPause())
        {
            downloadedFile.getDownloader().pause();
            statusBar1.setText("download Paused");
        }

        else
            statusBar1.setText("Download is Not Running You Cant Pause it");
    }
}