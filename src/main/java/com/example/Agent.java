package com.example;

import java.security.DrbgParameters.Reseed;
import java.util.ArrayList;
import java.util.Random;

import sim.engine.*;
import sim.field.grid.ObjectGrid2D;

public class Agent implements Steppable {
    
//TODO
//Wann wird geprüft ob die Constraints erfüllt werden?
//Wie entscheidet der Spieler wo was platziert wird? -> aus Expertenwissen bedienen

//Strategie 1
//Bei einem 25x25 Feld gibt es 2 hoch 625 Kombinationen, jede könnte probiert, validiert und benutzt oder verworfen werden. Wenn ich den gesamten Speicher und Rechenleistung des Universums hätte würde das auch funktionieren
//Strategie 2
//Alle sicher platzierbaren Birnen platzieren, dann wie Strategie 1 verfahren. Reduziert den Suchbaum
//Strategie 3
//Smarteste Stragie, aber welche?


    public ObjectGrid2D tempBoard = null;
    public GameBoard gameboard = null;
    public int stepcounter;
    public String strategy;


    public Agent(){
        super();
        this.stepcounter = 0;
    }

    public Agent(String strategy){
        super();
        this.stepcounter = 0;
        this.strategy = strategy;
    }

    public void step(SimState state){
        this.gameboard = (GameBoard)state;
        this.tempBoard = this.gameboard.field;
        
        //int num = smartStrategy(1);
        //System.out.println(num);
        //completeRandomStrategy();
        createSolutionspaceArrayX();
        smartStrategy(100);
        
/*
        System.out.println(gameboard.solutionspaceArrayX.size());
        System.out.println(gameboard.solutionspaceArrayX.get(2).size());
        System.out.println(gameboard.solutionspaceArrayX.get(2).get(1).length);
*/

    }

    public void smartSetter(){
        //Experiment
        int tempX;
        int tempY;
        while(!validateSolution()){

        for (int i = 0; i<gameboard.solutionspaceArrayX.size(); i++){
            for( int z = 0; z<gameboard.solutionspaceArrayX.get(i).size(); z++){
                tempX = gameboard.solutionspaceArrayX.get(i).get(z)[0];
                tempY = gameboard.solutionspaceArrayX.get(i).get(z)[1];
                setBulb(tempX, tempY);
            }
        }
        
    }

        //[Alle waagerechten leeren felder][Array der Locations][Location]
        //Durchlaufen
        while(numPlaceableBulbs()!=0){
        //Setbulbs

        if(validateSolution()){
            System.out.println("Solution found");
            break;
            }
        }           
    }

    public void setBulb(int x, int y){
        //Setzt Birnen
        if(!checkConstraintViolation(x, y)){
        tempBoard.set(x, y, new Bulb());
        illuminate(x, y);

        if(!isOutOfBounds(x+1, y) && tempBoard.get(x+1, y).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x+1, y);
            tempWall.numberLeftoverBulbs--;

        }
        if(!isOutOfBounds(x-1, y) && tempBoard.get(x-1, y).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x-1, y);
            tempWall.numberLeftoverBulbs--;
            
        }
        if(!isOutOfBounds(x, y+1) && tempBoard.get(x, y+1).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x, y+1);
            tempWall.numberLeftoverBulbs--;
            
        }
        if(!isOutOfBounds(x, y-1) && tempBoard.get(x, y-1).getClass() == Wall.class){
            Wall tempWall = (Wall) tempBoard.get(x, y-1);
            tempWall.numberLeftoverBulbs--;
            
            
        }
        //Wenn bulb platiert wird muss Leftoverbulbs von den Wall neighbors dekrementiert werden, in der Hauptmethode unten wird nähmlich sonst nur eine Mauer dekrementiert
        }
    }

    public int numPlaceableBulbs(){
        //Gibt die Zahl der Felder zurück die nicht als nicht platzierbar gekennzeichnet sind
        int placeable= 0;
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isEmptyField(x, y)){
                    EmptyField tempField = (EmptyField) tempBoard.get(x, y);
                    if (!tempField.implaceable && !tempField.illuminated){placeable++;}
                }
            }
        }
        return placeable;

    }

    public int numNotIlluminated(){
        //Gibt die Zahl der nicht beleuchteten Felder zurück
        int notIlluminated = 0;
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isEmptyField(x, y) && !isIlluminated(x, y)){notIlluminated++;}
            }
        }
        return notIlluminated;
    }

    public void setLocationPlaceableNonTrivialBulbs(){
        //Füllt das generische Array locationPlaceableNonTrivialBulbs, gibt die Zahl der möglichen nicht trivialen platzierbaren Birnen an
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isEmptyField(x, y)){
                    EmptyField tempField = (EmptyField) tempBoard.get(x, y);
                    if (!tempField.implaceable && !tempField.illuminated){
                        Integer[] temp = {x,y};
                        gameboard.locationPlaceableNonTrivialBulbs.add(temp);
                    }
                }
            }
        }
    }

    public void createSolutionspaceArrayX(){
        ArrayList<Integer[]> tempList = new ArrayList<Integer[]>();
        for(int y = 0; y<tempBoard.height;y++){
            for(int x = 0; x<tempBoard.width;x++){
                //Create Array of connected X empty fields and add it to the ArrayList, when there are no connectable fields add to the first dimension ArrayList
                if(isEmptyField(x,y)){
                    Integer[] temp = {x,y};
                    tempList.add(temp);
                    if(x==tempBoard.width-1){
                        gameboard.solutionspaceArrayX.add(tempList);
                        tempList = new ArrayList<Integer[]>();
                        continue;
                    }
                }
                else if(!tempList.isEmpty()){
                    //System.out.println(tempList.size());
                    gameboard.solutionspaceArrayX.add(tempList);
                    tempList = new ArrayList<Integer[]>();
                    
                }
            }
        }
        
    }

    public boolean checkConstraintViolation(int x, int y){
        //Die Prüfung der numbered Wall Constraints ist nicht notwendig da es durch die Art der Platzierung bereits behandelt wird
        if(!isWall(x, y) && !isBulb(x, y) && isEmptyField(x, y) && !isIlluminated(x, y) && !isImplaceable(x, y)){
            return false;
        }
        return true;
    }

    //Die nächsten Prüfungen sind selbsterklärend
    public boolean isWall(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return true;}
        if(tempBoard.get(x, y).getClass() == Wall.class){return true;}
        else{return false;}

    }

    public boolean isEmptyField(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return false;}
        if(tempBoard.get(x, y).getClass() == EmptyField.class){return true;}
        else{return false;}
    }

    public boolean isImplaceable(int x, int y){
        EmptyField tempField = (EmptyField) tempBoard.get(x, y);
        return tempField.implaceable;
    }

    public boolean isIlluminated(int x, int y){
       EmptyField tempField = (EmptyField) tempBoard.get(x, y);
       return tempField.illuminated;
    }

    public boolean isOutOfBounds(int x, int y){
        if(x>tempBoard.width-1 || x<0 || y>tempBoard.height-1|| y<0){return true;}
        else{return false;}
    }

    public boolean isBulb(int x, int y){
        if(isOutOfBounds(x, y)){return false;}
        if(tempBoard.get(x, y).getClass() == Bulb.class){return true;}
        else{return false;}
    }

    public int numBulbs(){
        //Gibt die Zahl der Birnen auf dem Spielbrett zurück
        int numBulbs = 0;
        for(int x = 0; x<tempBoard.width;x++){
            for(int y = 0; y<tempBoard.height;y++){
                if(isBulb(x, y)){numBulbs++;}
            }
        }
        return numBulbs;
    }

    public void illuminate(int x, int y){
        //Beleuchtet die Felder in alle 4 Richtungen bis eine Mauer erreicht ist
        //Alternative: Ein Beleuchtungscounter ermöglicht das entfernen einer Birne, also eventuell ToDo
        int tempX = x;
        int tempY = y;
        while(isEmptyField(tempX+1, tempY)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX+1, tempY);
            tempField.illuminated = true;
            tempX++;
        }

        tempX = x;
        tempY = y;
        while(isEmptyField(tempX-1, tempY)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX-1, tempY);
            tempField.illuminated = true;
            tempX--;
        }

        tempX = x;
        tempY = y;
        while(isEmptyField(tempX, tempY+1)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY+1);
            tempField.illuminated = true;
            tempY++;
        }

        tempX = x;
        tempY = y;
        while(isEmptyField(tempX, tempY-1)){
            EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY-1);
            tempField.illuminated = true;
            tempY--;
        }
    }

    public boolean validateNumBulbsOnWall(){
        //Prüft ob die Zahl der Birnen an einer Mauer mit der Constraint übereinstimmt
        int tempX;
        int tempY;
        int bulbCounter;
        boolean constraintViolation = false;

        for(int i = 0; i<gameboard.numberedWallLocations.size();i++){
            bulbCounter = 0;
            tempX = gameboard.numberedWallLocations.get(i)[0];
            tempY = gameboard.numberedWallLocations.get(i)[1];
            Wall tempWall = (Wall) tempBoard.get(tempX, tempY);

            if(isBulb(tempX+1,tempY)){bulbCounter++;}
            if(isBulb(tempX-1,tempY)){bulbCounter++;}
            if(isBulb(tempX,tempY+1)){bulbCounter++;}
            if(isBulb(tempX,tempY-1)){bulbCounter++;}

            if(bulbCounter > tempWall.numberAdjascentBulbs){constraintViolation = true;}
        }
        return !constraintViolation;
    }

    public boolean validateSolution(){
        //Gibt zurück ob die Lösung validie ist
        if(numNotIlluminated() == 0 && validateNumBulbsOnWall()){return true;}
        else{return false;}
    }

    public void placeTrivialBulbs(int degreeOfSmartmode){
        //first degree smartmode
        //place trivial bulbs (including surrounding walls)
        //only one iteration

        //second degree smartmode
        //place trivial bulbs iteratively (including surrounding walls)


        //check for implaceable fields in a x++ and y++ loop
        int smartmode = degreeOfSmartmode;
        boolean trivialTrigger;
        int tempX;
        int tempY;
        int tempFreeNeighbors;

        for(int i = 0;i<gameboard.emptyFieldLocations.size();i++){
            int wallCounter = 0;
            tempX = gameboard.emptyFieldLocations.get(i)[0];
            tempY = gameboard.emptyFieldLocations.get(i)[1];
            if(isWall(tempX+1,tempY)){wallCounter++;}
            if(isWall(tempX-1,tempY)){wallCounter++;}
            if(isWall(tempX,tempY+1)){wallCounter++;}
            if(isWall(tempX,tempY-1)){wallCounter++;}
            if(wallCounter == 4){setBulb(tempX, tempY);}
        }

        do{
            trivialTrigger = false;
            for(int i=0;i<gameboard.numberedWallLocations.size();i++){
                tempFreeNeighbors = 0;
                tempX = gameboard.numberedWallLocations.get(i)[0];
                tempY = gameboard.numberedWallLocations.get(i)[1];    
                Wall tempWall = (Wall) tempBoard.get(tempX, tempY);
    
    
                //Wenn die Mauer eine Null anzeigt wird die von neumann nachbarschaft als nicht platzierbar gekennezeichnet
                if(tempWall.numberLeftoverBulbs == 0){
                    if(isEmptyField(tempX+1, tempY)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX+1, tempY);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                        
                    }
                    if(isEmptyField(tempX-1, tempY)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX-1, tempY);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                        
                    }
                    if(isEmptyField(tempX, tempY+1)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY+1);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                        
                    }
                    if(isEmptyField(tempX, tempY-1)){
                        EmptyField tempField = (EmptyField) tempBoard.get(tempX, tempY-1);
                        if(!tempField.implaceable && smartmode==2){trivialTrigger=true;}
                        tempField.implaceable = true;
                    }
                    continue;
                }
    
                //Wenn das Feld in der von neumann nachbarschaft leer ist, nicht beleuchetet wird und nicht als nicht platzierbar gekennzeichnet ist wird es als freier Nachbar gezählt
                if(isEmptyField(tempX+1,tempY) && !isIlluminated(tempX+1, tempY) && !isImplaceable(tempX+1, tempY)){tempFreeNeighbors++;}
                if(isEmptyField(tempX-1,tempY) && !isIlluminated(tempX-1, tempY) && !isImplaceable(tempX-1, tempY)){tempFreeNeighbors++;}
                if(isEmptyField(tempX,tempY+1) && !isIlluminated(tempX, tempY+1) && !isImplaceable(tempX, tempY+1)){tempFreeNeighbors++;}
                if(isEmptyField(tempX,tempY-1) && !isIlluminated(tempX, tempY-1) && !isImplaceable(tempX, tempY-1)){tempFreeNeighbors++;}

                if(tempWall.numberLeftoverBulbs == tempFreeNeighbors){
                    if(isEmptyField(tempX+1,tempY) && !isIlluminated(tempX+1, tempY) && !isImplaceable(tempX+1, tempY)){
                        setBulb(tempX+1, tempY);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX-1,tempY)  && !isIlluminated(tempX-1, tempY) && !isImplaceable(tempX-1, tempY)){
                        setBulb(tempX-1, tempY);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX,tempY+1) && !isIlluminated(tempX, tempY+1) && !isImplaceable(tempX, tempY+1)){
                        setBulb(tempX, tempY+1);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                    if(isEmptyField(tempX,tempY-1) && !isIlluminated(tempX, tempY-1) && !isImplaceable(tempX, tempY-1)){
                        setBulb(tempX, tempY-1);
                        if(smartmode==2){trivialTrigger = true;}
                    }
                }
            }
        } while(trivialTrigger == true);
    }   

    public void removeNonTrivialBulbs(){
        //illuminate müsste dafür noch eine gegenteilige Funktion haben 
        int tempX;
        int tempY;

        for(int i = 0; i<gameboard.locationPlaceableNonTrivialBulbs.size();i++){
            tempX = gameboard.locationPlaceableNonTrivialBulbs.get(i)[0];
            tempY = gameboard.locationPlaceableNonTrivialBulbs.get(i)[1];
            tempBoard.set(tempX, tempY, new EmptyField());
        }
    }

    public int chaoticPlacement(boolean printEachIteration){
        Random dice = new Random();
        int counter = 0;
        int tempX;
        int tempY;
        while(numPlaceableBulbs()!=0){
            for(int i = 0; i<gameboard.locationPlaceableNonTrivialBulbs.size();i++){
                tempX = gameboard.locationPlaceableNonTrivialBulbs.get(i)[0];
                tempY = gameboard.locationPlaceableNonTrivialBulbs.get(i)[1];
                if(dice.nextInt(2) == 0){
                    setBulb(tempX, tempY);
                }

            
            }
            if(validateSolution()){
                System.out.println("Solution found");
                break;
            }
            if(numPlaceableBulbs() == 0){
                if(printEachIteration){System.out.print(".");}
                counter++;
                removeNonTrivialBulbs();
            }
               
        }

        return counter;
    }

    public void completeRandomStrategy(){
        setLocationPlaceableNonTrivialBulbs();
        int numIterations = chaoticPlacement(false);
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed"); 
        System.out.println("Solution validated: " + validateSolution() + " after " + numIterations + " Iterations"); 

    }

    public int smartStrategy(int numSimulations){

        int totalNumIterations = 0;

        int outpouNumNotIlluminated = numNotIlluminated();
        int outputNumPlaceableBulbs = numPlaceableBulbs();

        
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed\n");
        placeTrivialBulbs(2);

        int numBulbs = numBulbs();
        System.out.println(numBulbs + " trivial bulbs have been placed");

        outpouNumNotIlluminated -= numNotIlluminated();
        outputNumPlaceableBulbs -= numPlaceableBulbs();
        System.out.println("Number of placeable bulbs have been reduced by " + outputNumPlaceableBulbs);
        System.out.println("Number of fields to be illuminated have been reduced by " + outpouNumNotIlluminated);
        System.out.println("There are "+numNotIlluminated() + " fields which are not illuminated");
        System.out.println("There are "+numPlaceableBulbs() + " fields in which a bulb could be placed\n");        

        setLocationPlaceableNonTrivialBulbs();

        for(int i = 0; i<numSimulations; i++){
            removeNonTrivialBulbs();
            int numIterations = chaoticPlacement(false);
            System.out.println("Solution validated: " + validateSolution() + " after " + numIterations + " Iterations\n");
            totalNumIterations += numIterations;

        }

        return totalNumIterations/numSimulations;

    }
}

