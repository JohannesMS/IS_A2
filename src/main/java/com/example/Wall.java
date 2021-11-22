package com.example;

public class Wall {
    //Spielfigur als Objekt notwendig?
    public int numberAdjascentBulbs;
    public boolean blank = false;

    public Wall(){

    }
    
    public Wall(int num){
        this.numberAdjascentBulbs = num;
    }

    public Wall(boolean bool){
        this.blank = bool;
    }
}
