package org.kk.redpacktools.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class RedPackLog {
    @PrimaryKey
    private int id;

    private String name;

    private double money;

    private long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RedPackLog(String name, double money, long time) {
        this.name = name;
        this.money = money;
        this.time = time;
    }

    public RedPackLog(String name, double money) {
        this(name,money,new Date().getTime());
    }

    @Override
    public String toString() {
        return "RedPackLog{" +
                "name='" + name + '\'' +
                ", money=" + money +
                ", time=" + time +
                '}';
    }
}
