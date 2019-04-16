package dev.marshall.visualsearch.Database.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import io.reactivex.annotations.NonNull;

@Entity(tableName = "Cart")
public class Cart {

    @NonNull
    @PrimaryKey(autoGenerate =true)
    @ColumnInfo(name="id")
    public  int id;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="image")
    public String image;

    @ColumnInfo(name="amount")
    public  int amount;

    @ColumnInfo(name="price")
    public  double price;



}
