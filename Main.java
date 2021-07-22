package com.internshala.Connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.util.Optional;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane root = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane pane = (Pane) root.getChildren().get(0);
        pane.getChildren().add(menuBar);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //Creating Menu Bar
    private MenuBar createMenu() {

        //File Menu
        Menu File = new Menu("File");
        //Menu Items
        MenuItem newGame = new MenuItem("New Game");
        MenuItem resetGame = new MenuItem("Reset Game");
        MenuItem exitGame = new MenuItem("Exit Game");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        //Adding menu items to File Menu
        File.getItems().addAll(newGame, resetGame, separator, exitGame);

        //Help Menu
        Menu Help = new Menu("Help");
        //Menu Items
        MenuItem aboutGame = new MenuItem("About Connect Four");
        MenuItem aboutDev = new MenuItem("About Developer");
        //Adding menu Items to Help Menu
        Help.getItems().addAll(aboutGame, aboutDev);

        //Adding menu List  to menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(File, Help);

        //Event handler for reset Game, New Game,About Game, Exit Game, About Developer

        newGame.setOnAction(event -> {
            controller.resetGame();
        });
        resetGame.setOnAction(event -> {
            controller.resetGame();
        });
        exitGame.setOnAction(event -> {
            exitGame();
        });
        aboutGame.setOnAction(event -> {
            aboutGame();
        });
        aboutDev.setOnAction(event -> {
            aboutDev();
        });
        return menuBar;
    }

    private void exitGame() {

        Alert exit = new Alert(Alert.AlertType.CONFIRMATION);
        exit.setContentText("Do you want to exit?");
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        exit.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> option = exit.showAndWait();

        if (option.isPresent() && option.get() == yes){
            Platform.exit();
            System.exit(0);
        }
    }

    private void aboutDev() {
        Alert aboutMe = new Alert(Alert.AlertType.INFORMATION);
        aboutMe.setTitle("About the Developer");
        aboutMe.setHeaderText("Jason Hrangkhol");
        aboutMe.setContentText("I am a CSE UG at NIT SILCHAR.\n"+
                                "This is a Connect Four Game "+
                                    "which I have created.\n");
        aboutMe.show();
    }

    private void aboutGame() {

        Alert aboutGame = new Alert(Alert.AlertType.INFORMATION);
        aboutGame.setTitle("ABout Connect Four");
        aboutGame.setHeaderText("How to Play?");
        aboutGame.setContentText("Connect Four Game Description:\n" +
                "Connect Four is a two-player connection game in which " +
                "the players first choose a color and then take turns dropping colored " +
                "discs from the top into a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.\n");
        aboutGame.show();
    }
}
