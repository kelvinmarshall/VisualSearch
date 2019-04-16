package dev.marshall.visualsearch.Database.Local;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import dev.marshall.visualsearch.Database.Model.WishList;
import io.reactivex.Flowable;

@Dao
public interface WishListDAO {

    @Query("SELECT * FROM Wishlist")
    Flowable<List<WishList>> getFavItems();

    @Query("SELECT EXISTS (SELECT 1 FROM Wishlist  WHERE id=:itemId)")
    int isFavourite(int itemId);

    @Insert
    void  insertFav(WishList... wishLists);

    @Delete
    void  delete(WishList wishList);

}
