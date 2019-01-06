package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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

public class Controller implements Initializable {

    private static final int Column=7;
    private static final int Row=6;
   private static final int Circ_dia=80;
   private static final String dcolor1="#24303E";
 private static final String dcolor2="#4CAA88";

   private String Player_one;

 private static String Player_two;

  private boolean isplayeroneturn=true;
  private Disc[][] inserteddiscsArray=new Disc[Row][Column]; //for structural changes
    @FXML
    public TextField playerOneTextField;
    @FXML
    public TextField playerTwoField;
    @FXML
    public Button NamesButton;

   @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDisc;

    @FXML
    public Label playerName;



    private boolean isAllowedTO=true;
     public void createPlayGrount(){
          Shape rectanglewithHoles=createGameStructuralGrid();
         rootGridPane.add(rectanglewithHoles,0,1);
         List<Rectangle> rectangleList=createClikableColumn();
         for (Rectangle rectangle: rectangleList) {

             rootGridPane.add(rectangle,0,1);
         }
         NamesButton.setOnAction(evevt->{
           playerName.setText(playerOneTextField.getText());

         });
      }
      private Shape createGameStructuralGrid(){
          Shape rectanglewithHoles= new Rectangle((Column+1)*Circ_dia,(Row+1)*Circ_dia);
          for (int r=0;r<Row;r++) {
              for (int c = 0; c < Column; c++) {
                  Circle circle = new Circle();
                  circle.setRadius(Circ_dia / 2);
                  circle.setCenterX(Circ_dia / 2);
                  circle.setCenterY(Circ_dia / 2);
                  circle.setSmooth(true);
                  circle.setTranslateX(c * (Circ_dia + 5) + Circ_dia / 4);
                  circle.setTranslateY(r * (Circ_dia + 5) + Circ_dia / 4);
                  rectanglewithHoles = Shape.subtract(rectanglewithHoles, circle);
              }
          }

     rectanglewithHoles.setFill(Color.WHITE);
          return rectanglewithHoles;
     }
     public List<Rectangle> createClikableColumn(){

         List<Rectangle> rectangleList=new ArrayList<>();
         for (int c=0;c<Column;c++) {
             Rectangle rectangle = new Rectangle(Circ_dia, (Row + 1) * Circ_dia);
             rectangle.setFill(Color.TRANSPARENT);
             rectangle.setTranslateX(c * (Circ_dia + 5) + Circ_dia / 4);
             rectangle.setOnMouseEntered(event ->rectangle.setFill(Color.valueOf("#eeeeee26")) );
             rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
             final int column=c;
             rectangle.setOnMouseClicked(event -> {
                 if(isAllowedTO){
                 isAllowedTO=false;
                     insertDisc(new Disc(isplayeroneturn), column);
                 }
             });
             rectangleList.add(rectangle);
         }
         return rectangleList;
     }
     private void insertDisc(Disc disc,int column)
     {
         int row=Row-1;
         while (row >= 0) {
             if (getDiscIfPresent(row,column)==null)
                 break;
             row--;
         }
         if (row<0)
             return;

         inserteddiscsArray[row][column]=disc;
         insertedDisc.getChildren().add(disc);
         disc.setTranslateX(column * (Circ_dia + 5) + Circ_dia / 4);
         int currentRow=row;
         TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.3),disc);
         translateTransition.setToY(row * (Circ_dia + 5) + Circ_dia / 4);
         translateTransition.setOnFinished(event -> {
             isAllowedTO=true;
             if(gameEnded(currentRow,column)) {
             gameOver();

             return;
             }

             isplayeroneturn=!isplayeroneturn;
             playerName.setText(isplayeroneturn?playerOneTextField.getText():playerTwoField.getText());
         });
         translateTransition.play();
     }
     private boolean gameEnded(int row,int column){
         List<Point2D> verticalPoints= IntStream.rangeClosed(row-3,row+3)
                 .mapToObj(r->new Point2D(r,column))
                 .collect(Collectors.toList());

         List<Point2D> horizontalPoints= IntStream.rangeClosed(column-3,column+3)
                 .mapToObj(c->new Point2D(row,c))
                 .collect(Collectors.toList());

         Point2D startPoint1=new Point2D(row-3,column+3);
         List<Point2D> diagonal1Points=IntStream.rangeClosed(0,6)
                 .mapToObj(i->startPoint1.add(i,-i))
                 .collect(Collectors.toList());

         Point2D startPoint2=new Point2D(row-3,column-3);
         List<Point2D> diagonal2Points=IntStream.rangeClosed(0,6)
                 .mapToObj(i->startPoint2.add(i,i))
                 .collect(Collectors.toList());


         boolean isEnded=checkCombinations(verticalPoints)|| checkCombinations(horizontalPoints)
                 ||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);

         return isEnded;
     }

    private boolean checkCombinations(List<Point2D> points) {
        int chain=0;
        for (Point2D point:points) {
     int rowIndexArray= (int) point.getX();
            int columnIndexArray= (int) point.getY();
            Disc disc=getDiscIfPresent(rowIndexArray,columnIndexArray);
            if(disc!=null && disc.isPlayeroneMove==isplayeroneturn) {
                chain++;
                    if(chain==4)
                       return true;
            }else
                chain=0;
        }
        return false;
    }
    private Disc getDiscIfPresent(int row,int column){
         if (row>=Row||row<0||column>=Column||column<0)
             return null;
         return inserteddiscsArray[row][column];

    }

    private void gameOver(){
  String winner=isplayeroneturn?playerOneTextField.getText():playerTwoField.getText();
        System.out.println("Winner is: " + winner);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        //alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle("Connect4");
        alert.setHeaderText("Winner is " +winner);
        alert.setContentText("Want to Play again?");
        ButtonType yesbutton=new ButtonType("Yes");
        ButtonType nobutton=new ButtonType("No");
        alert.getButtonTypes().setAll(yesbutton,nobutton);

        Platform.runLater(()->{
            Optional< ButtonType> whichbutton=alert.showAndWait();
            if(whichbutton.isPresent() && whichbutton.get()==yesbutton){
                resetGame();

            }
            else{
                Platform.exit();
                System.exit(0);

            }
        });

     }

    public void resetGame() {

         insertedDisc.getChildren().clear();
         for (int row=0;row<=inserteddiscsArray.length;row++){
             for (int c=0;c<=inserteddiscsArray[row].length;c++){
                 inserteddiscsArray[row][c]=null;
             }
         }
         isplayeroneturn=true;
         playerName.setText(playerOneTextField.getText());
         createPlayGrount();

    }

    private static class Disc extends Circle{
         private final boolean isPlayeroneMove;
         public Disc(boolean isPlayeroneMove){
             this.isPlayeroneMove=isPlayeroneMove;
             setRadius(Circ_dia / 2);
              setFill(isPlayeroneMove?Color.valueOf(dcolor1):Color.valueOf(dcolor2));
             setCenterX(Circ_dia / 2);
             setCenterY(Circ_dia / 2);
         }
}
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
