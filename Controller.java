package com.internshala.Connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.valueOf;

public class Controller implements Initializable {

    private static final int rows =6;
    private static final int columns = 7;
    private static final int diameter = 80;
    private static final String disc1 ="#24303E";
    private static final String disc2 ="4CAA88";

    private static String playerOne ="Player One";
    private static String playerTwo = "Player Two";

    private boolean isPlayerOneMove = true;
    private boolean isAllowedToInsert = true;

    private  Disc[][] insertedDisc = new Disc[rows][columns]; //For structural changes:For the developers

    @FXML
    public GridPane gridPane;
    @FXML
    public Pane disc;
    @FXML
    public Label nameLabel;
    @FXML
    public TextField playerOneTextField, playerTwoTextField;
    @FXML
    public Button setNamesButton;

    public void createPlayground(){

        Platform.runLater(() -> setNamesButton.requestFocus());

        Shape rectangle = createGameStructure();
        gridPane.add(rectangle, 0, 1);
        List<Rectangle> list = clickAbleColumns();

        for(Rectangle rectanglelist:list){

            gridPane.add(rectanglelist, 0, 1);
        }

        setNamesButton.setOnAction(event -> {
            playerOne = playerOneTextField.getText();
            playerTwo = playerTwoTextField.getText();
            nameLabel.setText(isPlayerOneMove? playerOne : playerTwo);
        });
    }

    private Shape createGameStructure(){
        Shape rectangle = new Rectangle((columns+1)*diameter, (rows+1)*diameter);

        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<columns;j++)
            {
                Circle circle = new Circle();
                circle.setRadius(diameter/2);
                circle.setCenterX(diameter/2);
                circle.setCenterY(diameter/2);
                circle.setSmooth(true);
                circle.setTranslateX(j*(diameter+5)+diameter/4);
                circle.setTranslateY(i*(diameter+5)+diameter/4);

                rectangle = Shape.subtract(rectangle, circle);
            }
        }
        rectangle.setFill(Color.WHITE);
        return  rectangle;
    }

    private List<Rectangle> clickAbleColumns(){

        List<Rectangle> list = new ArrayList<>();

        for(int i=0;i<columns;i++){

            Rectangle rectangle = new Rectangle(diameter, (rows+1)*diameter );
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(i*(diameter+5)+diameter/4);

            rectangle.setOnMouseEntered(event->{
                rectangle.setFill(Color.valueOf("#eeeeee26"));
            });
            rectangle.setOnMouseExited( event ->{
                rectangle.setFill(Color.TRANSPARENT);
            });

            final int col = i;
            rectangle.setOnMouseClicked( event ->{

                if(isAllowedToInsert){
                    isAllowedToInsert = false; // when disc is being dropped, no more disc will be inserted
                    insertDisc(new Disc(isPlayerOneMove), col);
                }

            });
            list.add(rectangle);
        }
        return list;
    }

    private void insertDisc(Disc newDisc, int col){

        int i= rows-1;
        while(i>=0)
        {
            if(insertedDisc[i][col]==null)
                break;
            i--;
        }
        if(i<0)
            return ;
        int currentRow = i, currentCol = col;
        insertedDisc[i][col] = newDisc;//For structural changes :For developers
        newDisc.setTranslateX(col*(diameter+5)+diameter/4);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), newDisc);
        translateTransition.setToY(i*(diameter+5)+diameter/4);
        translateTransition.setOnFinished(event->{

            isAllowedToInsert = true;
            if(gameEnded(currentRow,currentCol))
                gameOver();

            isPlayerOneMove =!isPlayerOneMove;
            nameLabel.setText(isPlayerOneMove?playerOne:playerTwo);
        });
        translateTransition.play();
        disc.getChildren().add(newDisc);//For visualiztion:For Players
    }

    private boolean gameEnded(int row, int col)
    {
        List<Point2D>verticalPoints = IntStream.rangeClosed(row-3,row+3)
                                        .mapToObj(r->new Point2D(r, col))
                                        .collect(Collectors.toList());

        List<Point2D>horizontalPoints = IntStream.rangeClosed(col-3,col+3)
                                        .mapToObj(c->new Point2D(row,c))
                                        .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row-3,col+3);
        List<Point2D>diagonal1Points = IntStream.rangeClosed(0,rows)
                                        .mapToObj(i->startPoint1.add(i,-i))
                                        .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row-3,col-3);
        List<Point2D>diagonal2Points = IntStream.rangeClosed(0,rows)
                                        .mapToObj(i->startPoint2.add(i,i))
                                        .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                            ||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);
        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {

        int cnt = 0;
        for(Point2D point:points)
        {
            int rowIndex = (int)point.getX();
            int colIndex = (int)point.getY();
            //To handle index out of bound exception
            try{

                Disc newDisc = insertedDisc[rowIndex][colIndex];

                if(newDisc!=null && newDisc.playerMove==isPlayerOneMove)
                {
                    cnt++;
                    if(cnt==4)
                        return true;
                }
                else
                    cnt=0;
            }
            catch(ArrayIndexOutOfBoundsException e){

            }
        }
        return false;
    }

    private void gameOver() {

        String winner = isPlayerOneMove?playerOne:playerTwo;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect 4");
        alert.setHeaderText("The winner is "+winner);
        alert.setContentText("Do you want to play again?");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("NO, exit");
        alert.getButtonTypes().setAll(yes,no);

        Platform.runLater(()->{  // Helps us to resolve IllegalStateException.

            Optional<ButtonType>option = alert.showAndWait();
            if(option.isPresent() && option.get()==yes){
                resetGame();
            }
            else
            {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {

        disc.getChildren().clear(); // Remove all Inserted Disc from Pane

        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<columns;j++)
                insertedDisc[i][j]=null; // Structurally, Make all elements of insertedDiscsArray[][] to null
        }

        isPlayerOneMove = true; //Let Player One start the game
        nameLabel.setText(playerOne);

        createPlayground(); // Prepare a fresh playground
    }

    private static class Disc extends Circle{

        private boolean playerMove;

        Disc(boolean isPlayerOneMove){
            this.playerMove = isPlayerOneMove;
            setFill(isPlayerOneMove?Color.valueOf(disc1):Color.valueOf(disc2));
            setRadius(diameter/2);
            setCenterX(diameter/2);
            setCenterY(diameter/2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
