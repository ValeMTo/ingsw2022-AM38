

package it.polimi.ingsw.client.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {



    @Override
    public void start(Stage primaryStage) throws IOException {

        Group root = new Group();
        Canvas canvas = new Canvas(1024, 768);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ImageView imageView;

        Image img = new Image("graphics/assistantCards/Assistente_1.png");
        imageView = new ImageView(img);
        gc.drawImage(img,20,20,100,146);

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));

        primaryStage.setTitle("Hello! I'm the primaryStage ");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/logos/Eriantys_logo-180x180.png")));
        primaryStage.show();


    }

    @Override
    public void stop() {
        System.exit(0);
    }




    public static void main(String[] args) {
        launch();
    }

}
