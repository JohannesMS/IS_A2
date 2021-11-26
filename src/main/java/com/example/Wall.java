package com.example;

public class Wall {
    //Spielfigur als Objekt notwendig?
    public int numberAdjascentBulbs;
    public int numberLeftoverBulbs;
    public boolean blank = false;

    public Wall(){

    }
    
    public Wall(int num){
        this.numberAdjascentBulbs = num;
        this.numberLeftoverBulbs = num;
    }

    public Wall(boolean bool){
        this.blank = bool;
    }
}
