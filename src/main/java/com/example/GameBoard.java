package com.example;

import sim.engine.*;
import sim.field.grid.ObjectGrid2D;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class GameBoard extends SimState {

    //Selber anpassen
    static String csv_path = "C:\\Users\\johan\\iCloudDrive\\Main\\Master\\Semester 3\\Intelligent Systems\\Assignment 2\\IS_A2\\CSV\\";

//Spielfeld / Environment / Simulation
    public GameBoard(long seed, int csv) {
		super(seed);
        this.csv = csv;
	}
    
    public String[] strategies = {"bruteForce","smart"};

    //Folder enthält die CSV Dateien
    public File folder = new File(csv_path);
    //Files ist ein Array der Pfade der CSV Dateien, hier wird zunächst nur die Länge des Arrays (Die Zahl der CSV Dateien) deklariert
    public File[] files = new File[folder.listFiles().length];
    //Wenn die Klasse Gamboard gemacht wird wird mit csv bestimmt welche CSV genommen wird. Es soll damit die Erstellung des Gameboards mit jeder CSV geloopt werden
    //Temporär
    private int csv;

    //Liste der Locations der nummerierten Mauern
    public ArrayList<Integer[]> numberedWallLocations = new ArrayList<Integer[]>();
    public ArrayList<Integer[]> emptyFieldLocations = new ArrayList<Integer[]>();
    public ArrayList<Integer[]> locationPlaceableNonTrivialBulbs = new ArrayList<Integer[]>();

    public ArrayList<ArrayList<Integer[]>> solutionspaceArrayX = new ArrayList<ArrayList<Integer[]>>();
    

    //Spielbrett als ObjectGrid2D
    public ObjectGrid2D field = null;



    //Das Spielfeld soll wie ein einfaches Array sein, die Spieler spawnen die Figuren an fixen stellen. Falls ein Spieler am Ursprungspunkt-2 ist kommt er auf ein neues kleines array dass die Ziellinie abbildet
    

    public void finish(){
        super.finish();
    }

    public void start(){
        super.start();
        this.setField();
        
        //locationtest
        /*
        System.out.println(field.get(1,1));
        EmptyField emptyFieldTest = (EmptyField) field.get(1, 1);
        System.out.println(emptyFieldTest.illuminated);
        System.out.println(field.get(4,0));
        Wall wallTest = (Wall) field.get(4,0);
        System.out.println(wallTest.numberAdjascentBulbs);
        System.out.println(wallTest.blank);
        */
        Agent agent = new Agent();
        schedule.scheduleOnce(agent);
        schedule.step(this);

        //System.out.println(solutionspaceArrayX.get(0).get(1)[1]);
        //System.out.println(solutionspaceArrayX.get(0).size());
        //test = solutionspaceArrayX.get(0);
        
       
        //System.out.println(test.get(0).getClass());
        
        
    }

    public static void main(String[] args){
        
        //Hier würde dann mithilfe des files Array geloopt werden und die Simulation mindestens einmal pro CSV ausgeführt werden
        
        for(int i=0; i<1;i++){
        GameBoard board = new GameBoard(System.currentTimeMillis(), 1);
        board.setFilepaths();
        board.start();
        board.finish();
        }
        
        
        
        
    }

    public void setFilepaths(){
        //files ist ein Array der CSV Dateien
        this.files = folder.listFiles();
    }

    public int[] returnCSVDimension(String pathToCsv){
        //Gibt die Spielfeldgröße aus, man könnte es auch statisch machen aber es ist elegant die Größe des Spielbretts dynamisch zu machen
        int rowCount = 0;
        int columnCount = 0;
        try{
            Scanner sc = new Scanner(new File(pathToCsv));
            while(sc.hasNext()){
               sc.nextLine();
               rowCount++;
            }
            sc = new Scanner(new File(pathToCsv));
            sc.next();
            //regex Split weil die normale Methode keine trailing delimiters erkennt
            String[] rowArray = sc.next().split("\\;",-1);
            columnCount = rowArray.length;
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
        int[] lenghts = {rowCount, columnCount};
        return lenghts;
    }

    
    public void setField(){
        //Füllt das Sparse2DGrid mit Objekten, je nach CSV
        int[] size = returnCSVDimension(files[csv].toString());
        this.field = new ObjectGrid2D(size[1], size[0]);
        String[] rows;
        
        try{
            Scanner sc = new Scanner(new File(files[csv].toString())); 
            for(int row = 0; row<size[0] ;row++){
                rows = sc.nextLine().split("\\;", -1);
                for (int column = 0; column<size[1]; column++){
                    if(rows[column].equals("x")){
                        field.set(column, row, new Wall(true));
                    }
                    else if(rows[column].equals("")){
                        field.set(column, row, new EmptyField());
                        Integer[] temp = {column,row};
                        emptyFieldLocations.add(temp);
                    }
                    else{
                        field.set(column, row, new Wall(Integer.parseInt(rows[column])));
                        Integer[] temp = {column,row};
                        numberedWallLocations.add(temp);
                        
                    }
                }   
            }

        } catch (FileNotFoundException e){
            System.out.println("File probably not found");
        }   
    }





    public void nothing(){
        //filereadertest
        try{
            Scanner sc = new Scanner(new File(files[csv].toString()));
            String test = sc.nextLine();
            String[] test2;
            System.out.println(test);
            test2 = test.split("\\;", -1);
            System.out.println(test2);
        } catch(FileNotFoundException e){
            System.out.println("filenotfound");
        }
    }
}
