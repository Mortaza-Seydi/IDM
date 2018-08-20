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

package sample.downloadCore;

import javafx.concurrent.Task;
import sample.DatabaseManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

public class Downloader extends Task<Integer>
{
    private static final int BUFFER_SIZE = 4096;

    private HttpURLConnection httpConn;
    private DownloadedFile download;
    private boolean firstRun;

    private final Object object = new Object(); // Monitor
    private boolean pause = false;

    public Downloader(DownloadedFile downloadedFile, boolean firstRun)
    {
        this.firstRun = firstRun;
        this.download = downloadedFile;
        download.setDownloader(this);
        try
        {
            URL url = new URL(downloadedFile.getUrl());
            httpConn = (HttpURLConnection) url.openConnection(); // Open Connection
        }
        catch (IOException e)
        {
            updateMessage("There Was An Error While Connecting");
        }
    }

    @Override
    protected Integer call()
    {
        try
        {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(download.getSavedPath());

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesRead = 0;
            int fileSize = httpConn.getContentLength();

            updateMessage("Download Started");

            synchronized (object)
            {
                while ((bytesRead = inputStream.read(buffer)) != - 1) // Read The Bytes
                {
                    if (pause)
                        object.wait(); // Wait Thread
                    else
                    {
                        fileOutputStream.write(buffer, 0, bytesRead); // Write To File
                        totalBytesRead += bytesRead;
                        download.setStatus((totalBytesRead * 100) / fileSize); // Progress
                    }
                }
            }

            download.setStatus(100);

            updateMessage("Download Finished");

            /*
             * Close Streams And Connections
             */
            fileOutputStream.close();
            inputStream.close();
            httpConn.disconnect();

            return (int) download.getStatus();

        }
        catch (IOException e)
        {
            updateMessage("Can Not Download The File");
        }
        catch (InterruptedException e)
        {
            updateMessage("Can Not Pause Or Resume Download");
        }

        return (int) download.getStatus();
    }

    @Override
    protected void succeeded() // Calls When Task Finished Successfully
    {
        if (firstRun)
        {
            try
            {
                DatabaseManager.insert(download); // Insert Into DataBase
            }

            catch (SQLException e)
            {
                updateMessage(e.getMessage() + " Sql status : " + e.getSQLState());
            }
        }
    }

    public void pause()
    {
        pause = true; // The Thread States Flag
    }

    public void resume()
    {
        pause = false; // The Thread States Flag

        synchronized (object)
        {
            object.notify(); // Resume Thread
        }
    }

    public boolean isPause()
    {
        return pause;
    }
}