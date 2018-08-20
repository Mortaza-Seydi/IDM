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

public class DownloadedFile
{
    private String fileName;
    private String size;
    private double status;
    private String url;
    private String savedPath;


    private Downloader downloader;

    public void setDownloader(Downloader downloader)
    {
        this.downloader = downloader;
    }
    public Downloader getDownloader()
    {
        return downloader;
    }

    public DownloadedFile(String fileName, String size, double status, String url, String savedPath)
    {
        this.fileName = fileName;
        this.size = size;
        this.status = status;
        this.url = url;
        this.savedPath = savedPath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getSize()
    {
        return size;
    }

    public String getUrl()
    {
        return url;
    }

    public String getSavedPath()
    {
        return savedPath;
    }

    public double getStatus()
    {
        return status;
    }

    public void setStatus(double status)
    {
        this.status = status;
    }
}
