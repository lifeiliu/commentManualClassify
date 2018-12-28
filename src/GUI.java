
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GUI extends Application {

    File sourceCodeFile;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        HBox hBox = addHBox();
        root.setTop(hBox);



        Scene scene = new Scene(root,1000,800);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private HBox addHBox(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        Button openfile = new Button("open java file");
        openfile.setOnAction(e->
            sourceCodeFile = openfile()
        );
        hbox.getChildren().add(openfile);
        return hbox;
    }

    private File openfile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("java source code","*.java"));
        return fileChooser.showOpenDialog(new Stage());

    }
}
