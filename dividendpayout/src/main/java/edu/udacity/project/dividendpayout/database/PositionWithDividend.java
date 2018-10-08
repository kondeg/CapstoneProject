package edu.udacity.project.dividendpayout.database;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class PositionWithDividend {

    @Embedded
    Position position;

    @Relation(parentColumn =  "id", entityColumn = "positionId")
    List<Dividend> dividends;


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(List<Dividend> dividends) {
        this.dividends = dividends;
    }
}
