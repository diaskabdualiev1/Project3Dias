package com.example.project3prostoi2vodnom;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class ServerClientClass extends Thread {

    private final static int JOIN_GAME = 100;
    private final static int SUCCESS = 7;
    private final static int START_GAME = 101;
    private final static int WAIT_ANSWER = 200;
    private final static int DENY = 9;
    private final static int ANSWER = 67;
    private final static int FILLIN = 55;
    private final static int TEST = 66;

    public int[] answers;
    public boolean otvet;

    private Socket socket; // сокет, через который сервер общается с клиентом,
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет

    private int pinCode;
    String nameUser;


    public ServerClientClass(Socket socket, int pinCode) throws IOException {
        this.socket = socket;
        this.pinCode = pinCode;
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("While start");
                int code1 = in.read();
                System.out.println(code1);
                if(code1 == JOIN_GAME) joinGame();
                else if(code1 == ANSWER) checkAnswer();
                System.out.println("While End");
            }
        } catch (IOException e) {
        }

    }

    private void joinGame() throws IOException {
        System.out.println("join");
        nameUser = in.readLine();
        UserUI userUI = new UserUI(nameUser);
        int outPinCode = Integer.parseInt(in.readLine());
        if(outPinCode == pinCode) {
            send(SUCCESS);
            Platform.runLater(() -> {
                Server.addUser(userUI);
            });
        }
        else {
            send(DENY);
            System.out.println("loh");
        }
        System.out.println("4");
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

    public void startGame() throws IOException {
        if(answers == null){
            answers = new int[Server.quiz.questions.size()];
        }
        out.write(START_GAME);
        out.flush();
        if(Server.quiz.questions.get(Server.quizCount) instanceof Test){
            out.write(TEST);
            out.flush();
        }else if(Server.quiz.questions.get(Server.quizCount) instanceof Fillin){
            out.write(FILLIN);
            out.flush();
        }
    }

    public void checkAnswer() throws IOException {
        int test1 = in.read();
        if(test1 == TEST) {
            int index = in.read();
            int millis = in.read();
            otvet = true;
            System.out.println(index);
            Quiz quizCheck = Server.quiz;
            if (quizCheck.questions.get(Server.quizCount - 1) instanceof Test test) {
                System.out.println(test.getAnswer());
                if (index == 1 && Server.button1.getText().equals(test.getAnswer())) {
                    answers[Server.quizCount - 1] = millis / 10;
                    System.out.println("1 dyrys");
                } else if (index == 2 && Server.button2.getText().equals(test.getAnswer())) {
                    answers[Server.quizCount - 1] = millis / 10;
                    System.out.println("2 dyrys");
                } else if (index == 3 && Server.button3.getText().equals(test.getAnswer())) {
                    answers[Server.quizCount - 1] = millis / 10;
                    System.out.println("3 dyrys");
                } else if (index == 4 && Server.button4.getText().equals(test.getAnswer())) {
                    answers[Server.quizCount - 1] = millis / 10;
                    System.out.println("4 dyrys");
                } else {
                    answers[Server.quizCount - 1] = 0;
                }
            }
        }else{
            String text = in.readLine();
            int millis = in.read();
            Quiz quizCheck = Server.quiz;
            if(quizCheck.questions.get(Server.quizCount - 1).equals(text)){
                answers[Server.quizCount - 1] = millis / 10;
            }
            otvet = true;
        }
    }

    public int getBall(){
        int ball = 0;
        for (int i = 0; i < answers.length; i++) {
            ball += answers[i];
        }
        return ball;
    }
}