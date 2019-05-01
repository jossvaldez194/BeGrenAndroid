package com.hakagamesstudio.begreen.RoomDatabase;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface BeGreenDao {

    @Insert(onConflict = REPLACE)
    void replaceCreditCard(CreditCard creditCard);

    @Delete
    void deleteCreditCard(CreditCard... creditCards);

    @Query("SELECT * FROM creditCard")
    CreditCard getAllObject();

    @Query("SELECT * FROM creditCard where idUser = :idUser")
    List<CreditCard> getAll(String idUser);

    @Query("delete from creditCard where numberTarjet =:numberTarjet")
    void deletefromnumberTarjet(String numberTarjet);

}
