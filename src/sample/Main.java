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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import sample.controllersAndFxml.AddUrlController;
import sample.controllersAndFxml.Controller;

import java.io.FileInputStream;
import java.io.IOException;

public class Main extends Application
{
    private Controller controller;
    private AddUrlController addUrlController;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        /*
         * Define Two Scenes
         */
        Scene mainScene = defineMainStage();
        Scene addUrlScene = defineAddUrlStage();

        /*
         * Two Controller Classes Should Know Each Other
         */
        addUrlController.setController(controller);
        controller.setStage(addUrlScene);

        /*
         * Set The Properties Of MainStage
         */
        primaryStage.setTitle("Internet Download Manager (IDM)");
        primaryStage.getIcons().add(new Image(new FileInputStream("assets/Download.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        /*
         * Method Call Using Lambda When Stage Is Closed
         */
        primaryStage.setOnHiding (event -> controller.stageClosed ());
    }

    private Scene defineMainStage() throws IOException
    {
        /*
         * Load The FXML File For Main Scene
         */
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("controllersAndFxml/sample.fxml"));
        Parent parent = loader.load();

        /*
         * Set Controller
         */
        controller = loader.getController();

        return new Scene(parent);
    }

    private Scene defineAddUrlStage() throws IOException
    {
        /*
         * Load The FXML File For Add Url Scene
         */
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("controllersAndFxml/AddUrl.fxml"));
        Parent parent = loader.load();

        /*
         * Set Controller
         */
        addUrlController = loader.getController();

        return new Scene(parent);
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
