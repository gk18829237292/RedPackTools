package org.kk.redpacktools.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.kk.redpacktools.db.dao.RedPackDao;
import org.kk.redpacktools.db.entities.RedPackLog;

@Database(entities = {RedPackLog.class}, version = 1)
public abstract class RedPackDataBase extends RoomDatabase {
    public abstract RedPackDao getRedPackDao();
}
