package com.hakagamesstudio.begreen.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CreditCard.class}, version = 2, exportSchema = false)
public abstract class BeGreenDB extends RoomDatabase {


    private static BeGreenDB mInstance;
    public abstract BeGreenDao mPickPalDAO();

    public static BeGreenDB getAppDatabase(Context context) {
        if (mInstance == null) {
            mInstance =
                    Room.databaseBuilder(context.getApplicationContext(), BeGreenDB.class, "begreen-database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }


}
