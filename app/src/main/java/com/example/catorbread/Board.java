package com.example.catorbread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Board {
    ArrayList <String> cells = new ArrayList <String> ();
    int cat = 4;
    int bread = 4;
    int size = 9;

    public Board () {
    }

    public void setCells (ArrayList <String> cells) {
        this.cells = cells;
    }

    public int getCat () {
        return cat;
    }

    public void setCat (int cat) {
        this.cat = cat;
    }

    public int getBread () {
        return bread;
    }

    public void setBread (int bread) {
        this.bread = bread;
    }

    public int getSize () {
        return size;
    }

    public void setSize (int size) {
        this.size = size;
    }

    public ArrayList <String> getCells () {
        return cells;
    }

    public void init () {
        cells = new ArrayList <String> ();
        cat = 4;
        bread = 4;
        size = 9;
        cells.clear();

        for (int i = 0 ; i < size - 1 ; i++) {
            if (cat > 0) {
                cells.add("c");
                cat--;
            } else if (bread > 0) {
                cells.add("b");
                bread--;
            }
        }
        Random rnd = new Random ();
        int num = rnd.nextInt(2);
        if (num == 1) {
            cells.add("c");
        } else {
            cells.add("b");
        }
        Collections.shuffle(cells);
    }

    public void scan () {
        int bread = 0 , cat = 0;
        for (int i = 0 ; i < cells.size() ; i++) {
            if (cells.get(i).equals("c")) {
                cat++;
            }
            if (cells.get(i).equals("b")) {
                bread++;
            }
        }
        if (cat == 0 || bread == 0) {
            init();
        }

    }
}
