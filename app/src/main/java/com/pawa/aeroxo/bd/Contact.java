package com.pawa.aeroxo.bd;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Contact {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String Surname;
    public String Name;
    public String Email;
    public String FullName;
}
