package org.kk.redpacktools.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class RedPackLog {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public double money;

    public long time;

    public RedPackLog(String name, double money, long time) {
        this.name = name;
        this.money = money;
        this.time = time;
    }

    @Override
    public String toString() {
        return "RedPackLog{" +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", time=" + time +
                '}';
    }
}
