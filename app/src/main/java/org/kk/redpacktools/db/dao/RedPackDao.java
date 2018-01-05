package org.kk.redpacktools.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.kk.redpacktools.db.entities.RedPackLog;

@Dao
public interface RedPackDao {

    @Query("select * from RedPackLog")
    RedPackLog[] queryAll();

    @Insert
    void insert(RedPackLog redPackLog);

    @Query("select * from RedPackLog where name = :name")
    RedPackLog[] queryByName(String name);
}
