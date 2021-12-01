package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TextAdventure extends Application {

    private TextArea output = new TextArea();
    private TextField input = new TextField();

    private Map<String, Command> commands = new HashMap<>();

    private Room[][] rooms = new Room[4][4];

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
        initGame();
        return root;
    }

    private void initGame() {

        println("Welcome to Text Adventure");
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
        int max = 3;
        int min = 0;
        int range = max - min + 1;
        int randX = 0;
        int randY = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                rooms[x][y] = new Room(x, y);
            }
        }
        for (int n=0; n<4; ++n)
        {
            randX = (int)(Math.random() * range) + min;
            randY = (int)(Math.random() * range) + min;
            if (!rooms[randX][randY].hasMonsters())
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

    private void onInput(String line) {
        if (!commands.containsKey(line)) {
            println("Command " + line + " not found");
            return;
        }

        commands.get(line).execute();
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
        if (currentX==3 || currentY == 3 || currentY==0 || currentX==0)
        {
                println("You won!");
                println("Retry? yes/no");
                commands.put("yes", new Command("yes", "Retry", this::initGame));
                commands.put("no", new Command("no", "Exit the game", Platform::exit));
        }
    }

    private void runAttack() {
        getCurrentRoom().killMonsters();
        println("All monsters in the room have been killed");
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    public static class Launcher {
        public static void main(String[] args) {
            Application.launch(TextAdventure.class, args);
        }
    }
}
