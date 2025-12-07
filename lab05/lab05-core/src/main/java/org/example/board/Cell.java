package org.example.board;

import org.example.model.Figure;

public class Cell {
    Figure figure;
    boolean treasure;
    boolean creatorHere;

    private boolean laserEffect;
    private boolean pushEffect;

    public boolean isFreeForFigure() {
        return figure == null && !creatorHere;
    }

    public Figure getFigure() {
        return figure;
    }

    public boolean hasTreasure() {
        return treasure;
    }

    public boolean isCreatorHere() {
        return creatorHere;
    }



    //efekty laser, odpychanie
    public boolean hasLaserEffect() {
        return laserEffect;
    }

    public void setLaserEffect(boolean laserEffect) {
        this.laserEffect = laserEffect;
    }

    public boolean hasPushEffect() {
        return pushEffect;
    }

    public void setPushEffect(boolean pushEffect) {
        this.pushEffect = pushEffect;
    }
}
