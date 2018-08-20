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

package sample;

import javafx.collections.ObservableList;
import sample.downloadCore.DownloadedFile;
import sample.downloadCore.Downloader;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager
{
    private static final String url = "jdbc:mysql://localhost:3306/download?useSSL=false";
    private static final String user = "root";
    private static final String password = "1234";

    public static void insert (DownloadedFile download) throws SQLException
    {
        /*
         * Get Connection And Create A SQL Statement
         */
        Connection dbConnection = DriverManager.getConnection (url, user, password);
        Statement statement = dbConnection.createStatement ();

        /*
         * Store A SQL INSERT Statement Into A String
         */
        String s = String.format ("INSERT INTO download.download values ('%s', '%s', %f, '%s', '%s')",
                download.getFileName (), download.getSize (), download.getStatus (),
                download.getUrl (), download.getSavedPath ());

        statement.execute (s); // Run SQL Statement
        dbConnection.close (); // Close Connection
    }

    public static ArrayList<DownloadedFile> read () throws SQLException
    {
        /*
         * Get Connection And Create A SQL Statement
         */
        Connection dbConnection = DriverManager.getConnection (url, user, password);
        Statement statement = dbConnection.createStatement ();

        /*
         * Get The Query Results
         */
        ResultSet resultSet = statement.executeQuery ("SELECT * FROM download.download");

        ArrayList<DownloadedFile> downloads = new ArrayList<> ();

        if (resultSet.first ())
        {
            do
            {
                /*
                 * Define New DownloadFile And New Downloader
                 */

                DownloadedFile downloadedFile = new DownloadedFile (
                        resultSet.getString (1),
                        resultSet.getString (2),
                        resultSet.getDouble (3),
                        resultSet.getString (4),
                        resultSet.getString (5));

                new Downloader(downloadedFile, false);

                downloads.add(downloadedFile); // Add To Array List

            } while (resultSet.next ());
        }

        dbConnection.close (); // Close Connection
        return downloads;
    }

    public static void update (ObservableList<DownloadedFile> list) throws SQLException
    {
        /*
         * Get Connection And Create A SQL Statement
         */
        Connection dbConnection = DriverManager.getConnection (url, user, password);
        Statement statement = dbConnection.createStatement ();

        /*
         * Delete Every Thing On Table
         */
        statement.execute ("DELETE FROM download.download");
        dbConnection.close (); // Close Connection

        for (DownloadedFile downloaded : list)
        {
            /*
             * Insert New DownloadFiles (Or Update) On DataBase
             */
            DatabaseManager.insert (downloaded);
        }
    }
}