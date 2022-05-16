

package it.polimi.ingsw.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class HelloApplication extends Application {



    @Override
    public void start(Stage primaryStage) throws IOException {

        Group root = new Group();
        Canvas canvas = new Canvas(1920, 1080);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ImageView imageView;

        Image img = new Image("Assistente_1.png");
        imageView = new ImageView(img);

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));

        primaryStage.setTitle("Hello! I'm the primaryStage ");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
