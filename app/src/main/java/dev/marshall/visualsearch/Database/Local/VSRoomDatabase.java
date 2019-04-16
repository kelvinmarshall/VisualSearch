package dev.marshall.visualsearch.Database.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import dev.marshall.visualsearch.Database.Model.Cart;
import dev.marshall.visualsearch.Database.Model.WishList;

@Database(entities = {Cart.class, WishList.class},version = 1)
public abstract class VSRoomDatabase extends RoomDatabase {

    public  abstract  CartDAO cartDAO();
    public  abstract WishListDAO favouriteDAO();

    private  static VSRoomDatabase instance;

    public static VSRoomDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = Room.databaseBuilder(context, VSRoomDatabase.class,"VisualSearchOrderDB")
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }
}
