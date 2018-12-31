
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;


public class GUI extends Application {

    File sourceCodeFile;
    BorderPane root;
    TextArea comment;
    TextArea code;
    TextField location;
    CommentForCat currentSeclectItem;
    ChoiceBox<CommentCategory> category;
    static List<CommentForCat> commentsFromFile;


    int selectedIndex = -1;

    @Override
    public void start(Stage primaryStage) throws Exception {

        root = new BorderPane();
        HBox hBox = addHBox();
        root.setTop(hBox);



        Scene scene = new Scene(root,1200,800);
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
        Button showCommentList = new Button("show Comments in file");
        showCommentList.setOnAction(e -> showCommentList());
        hbox.getChildren().addAll(openfile,showCommentList);
        return hbox;
    }

    private VBox addVBoxwithCommentsList(){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(15, 12, 15, 12));

        try {
            commentsFromFile = SourceFileUtil.getCommentsFromFile(sourceCodeFile);
            ObservableList<CommentForCat> comments = FXCollections.observableArrayList();
            for (CommentForCat each : commentsFromFile){
                comments.add(each);
            }
            ListView<CommentForCat> commentsListView = new ListView<>(comments);

            commentsListView.setCellFactory(e -> new ListCell<CommentForCat>(){
                @Override
                protected void updateItem(CommentForCat item, boolean empty){
                    super.updateItem(item,empty);

                    if(empty || item == null ){
                        setText(null);
                    }else {
                        setText(item.toString());
                    }
                }
            });

            commentsListView.setOnMouseClicked(new ListViewHandle(){
                @Override
                public void handle(MouseEvent event) {
                    super.handle(event);
                    currentSeclectItem = commentsListView.getSelectionModel().getSelectedItem();
                    selectedIndex = commentsListView.getSelectionModel().getSelectedIndex();
                    comment.setText(null);
                    comment.appendText(currentSeclectItem.text);
                    code.setText(null);
                    code.appendText(currentSeclectItem.commentedCode);
                    location.setText(null);
                    location.appendText(currentSeclectItem.commentLocation.toString());
                    category.setValue(currentSeclectItem.commentCategory);


                }
            });




            commentsListView.setOrientation(Orientation.VERTICAL);
            commentsListView.setPrefSize(400,650);
            vBox.getChildren().add(commentsListView);



        } catch (Exception e) {
            e.printStackTrace();
        }
        return  vBox;
    }



    private VBox addCommentDetailVBox(){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(15, 12, 15, 12));

        Label commentText = new Label("Comment Text");
        Label commentedCode = new Label("Commented Code");

        location = new TextField();
        comment = new TextArea();
        comment.setPrefSize(550,200);
        comment.setEditable(false);
        code = new TextArea();
        code.setPrefSize(550,500);
        code.setEditable(false);



        vBox.getChildren().addAll(location,commentText,comment,commentedCode,code);

        return vBox;
    }

    private VBox addChoiceBoxforCategory(){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(15, 12, 15, 12));

        Label label = new Label("please set category for\n the selected comment");


        CommentCategory[] categories = CommentCategory.class.getEnumConstants();

        category = new ChoiceBox<>(FXCollections.observableArrayList(categories));


       /* category.setOnMouseClicked(new ListViewHandle(){
            @Override
            public void handle(MouseEvent event) {
                super.handle(event);
                CommentCategory selected =category.getSelectionModel().getSelectedItem();
                currentSeclectItem.setCommentCategory(selected);
                System.out.println("selected category: "+category.toString() );


            }

        });*/



        Button saveCategory = new Button("save Current Comment");
        saveCategory.setOnAction( e -> saveCategory(category));

        Button saveToFile = new Button("save all comments to file");
        Tooltip tt = new Tooltip("we are supposed manually classify\n all comments before save!");
        saveToFile.setTooltip(tt);
        tt.setStyle( "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;");

        saveToFile.setOnAction(e -> {
            try {
                FileWriter fw = new FileWriter(sourceCodeFile.getPath()+".json");
                for (CommentForCat each : commentsFromFile){
                    fw.write(each.saveToJson());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

        vBox.getChildren().addAll(label,category,saveCategory,saveToFile);
        return vBox;
    }

    private void saveCategory(ChoiceBox<CommentCategory> choiceBox){
        CommentCategory category = choiceBox.getValue();
        currentSeclectItem.setCommentCategory(category);
        System.out.println("selected category: "+category.toString() );

    }

    private void showCommentList(){
        VBox commentList = addVBoxwithCommentsList();
        root.setLeft(commentList);

        VBox commentDetail = addCommentDetailVBox();
        root.setCenter(commentDetail);

        VBox categoryChoiceBox = addChoiceBoxforCategory();
        root.setRight(categoryChoiceBox);
    }

    private File openfile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("java source code","*.java"));
        return fileChooser.showOpenDialog(new Stage());

    }



    private class ListViewHandle implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {

        }
    }

    public static void main(String[] args){
        launch(args);


        for (CommentForCat each : commentsFromFile){
            System.out.println(each+"\n"+each.commentCategory);
        }
        System.out.println("-----------");
    }

}
