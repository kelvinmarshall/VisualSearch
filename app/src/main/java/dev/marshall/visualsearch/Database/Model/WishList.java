package dev.marshall.visualsearch.Database.Model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Wishlist")
public class WishList {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name="id")
    public  int id;

    @ColumnInfo(name="name")
    public  String name;

    @ColumnInfo(name="image")
    public  String image;

    @ColumnInfo(name="price")
    public  String price;
}
