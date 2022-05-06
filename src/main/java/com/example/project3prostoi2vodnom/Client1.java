package com.example.project3prostoi2vodnom;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;

public class Client1 extends Application {

    private final static int JOIN_GAME = 100;
    private final static int SUCCESS = 7;
    private final static int START_GAME = 101;
    private final static int WAIT_ANSWER = 200;
    private final static int DENY = 9;
    private final static int ANSWER = 67;
    private final static int FILLIN = 55;
    private final static int TEST = 66;

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    VBox vBox;
    FlowPane flowPane;

    int millis = 0;
    int questionCount = 0;

    Scene sceneFlow;
    Scene fillinScene;
    Scene testPane;

    @Override
    public void start(Stage stage) throws IOException {

        TextField name = new TextField();
        TextField code = new TextField();
        Button joinGameButton = new Button("Join");
        vBox = new VBox(20,name,code,joinGameButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #6200bc");
        stage.setScene(new Scene(vBox,600,600));
        stage.show();
        Button a = new Button("A");
        a.setPrefSize(300,300);
        Button b = new Button("B");
        b.setPrefSize(300,300);
        Button c = new Button("C");
        c.setPrefSize(300,300);
        Button d = new Button("D");
        d.setPrefSize(300,300);
        flowPane = new FlowPane(a,b,c,d);
        sceneFlow = new Scene(flowPane,600,600);

        TextField textField = new TextField();
        Button sendFillin = new Button("Отправить ответ!");
        VBox vBox = new VBox(100,textField,sendFillin);
        VBox.setMargin(textField,new Insets(50,50,50,50));
        vBox.setAlignment(Pos.TOP_CENTER);
        fillinScene = new Scene(vBox,600,600);

        try {
            try {
                // адрес - локальный хост, порт - 4004, такой же как у сервера
                clientSocket = new Socket("localhost", 8081); // этой строкой мы запрашиваем
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            }
            finally {
            }
        }
        catch (IOException e) {
            System.err.println(e);
        }

        new Thread(()->{
            try {
                while (true) {
                    System.out.println("While start");
                    int code1 = in.read();
                    System.out.println(code1);
                    if(code1 == START_GAME) startGame(stage);
                    else if(code1 == SUCCESS) successPin(stage);
                    else if(code1 == DENY) denyPin();
                    System.out.println("While End");
                }
            } catch (IOException e) {
            }
        }).start();
        joinGameButton.setStyle("-fx-text-fill: white;-fx-background-color: #44394b;-fx-font-size: 18;-fx-font-family: Ubuntu");
        joinGameButton.setOnAction(event -> {
            try {
                System.out.println("1");
                out.write(JOIN_GAME);
                out.write(name.getText()+"\n");
                out.write(code.getText()+"\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        a.setOnAction(event -> {
            waitNextQuestion(stage);
            try {
                out.write(ANSWER);
                out.flush();
                out.write(TEST);
                out.flush();
                out.write(1);
                out.flush();
                out.write(millis);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        b.setOnAction(event -> {
            waitNextQuestion(stage);
            try {
                out.write(ANSWER);
                out.flush();
                out.write(TEST);
                out.flush();
                out.write(2);
                out.flush();
                out.write(millis);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        c.setOnAction(event -> {
            waitNextQuestion(stage);
            try {
                out.write(ANSWER);
                out.flush();
                out.write(TEST);
                out.flush();
                out.write(3);
                out.flush();
                out.write(millis);
                 out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        d.setOnAction(event -> {
            waitNextQuestion(stage);
            try {
                out.write(ANSWER);
                out.flush();
                out.write(TEST);
                out.flush();
                out.write(4);
                out.flush();
                out.write(millis);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sendFillin.setOnAction(event -> {
            waitNextQuestion(stage);
            try {
                out.write(ANSWER);
                out.flush();
                out.write(FILLIN);
                out.flush();
                out.write(textField.getText());
                out.flush();
                out.write(millis);
                out.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public static void main(String[] args) {
        launch();
    }
    public void successPin(Stage stage){
        System.out.println("krasava");
        Platform.runLater(() -> {
            stage.setScene(new Scene(new StackPane(new Text("Waiting Start game")), 500, 500));
        });
        System.out.println("bitty");
    }
    public void denyPin(){
        System.out.println("denypin");
        if(vBox != null) {
            Platform.runLater(()->{
                vBox.getChildren().add(new Text("Неверный пароль!"));
                System.out.println("loh");
            });
        }
    }
    public void startGame(Stage stage) throws IOException {
        if(flowPane != null) {
            if(in.read() == TEST) {
                millis = 10000;
                Platform.runLater(() -> {
                    stage.setScene(sceneFlow);
                    System.out.println("loh");
                });
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
                    millis--;
                }));
                questionCount++;
                timeline.setCycleCount(millis);
                timeline.play();
            }
            else{
                millis = 10000;
                Platform.runLater(() -> {
                    stage.setScene(fillinScene);
                    System.out.println("loh");
                });
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
                    millis--;
                }));
                questionCount++;
                timeline.setCycleCount(millis);
                timeline.play();
            }
        }
        System.out.println("start");
    }
    public void waitNextQuestion(Stage stage, int second){
        System.out.println("krasava");
        Platform.runLater(() -> {
            stage.setScene(new Scene(new StackPane(new Text("Waiting next game " + second)), 500, 500));
        });
        System.out.println("bitty");
    }
    public void waitNextQuestion(Stage stage){
        System.out.println("krasava");
        Platform.runLater(() -> {
            Text text = new Text();
            text.setText("Waiting next game");
            text.setStyle("-fx-font-family: Ubuntu;-fx-font-size: 18");
            stage.setScene(new Scene(new StackPane(text), 600, 600));
        });
        System.out.println("bitty");
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
    private void send(int msg) throws IOException {
        try {
            out.write(msg);
            out.flush();
        } catch (IOException ignored) {
        }
    }
}