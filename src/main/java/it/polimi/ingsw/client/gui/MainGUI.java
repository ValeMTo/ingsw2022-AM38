package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.client.gui.controllers.LobbyMenuController;
import it.polimi.ingsw.client.gui.controllers.MyBoardGuiController;
import it.polimi.ingsw.client.view.ViewState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Each scene has its own controller.
 * The currentScene is changed after
 */


public class MainGUI extends Application {

    public static final String HOME_SCENE = "homeMenu.fxml";
    public static final String LOGIN_SCENE = "loginMenu.fxml";
    public static final String SETUP_SCENE = "setupMenu.fxml";
    public static final String ACCEPT_CONDITIONS_SCENE = "acceptConditionsMenu.fxml";
    public static final String LOBBY_SCENE = "lobbyScene.fxml";
    public static final String CREDITS_SCENE = "creditsScene.fxml";
    public static final String MY_BOARD_SCENE = "myBoardScene.fxml";

    private final ViewState viewState;
    private final HashMap<String, Scene> guiScenesMap = new HashMap<>();
    private final HashMap<String, GUIController> guiControllersMap = new HashMap<>();
    private final Logger logger = Logger.getLogger(getClass().getName());
    private Stage primaryStage;
    private Stage secondStage;
    private Scene runningScene;
    private ConnectionSocket connectionSocket = null;
    private final boolean isRunning;


    /**
     * MainGUI  default constructor
     */
    public MainGUI() {
        this.isRunning = true;
        this.viewState = new ViewState(false);
    }

    public static void main(String[] args) {
        launch();
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
     */
    public void loadScenes() {

        List<String> fxmlScenes = new ArrayList<String>();

        fxmlScenes.add(HOME_SCENE);
        fxmlScenes.add(LOGIN_SCENE);
        fxmlScenes.add(SETUP_SCENE);
        fxmlScenes.add(ACCEPT_CONDITIONS_SCENE);
        fxmlScenes.add(LOBBY_SCENE);
        fxmlScenes.add(CREDITS_SCENE);
        fxmlScenes.add(MY_BOARD_SCENE);


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
     * Changes the stage launching the new desired scene.
     *
     * @param newScene : the new scene that has to be displayed
     */
    public void setNextStage(String newScene) {
        runningScene = guiScenesMap.get(newScene);
        primaryStage.setScene(runningScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Updates the Lobby scene by telling the LobbyMenuController to updates its online players list and the
     * game settings of that lobby.
     * To get the new online players list, the LobbyMenuController uses getters from the ViewState.
     *  If the newly added player is the player who owns this ViewMessageParser, it simply welcomes the player
     *  in the lobby menu scene.  Otherwise, it tells the lobbyMenu controller to refresh and updates
     *  the list of current online players in the lobby
     */
    public void updateLobbyScene(String newNickname) {
        Platform.runLater(() -> {
            LobbyMenuController controller = (LobbyMenuController) getController("lobbyScene.fxml");
            controller.addNicknameInLobby(newNickname);
            controller.setLobbySettings();
        });
        System.out.println("I'm updating the lobby scene with the new player: " + newNickname);
        setNextStage("lobbyScene.fxml");
    }

    /**
     * Initializes the first setup of the board at the beginning of the game.  This operation is triggered as soon as
     * the "Setup Update message"  is received and parsed by the ViewMessageParser.
     *
     */
    public void initBoard() {
            MyBoardGuiController controller = (MyBoardGuiController) getController("myBoardScene.fxml");
            controller.setupBoard();

    }

    public void updatePlayerBoard() {
        MyBoardGuiController controller = (MyBoardGuiController) getController("myBoardScene.fxml");
        controller.updateWholeBoard();
    }



    public ViewState getViewState() {
        return viewState;
    }

    public void setConnectionSocket(ConnectionSocket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public ConnectionSocket getConnectionSocket(){
        return connectionSocket;
    }

    public GUIController getController(String fxmlName){
        return guiControllersMap.get(fxmlName);
    }

    public void loadSecondWindow(String fxmlName) {
        secondStage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader (getClass().getResource("/gui_fxml/" + fxmlName));
            root = loader.load();

            secondStage.setTitle("Eriantys");
            secondStage.setScene(new Scene(root));
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/logos/Eriantys_logo-180x180.png")));

            GUIController controller = loader.getController();
            controller.setGuiToController(this);
            controller.loadContent();
            guiControllersMap.put(fxmlName, controller);

            secondStage.setResizable(false);
            secondStage.show();

        } catch (IOException e) {e.printStackTrace();}
    }



}
