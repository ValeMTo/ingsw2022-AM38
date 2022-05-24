package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.client.view.ViewState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  Each scene has its own controller.
 *  The currentScene is changed
 */


public class MainGUI extends Application {

    public static final String HOME_SCENE = "homeMenu.fxml";
    public static final String LOGIN_SCENE = "loginMenu.fxml";

    //private final ViewState viewState;
    private Stage primaryStage;
    private Scene currentScene;

    private final HashMap<String, Scene> guiScenesMap = new HashMap<>();
    private final HashMap<String, GUIController> guiControllersMap = new HashMap<>();

    private final Logger logger = Logger.getLogger(getClass().getName());


    /**
     * MainGUI  default constructor
     */
    public MainGUI() {
        // this.viewState = new ViewState()
    }





    @Override
    public void start(Stage primaryStage) {
        loadScenes();
        this.primaryStage = primaryStage;
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
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    /**
     * Fills the two maps (for the scenes and for their controllers, linking them to the fxml files), then  loads the HOME_MENU scene
     *
     */
    public void loadScenes() {

        List<String> fxmlScenes = new ArrayList<>(Arrays.asList(HOME_SCENE, LOGIN_SCENE));
        try {
            for (String fxmlName : fxmlScenes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_fxml/" + fxmlName));
                Scene loadedScene = new Scene(loader.load());
                guiScenesMap.put(fxmlName, loadedScene);
                GUIController guiController = loader.getController();
                guiController.initGUI(this);
                guiControllersMap.put(fxmlName, guiController);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }



        String fxmlHomeMenu = HOME_SCENE;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_fxml/" + fxmlHomeMenu));
            currentScene = new Scene(loader.load());
            GUIController guiController = loader.getController();
            guiController.initGUI(this);

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }



    }


    /**
     *  Changes the stage launching the new desired scene.
     * @param newScene : the new scene that has to be displayed
     *
     */
    public void changeStage(String newScene) {
        currentScene = guiScenesMap.get(newScene);
        primaryStage.setScene(currentScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }



}
