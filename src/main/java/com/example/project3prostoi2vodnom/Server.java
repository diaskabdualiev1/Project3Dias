package com.example.project3prostoi2vodnom;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Server extends Application {
    public static final int PORT = 8081;
    public static LinkedList<ServerClientClass> serverList = new LinkedList<>();
    private int pinCode;
    static VBox vBox;
    Button startGame;
    static int seconds;
    static int timeObrat;
    static HBox hBox;

    static int quizCount = 0;
    int a = 1;

    static Button nextQuiz;

    static Quiz quiz;

    static Button button2a;

    static ToggleButton button1;
    static ToggleButton button2;
    static ToggleButton button3;
    static ToggleButton button4;
    static ArrayList<ToggleButton> toggleButtons;
    static Scene sceneTest;
    static Label vopros;
    static Text vremya;
    static GridPane gridPane;
    static BorderPane borderPane;

    static File quizFile;

    static Timeline timeline;

    @Override
    public void start(Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        Button button = new Button("Selected file");
        button.setStyle("-fx-text-fill: white;-fx-background-color: #44394b;-fx-font-size: 18;-fx-font-family: Ubuntu");
        StackPane stackPane1 = new StackPane(button);
        stackPane1.setStyle("-fx-background-color: #670092");
        stage.setScene(new Scene(stackPane1,500,500));
        stage.show();

        Random random = new Random();

        Text text = new Text("Kahoot!");
        text.setStyle("-fx-font-size: 20");
        vBox = new VBox(10,text);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(new Text("Pin Code Game: " + pinCode));

        startGame = new Button("Start Game!");
        vBox.getChildren().add(startGame);

        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #670092");
        pinCode = random.nextInt(999999);
        Text text2 = new Text("Users\n"+"pincode "+pinCode);
        BorderPane.setAlignment(text2,Pos.TOP_CENTER);
        text2.setStyle("-fx-font-size: 24;-fx-text-fill: white");
        borderPane.setTop(text2);

        startGame.setStyle("-fx-text-fill: white;-fx-background-color: #44394b;-fx-font-size: 18;-fx-font-family: Ubuntu");
        borderPane.setRight(startGame);

        hBox = new HBox(30);
        hBox.setAlignment(Pos.TOP_CENTER);
        BorderPane.setAlignment(hBox,Pos.TOP_CENTER);
        borderPane.setCenter(hBox);

        Scene scene = new Scene(borderPane,900,600);
        System.out.println("1");
        ServerSocket server = new ServerSocket(PORT);
        button.setOnAction(event -> {
            quizFile = fileChooser.showOpenDialog(stage);
            stage.setScene(scene);
        });

        new Thread(()-> {
            try {
                while (true) {
                    Socket socket = server.accept();
                    System.out.println("kirdi");
                    try {
                        serverList.add(new ServerClientClass(socket, pinCode));
                    } catch (IOException e) {
                        socket.close();
                    }
                }
            }
            catch (Exception e) {}
            finally {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("2");

        startGame.setOnAction(event -> {
            quiz = Quiz.loadFromFile(quizFile.getPath());
            for (ServerClientClass client : Server.serverList) {
                try {
                    client.startGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            gameStartQuiz(stage);
        });
        nextQuiz = new Button("Next");
        nextQuiz.setStyle("-fx-background-color: #eec6ff");
        nextQuiz.setOnAction(event -> {
            if(quizCount != quiz.questions.size()) {
                if(a == 0) {
                    for (ServerClientClass client : Server.serverList) {
                        try {
                            client.startGame();
                            client.otvet = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    gameStartQuiz(stage);
                    a = 1;
                }
                else if(a == 1){
                    stage.setScene(ballShow());
                    a = 0;
                }
//                Text waitText = new Text();
//                waitText.setStyle("-fx-font-size: 18");
//                StackPane waitPane = new StackPane(waitText);
//                Scene scene1 = new Scene(waitPane, 500, 500);
//                stage.setScene(scene1);
//                seconds = 0;
//                System.out.println("startQuiz1");
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event1 -> {
//                    seconds++;
//                    System.out.println("Seconds " + seconds);
//                    waitText.setText(seconds + "");
//                    if (seconds == 5) {
//                        stage.setScene(quizBorderPane(quiz.questions.get(quizCount)));
//                        quizCount++;
//                        System.out.println("Болды");
//                    }
//                }));
//                timeline.setCycleCount(5);
//                timeline.play();
            }
            else{
                VBox vBox1 = new VBox(150);
                vBox1.setAlignment(Pos.TOP_CENTER);
                HBox hBox = new HBox(100);
                hBox.setAlignment(Pos.TOP_CENTER);
                Label text1 = new Label("Top 3");
                text1.setStyle("-fx-font-size: 24");
                vBox1.getChildren().add(new Text("Top 3"));
                int count = 0;
                for (ServerClientClass user :
                        serverList) {
                    Rectangle rectangle = new Rectangle(80,user.getBall()/10, Color.GREEN);
                    StackPane stackPane = new StackPane(rectangle, new Text(user.nameUser+"\n"+user.nameUser));
                    hBox.getChildren().add(stackPane);
                }
                vBox1.getChildren().add(hBox);
                vBox1.setStyle("-fx-background-color: #eec6ff");
                Scene scene1 = new Scene(vBox1,900,600);
                stage.setScene(scene1);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

    public static void addUser(UserUI userUI){
        Rectangle rectangle = new Rectangle(100,50,Color.GREY);
        Text text1 = new Text("Dias");
        text1.setStyle("-fx-font-size: 16");
        StackPane stackPane1 = new StackPane(rectangle, text1);
        hBox.getChildren().add(stackPane1);
    }

    public static void gameStartQuiz(Stage stage){// самый басында брлет
        stage.setScene(quizBorderPane(quiz.questions.get(quizCount)));
        quizCount++;
    }

    public static Scene quizBorderPane(Question question){
        BorderPane borderPane = new BorderPane();
        vopros = new Label("Zdes budet vopros");
        vopros.setStyle("-fx-font-size: 18");
        vopros.setWrapText(true);
        BorderPane.setAlignment(vopros, Pos.TOP_CENTER);
        borderPane.setTop(vopros);

        Image image = new Image(new File("src/main/resources/logo.gif").toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitHeightProperty().bind(borderPane.heightProperty().divide(1.5));
        VBox sBox = new VBox(8,imageView);
        sBox.setAlignment(Pos.TOP_CENTER);
        borderPane.setCenter(sBox);

        BorderPane.setAlignment(nextQuiz,Pos.TOP_CENTER);
        borderPane.setRight(nextQuiz);
        borderPane.setStyle("-fx-background-color: #7affda");

        Text timeText = new Text(timeObrat+"");

        button1 = new ToggleButton();
        button1.setStyle("-fx-background-color: red;-fx-font-size: 15;-fx-font-family: 'Eras Demi ITC';-fx-font-weight: bold");
        button2 = new ToggleButton();
        button2.setStyle("-fx-background-color: blue;-fx-font-size: 15;-fx-font-family: 'Eras Demi ITC';-fx-font-weight: bold");
        button3 = new ToggleButton();
        button3.setStyle("-fx-background-color: orange;-fx-text-fill: #ffffff;-fx-font-size: 15;-fx-font-family: 'Eras Demi ITC';-fx-font-weight: bold");
        button4 = new ToggleButton();
        button4.setStyle("-fx-background-color: green;-fx-font-size: 15;-fx-font-family: 'Eras Demi ITC';-fx-font-weight: bold");
        toggleButtons = new ArrayList<ToggleButton>();
        toggleButtons.add(button1);
        toggleButtons.add(button2);
        toggleButtons.add(button3);
        toggleButtons.add(button4);
        gridPane = new GridPane();
        ToggleGroup toggleGroup = new ToggleGroup();
        for (ToggleButton tg : toggleButtons) {
            tg.setToggleGroup(toggleGroup);
            tg.prefWidthProperty().bind(gridPane.widthProperty().divide(2));
            tg.prefHeightProperty().bind(gridPane.widthProperty().divide(15));
            tg.setWrapText(true);
        }
        gridPane.add(button1,0,0);
        gridPane.add(button2,1,0);
        gridPane.add(button3,0,1);
        gridPane.add(button4,1,1);
        gridPane.setVgap(4);
        gridPane.setHgap(4);
        gridPane.setPadding(new Insets(5));
        if(question instanceof Test test) {
            vopros.setText(test.getDescription());
            for (int i = 0; i < 4; i++) {
                toggleButtons.get(i).setText(test.getOptionAt(i));
            }
        }else if (question instanceof Fillin fillin){
            vopros.setText(fillin.getDescription());
            for (ToggleButton tg : toggleButtons) {
                tg.setVisible(false);
            }
        }
        borderPane.setBottom(gridPane);

        timeObrat = 30000;
        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            timeObrat-= 100;
            int result = (int)Math.floor(timeObrat/1000);
            timeText.setText(String.valueOf(result));
            if(timeObrat == 0){
                if(question instanceof Test) {
                    for (ToggleButton tg : toggleButtons) {
                        if (tg.getText().equals(question.getAnswer())) {
                            tg.setVisible(true);
                            tg.setStyle("-fx-base: green");
                        } else {
                            tg.setVisible(false);
                        }
                    }
                    timeline.stop();
                }
                else{
                    Label label = new Label();
                    label.setStyle("-fx-font-size: 28");
                    borderPane.setBottom(new StackPane(label));
                    label.setText("Answer " +question.getAnswer());
                    timeline.stop();
                }
            }
            else if(vseOtvetily()){
                if(question instanceof Test) {
                    for (ToggleButton tg : toggleButtons) {
                        if (tg.getText().equals(question.getAnswer())) {
                            tg.setVisible(true);
                            tg.setStyle("-fx-base: green");
                        } else {
                            tg.setVisible(false);
                        }
                    }
                    timeline.stop();
                }
                else{
                    Label label = new Label();
                    label.setStyle("-fx-font-size: 28");
                    borderPane.setBottom(new StackPane(label));
                    label.setText("Answer " +question.getAnswer());
                    timeline.stop();
                }
            }
        }));
        borderPane.setLeft(timeText);
        BorderPane.setAlignment(timeText, Pos.TOP_CENTER);
        timeText.setStyle("-fx-font-size: 20");
        timeline.setCycleCount(timeObrat/100);
        timeline.play();
        sceneTest = new Scene(borderPane,900,600);
        return sceneTest;
    }

    private static boolean vseOtvetily() {
        boolean otvet = true;
        for (ServerClientClass client : Server.serverList) {
            if(!client.otvet) otvet = false;
        }
        return otvet;
    }

    public static Scene ballShow(){
        VBox vBox = new VBox(30);
        vBox.setStyle("-fx-background-color: #7affda");
        Insets insets = new Insets(5,80,5,100);
        nextQuiz.setAlignment(Pos.BASELINE_RIGHT);
        vBox.getChildren().add(nextQuiz);
        for (ServerClientClass client :
                Server.serverList) {
            Rectangle rectangle = new Rectangle(client.getBall()/4,50, Color.VIOLET);
            StackPane stackPane = new StackPane(rectangle);
            VBox.setMargin(rectangle, insets);
            VBox.setMargin(rectangle, insets);
            Text text = new Text(client.nameUser+"\n"+client.getBall());
            vBox.getChildren().addAll(rectangle, text);
        }
        return new Scene(vBox,900,600);
    }
}

