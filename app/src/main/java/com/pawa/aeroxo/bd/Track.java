package com.pawa.aeroxo.bd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Track {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String trackNumber;
    public String amount;
    public String status;
    public String Name;

    @Override
    public boolean equals(@Nullable Object obj) {
        Track track = (Track)obj;
        return trackNumber.equals(track.trackNumber)&&amount.equals(track.amount)&&status.equals(track.status)&&Name.equals(track.Name);
    }
}

