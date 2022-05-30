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
 *  The currentScene is changed after
 */


public class MainGUI extends Application {

    public static final String HOME_SCENE = "homeMenu.fxml";
    public static final String LOGIN_SCENE = "loginMenu.fxml";

    private final ViewState viewState;
    private Stage primaryStage;
    private Scene runningScene;

    private final HashMap<String, Scene> guiScenesMap = new HashMap<>();
    private final HashMap<String, GUIController> guiControllersMap = new HashMap<>();

    private final Logger logger = Logger.getLogger(getClass().getName());
    private boolean isRunning;

    /**
     * MainGUI  default constructor
     */
    public MainGUI() {
        this.isRunning = true;
        this.viewState = new ViewState();
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
        primaryStage.setScene(runningScene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/logos/Eriantys_logo-180x180.png")));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    /**
     * Fills the two maps (for the scenes and for their controllers, linking them to the fxml files), then  loads the
     * HOME_MENU scene, which is the first scene to be displayed.
     *
     */
    public void loadScenes() {

        List<String> fxmlScenes = new ArrayList<String>();

        fxmlScenes.add(HOME_SCENE);
        fxmlScenes.add(LOGIN_SCENE);

        try {
            for (String fxmlName : fxmlScenes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_fxml/" + fxmlName));
                Scene loadedScene = new Scene(loader.load());
                guiScenesMap.put(fxmlName, loadedScene);
                GUIController guiController = loader.getController();
                guiController.setGuiToController(this);
                guiControllersMap.put(fxmlName, guiController);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }



        String fxmlHomeMenu = HOME_SCENE;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_fxml/" + fxmlHomeMenu));
            runningScene = new Scene(loader.load());
            GUIController guiController = loader.getController();
            guiController.setGuiToController(this);

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }



    }


    /**
     *  Changes the stage launching the new desired scene.
     * @param newScene : the new scene that has to be displayed
     *
     */
    public void setNextStage(String newScene) {
        runningScene = guiScenesMap.get(newScene);
        primaryStage.setScene(runningScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public ViewState getViewState() {
        return viewState;
    }

}
