package dev.marshall.visualsearch.Database.DataSource;


import java.util.List;

import dev.marshall.visualsearch.Database.Model.WishList;
import io.reactivex.Flowable;

public interface IWishListDataSource {


    Flowable<List<WishList>> getFavItems();

    int isFavourite(int itemId);

    void  insertFav(WishList... wishLists);

    void  delete(WishList wishList);
}
