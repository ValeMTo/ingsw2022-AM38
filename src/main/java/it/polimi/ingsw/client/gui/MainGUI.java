package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.view.ViewState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  Each scene has its own controller
 */


public class MainGUI extends Application {

    public static final String HOME_MENU = "homeMenu.fxml";
    public static final String LOGIN_MENU = "loginMenu.fxml";

    //private final ViewState viewState;
    private Stage primaryStage;
    private Scene currentScene;

    private final Logger logger = Logger.getLogger(getClass().getName());


    /**
     * MainGUI  default constructor
     */
    public MainGUI() {
        // this.viewState = new ViewState()
    }





    @Override
    public void start(Stage stage) {
        setupStage();
        this.primaryStage = stage;
        runStage();

    }




    @Override
    public void stop() {
        System.exit(0);
    }


    public static void main(String[] args) {
        launch();
    }


    public void runStage() {
        primaryStage.setTitle("Eriantys");
        primaryStage.setScene(currentScene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/logos/Eriantys_logo-180x180.png")));
        primaryStage.show();
    }

    public void setupStage() {
        String fxmlName = HOME_MENU;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_fxml/" + fxmlName));
            currentScene = new Scene(loader.load());

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

    }




}
