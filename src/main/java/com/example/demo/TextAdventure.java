package com.example.demo;

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;


public class TextAdventure extends Application {

    private TextArea output = new TextArea();
    private TextField input = new TextField();
    static int max_l = 10;
    static int min_l = 1;
    static int max = 10;
    static int min = 1;
    static int range = max - min + 1;
    static int range_l = max_l - min_l + 1;
    static int randX = 0;
    static int randY = 0;
    //int luck=1;
    //int luck=8;

    int best_score=0;
    static int setLuck()
    {
        int luck=(int)(Math.random()*range_l)+min_l;
        return luck;
    }

    private Map<String, Command> commands = new HashMap<>();

    private Room[][] rooms = new Room[5][5];

    private int currentX = 2;
    private int currentY = 2;

    private Parent createContent() {
        output.setPrefHeight(600 - 80);
        output.setFont(Font.font(24));
        output.setEditable(false);
        output.setFocusTraversable(false);


        input.setOnAction(e -> {
            String inputText = input.getText();
            input.clear();
            onInput(inputText);
        });

        VBox root = new VBox(15, output, input);
        root.setPadding(new Insets(15));
        root.setPrefSize(800, 600);
        root.getStyleClass().add("root1");
        root.setStyle("-fx-border-color:none;-fx-font-size: 20; -fx-font-weight: bold; -fx-color:red; -fx-background-color: black; -fx-font-family: 'Bell MT'");
        input.setStyle("-fx-border-color:none;-fx-font-size: 20; -fx-font-weight: bold; -fx-color:white; -fx-background-color: black; -fx-font-family: 'Bell MT'");
        output.setStyle("-fx-border-color:none;-fx-font-size: 20; -fx-font-weight: bold; -fx-color:white; -fx-control-inner-background:#000000; -fx-font-family: 'Bell MT'");
        initGame();
        return root;
    }

    private void initGame() {
        try {
            FileReader fr = new FileReader("best_score_fr.txt");
            Scanner scanner=new Scanner(fr);
            best_score=scanner.nextInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        print("Welcome to Text Adventure");
        println("///" + "Best score: " + best_score + "///");
        initCommands();
        runHelp();
        initRooms();
    }


    private void initCommands() {
        commands.put("exit", new Command("exit", "Exit the game", Platform::exit));

        commands.put("help", new Command("help", "Print all commands", this::runHelp));

        commands.put("west", new Command("west", "Move west", () -> runGo(-1, 0)));
        commands.put("east", new Command("east", "Move east", () -> runGo(1, 0)));
        commands.put("north", new Command("north", "Move north", () -> runGo(0, -1)));
        commands.put("south", new Command("south", "Move south", () -> runGo(0, 1)));

        commands.put("attack", new Command("attack", "Kill monsters in the room", this::runAttack));
    }

    private void initRooms() {
        max = 3;
        min = 0;
        range = max - min + 1;
        randX = 0;
        randY = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                rooms[x][y] = new Room(x, y);
            }
        }
        for (int n=0; n<4; ++n)
        {
            randX = (int)(Math.random() * range) + min;
            randY = (int)(Math.random() * range) + min;
            if (!rooms[randX][randY].hasMonsters() && randX!=2 && randY!=2 && currentX!=4 || currentY != 4 || currentY!=0 || currentX!=0)
            {
                rooms[randX][randY].spawnMonsters();
                /*println("X: "+String.valueOf(randX));
                println("Y: "+String.valueOf(randY));*/
            }else
            {
                --n;
            }
        }
    }

    private Room getCurrentRoom() {
        return rooms[currentX][currentY];
    }

    private void println(String line) {
        output.appendText(line + "\n");
    }
    private void print(String line) {
        output.appendText(line);
    }

    private void onInput(String line) {
        if (!commands.containsKey(line)) {
            println("Command " + line + " not found");
            return;
        }
        commands.get(line).getAction();
    }
    private void runHelp() {
        commands.forEach((name, cmd) -> {
            println(name + "\t" + cmd.getDescription());
        });
    }

    private void runGo(int dx, int dy) {
        if (getCurrentRoom().hasMonsters()) {
            println("The room still has monsters");
            return;
        }
        currentX += dx;
        currentY += dy;
        println("You are now in room: " + currentX + "," + currentY);
        if (currentX==4 || currentY == 4 || currentY==0 || currentX==0)
        {
                println("You won!");
                println("Retry? yes/no");
                currentX=2;
                currentY=2;
                commands.put("yes", new Command("yes", "Retry", this::initGame));
                commands.put("no", new Command("no", "Exit the game", Platform::exit));
        }
    }

    private void runAttack() {
        int luck=setLuck();
        if (luck>3) {
            getCurrentRoom().killMonsters();
            println("All monsters in the room have been killed");
            println(String.valueOf(luck));
            best_score++;
            try {
                FileWriter fw=new FileWriter("best_score_fr.txt");
                fw.write(String.valueOf(best_score));
                fw.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }else
        {
            println("You are dead :˙(");
            println(String.valueOf(luck));
            try {
                FileWriter fw=new FileWriter("best_score_fr.txt");
                fw.write(String.valueOf(best_score));
                fw.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            println("Retry? yes/no");
            currentX=2;
            currentY=2;
            commands.put("yes", new Command("yes", "Retry", this::initGame));
            commands.put("no", new Command("no", "Exit the game", Platform::exit));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());
        stage.setScene(scene);
        stage.show();
    }

    public static class Launcher {
        public static void main(String[] args) {
            Application.launch(TextAdventure.class, args);
        }
    }
}
