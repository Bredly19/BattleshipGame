package com.company;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Battleship battleship = new Battleship();
        battleship.startGame();
    }
}

class Battleship {

    private final Player player1 = new Player();
    private final Player player2 = new Player();

    private void waitEnterKey() {
        System.out.print("Press Enter and pass the move to another player\n...");
        try {
            int a = System.in.read();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void startGame() {

        System.out.println("Player 1, place your ships on the game field");
        player1.print();
        player1.setShipsOnPosition();

        waitEnterKey();

        System.out.println("Player 2, place your ships on the game field");
        player2.print();
        player2.setShipsOnPosition();

        waitEnterKey();

        while (player1.allShipsDestroyed() || player2.allShipsDestroyed()) {

            player2.printWithoutShips();
            System.out.println("---------------------");
            player1.print();
            System.out.println("Player 1, it's your turn:");
            player2.shoot();

            waitEnterKey();

            player1.printWithoutShips();
            System.out.println("---------------------");
            player2.print();
            System.out.println("Player 2, it's your turn:");
            player1.shoot();

            waitEnterKey();
        }
    }
}


class Player {

    private final Scanner scanner = new Scanner(System.in);

    private final Field field = new Field();

    private final Ship[] ships = {
            new Ship("Aircraft Carrier", 5),
            new Ship("Battleship", 4),
            new Ship("Submarine", 3),
            new Ship("Cruiser", 3),
            new Ship("Destroyer", 2)
    };


    public void printWithoutShips(){

        char[][] map = field.getMap();

        System.out.print(' ');

        for (int i = 1; i <= map.length; i++) {
            System.out.print(" " + i);
        }

        System.out.println();

        for (int i = 0; i < map.length; i++) {
            System.out.print( (char) ('A' + i));
            for (int j = 0; j < map.length; j++) {
                System.out.print(map[i][j] == 'O' ? " " + '~' : " " + map[i][j]);
            }
            System.out.println();
        }
    }

    public void print() {

        char[][] map = field.getMap();

        System.out.print(' ');

        for (int i = 1; i <= map.length; i++) {
            System.out.print(" " + i);
        }

        System.out.println();

        for (int i = 0; i < map.length; i++) {
            System.out.print( (char) ('A' + i));
            for (int j = 0; j < map.length; j++) {
                System.out.print(" " + map[i][j]);
            }
            System.out.println();
        }
    }


    void setShipsOnPosition() {

        Coordinate first = new Coordinate();
        Coordinate second = new Coordinate();

        for (Ship ship : ships) {

            System.out.printf("Enter the coordinates of the %s (%d cells):\n", ship.getName(), ship.getSize());

            while (true) {
                try {
                    String str = scanner.nextLine();
                    first.stringToCoordinate(str.split("\\s")[0]);
                    second.stringToCoordinate(str.split("\\s")[1]);

                    if (chekPosition(first, second, ship.getSize())) { break; }
                } catch (Exception e) {
                    System.out.println("Error! " + e.getMessage() + ". Try again:");
                }
            }

            ship.setCoordinates(first, second);
            field.setShipToPosition(ship);
            System.out.println();
            this.print();
        }
    }

    private boolean chekPosition(Coordinate first, Coordinate second, int size) {

        if (first == null || second == null) {
            throw new NullPointerException("Exception: Coordinate is null!");
        }

        if (first.getRow() > second.getRow() || first.getColumn() > second.getColumn()) {
            first.swap(second);
        }

        if (first.getColumn() != second.getColumn() && first.getRow() != second.getRow()) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        if (size != Math.abs(second.getColumn() - first.getColumn()) + 1 &&
                size != Math.abs(second.getRow() - first.getRow()) + 1){
            System.out.println("Error! Wrong length of the Submarine! Try again:");
            return false;
        }

        int length = field.getMap().length - 1;

        if (first.getColumn() < 0 || first.getColumn() > length || first.getRow() < 0 || first.getRow() > length ||
                second.getColumn() < 0 || second.getColumn() > length || second.getRow() < 0 || second.getRow() > length) {
            System.out.println("Error! You're out of the war field! Try again:");
            return false;
        }

        if (this.isCollision(first, second)) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;
        }

        return true;
    }

    private boolean isCollision(Coordinate first, Coordinate second){

        if (first == null || second == null) {
            throw new NullPointerException("Exception: Coordinate is null!");
        }

        char[][] map = field.getMap();
        int width = map.length - 1;
        int height = map[0].length - 1;

        Coordinate beginArea = new Coordinate();
        Coordinate endArea = new Coordinate();

        if (first.getRow() == second.getRow()) { //hor
            beginArea.setCoordinate(Math.max(first.getRow() - 1, 0), first.getColumn());
            endArea.setCoordinate(Math.min(second.getRow() + 1, height), second.getColumn());

            if (map[first.getRow()][Math.max(first.getColumn() - 1, 0)] == State.WHOLE.getLetterState() ||
                    map[second.getRow()][Math.min(second.getColumn() + 1, width)] == State.WHOLE.getLetterState()) {
                return true;
            }
        } else { //ver
            beginArea.setCoordinate(first.getRow(), Math.max(first.getColumn() - 1, 0));
            endArea.setCoordinate(second.getRow(), Math.min(second.getColumn() + 1, width));

            if (map[Math.max(first.getRow() - 1, 0)][first.getColumn()] == State.WHOLE.getLetterState() ||
                    map[Math.min(second.getRow() + 1, height)][second.getColumn()] == State.WHOLE.getLetterState()) {
                return true;
            }
        }

        for (int i = beginArea.getRow(); i <= endArea.getRow(); i++) {
            for (int j = beginArea.getColumn(); j <= endArea.getColumn() ; j++) {
                if (map[i][j] == State.WHOLE.getLetterState()) {
                    return true;
                }
            }
        }

        return false;
    }

    void shoot() {

        int length = field.getMap().length;

        Coordinate coordinate = new Coordinate();

        while (true) {
            try {
                coordinate.stringToCoordinate(scanner.next());

                if (coordinate.getRow() > 0 || coordinate.getRow() < length ||
                        coordinate.getColumn() > 0 || coordinate.getColumn() < length) {

                    switch (field.getMap()[coordinate.getRow()][coordinate.getColumn()]) {
                        case '~': {
                            field.setMissed(coordinate);
                            System.out.println("You missed!");
                            return;
                        }
                        case 'O': {
                            field.setHit(coordinate);

                            for (Ship ship : ships) {
                                if (ship.checkHit(coordinate)) {
                                    if (ship.getNumberOfWholeParts() == 0) {
                                        if (!this.allShipsDestroyed()) {
                                            System.out.println("You sank the last ship. You won. Congratulations!");
                                            return;
                                        }
                                        System.out.println("You sank a ship!");
                                    } else {
                                        System.out.println("You hit a ship!");
                                    }
                                    return;
                                }
                            }
                        }
                        case 'X': return;
                    }

                } else {
                    System.out.println("Error! You entered the wrong coordinates! Try again:");
                }
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage() + ". Try again:");
            }
        }
    }

    boolean allShipsDestroyed() {

        for (Ship ship: ships) {
            if (ship.getNumberOfWholeParts() != 0) {
                return true;
            }
        }
        return false;
    }
}


class Field {

    private final char[][] map;

    Field() {
        this.map = new char[10][10];

        for (char[] chars : this.map) {
            Arrays.fill(chars, '~');
        }
    }

    public char[][] getMap() {
        return map;
    }

    public void setMissed(Coordinate coordinate) {

        if (coordinate == null) {
            throw new NullPointerException("Exception: Coordinate is null!");
        }

        char missed = 'M';
        this.map[coordinate.getRow()][coordinate.getColumn()] = missed;
    }

    public void setHit(Coordinate coordinate) {

        if (coordinate == null) {
            throw new NullPointerException("Exception: Coordinate is null!");
        }

        char hit = 'X';
        this.map[coordinate.getRow()][coordinate.getColumn()] = hit;
    }


    public void setShipToPosition(Ship ship) {

        if (ship == null) {
            throw new NullPointerException("Exception: Ship is null!");
        }

        for (PartShip part : ship.getParts()) {
            this.map[part.getRow()][part.getColumn()] = part.getLetterState();
        }
    }
}

class Ship {

    private final String name;

    private final PartShip[] parts;

    private int numberOfWholeParts;

    private static int numberOfWholeShips = 0;

    public Ship(String name, int size) {
        this.parts = new PartShip[size];
        this.name = name;
        this.numberOfWholeParts = size;
        numberOfWholeShips += size;
    }

    void setCoordinates(Coordinate first, Coordinate second) {

        if (first == null || second == null) {
            throw new NullPointerException("Exception: Coordinates begin or end is null!");
        }

        if (first.getRow() == second.getRow()) {
            for (int i = 0; i < this.parts.length; i++) {
                parts[i] = new PartShip(first.getRow(), first.getColumn() + i);
            }
        } else {
            for (int i = 0; i < this.parts.length; i++) {
                parts[i] = new PartShip(first.getRow() + i, first.getColumn());
            }
        }
    }

    boolean checkHit(Coordinate coordinate) {

        if (coordinate == null || parts[0] == null) {
            throw new NullPointerException("Exception: coordinate or parts is null!");
        }

        for (PartShip part : parts) {
            if (part.equals(coordinate)) {
                part.setState(State.DESTROYED);
                --this.numberOfWholeParts;
                return true;
            }
        }
        return false;
    }

    public int getNumberOfWholeParts() {
        return numberOfWholeParts;
    }

    public PartShip[] getParts() {
        return parts;
    }

    public PartShip getFirstParts() {
        return parts[0];
    }

    public PartShip getSecondParts() {
        return parts[parts.length - 1];
    }

    public boolean isHorizontal() {
        return this.getFirstParts().getRow() == this.getSecondParts().getRow();
    }

    String getName() {
        return name;
    }

    int getSize() {
        return this.parts.length;
    }

}

class PartShip extends Coordinate {

    private State state;

    PartShip() {
        super();
        this.state = State.WHOLE;
    }

    PartShip(int row, int column) {
        super(row, column);
        this.state = State.WHOLE;
    }

    char getLetterState() {
        return this.state.getLetterState();
    }

    State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
    }
}

enum State {
    WHOLE('O'),
    DESTROYED('X');

    private final char letterState;

    State(char letterState) {
        this.letterState = letterState;
    }

    char getLetterState() {
        return this.letterState;
    }
}

class Coordinate {

    private int row;
    private int column;

    Coordinate() {
        this.row = -1;
        this.column = -1;
    }

    Coordinate(String str) {
        this.stringToCoordinate(str);
    }

    Coordinate(int row, int column) {
        this.setCoordinate(row, column);
    }

    Coordinate(Coordinate coordinate) {
        this.setCoordinate(coordinate);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    void stringToCoordinate(String str) {
        if (str == null) {
            throw new NullPointerException("Exception: str is null!");
        }

        this.row = str.charAt(0) - 'A';
        this.column = Integer.parseInt(str.substring(1, str.length())) - 1;
    }

    void setCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            throw new NullPointerException("Exception: coordinate is null!");
        }

        this.row = coordinate.row;
        this.column = coordinate.column;
    }

    void setCoordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    boolean equals(Coordinate coordinate) {

        if (coordinate == null) {
            throw new NullPointerException("Exception: coordinate is null!");
        }

        return this.column == coordinate.column && this.row == coordinate.row;
    }

    void swap(Coordinate coordinate) {

        if (coordinate == null) {
            throw new NullPointerException("Exception: coordinate is null!");
        }

        int row = coordinate.row;
        int column = coordinate.column;

        coordinate.row = this.row;
        coordinate.column = this.column;

        this.row = row;
        this.column = column;
    }
}
