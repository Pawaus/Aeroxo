package com.pawa.aeroxo.bd;

import java.util.List;

import androidx.annotation.RequiresPermission;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact")
    public List<Contact>getAll();

    @Insert
    void insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);
}
