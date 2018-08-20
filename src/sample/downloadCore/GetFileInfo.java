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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetFileInfo
{
    public static String[] GetFileInformation(URL url) throws IOException
    {
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) // Check For The File Existence
        {
            /*
             * Try To Get The File Name
             */
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String fileName = url.toString().substring(url.toString().lastIndexOf("/") + 1, url.toString().length());

            if (disposition != null)
            {
                int index = disposition.indexOf("filename=");

                if (index > 0)
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
            }

            double fileSize = httpConn.getContentLength(); // Get The File Size
            httpConn.disconnect(); // Disconnect

            String[] b = new String[2];

            b[0] = fileName.replace("%20", " "); // Replace "%20"
            b[1] = humanReadableByteCount((long) fileSize, false); // Convert To Readable Size

            return b;

        }

        else
            throw new IOException("The File Does Not Exist");
    }

    /*
     * This Method Converts The Bytes To KB Or MB (Copied From StackOverFlow.com)
     */
    public static String humanReadableByteCount(long bytes, boolean si)
    {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
