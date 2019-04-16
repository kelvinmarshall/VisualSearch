package dev.marshall.visualsearch.Database.DataSource;


import java.util.List;

import dev.marshall.visualsearch.Database.Model.WishList;
import io.reactivex.Flowable;

public class WishListRepository implements IWishListDataSource {

    private IWishListDataSource iWishListDataSource;

    public WishListRepository(IWishListDataSource iWishListDataSource) {
        this.iWishListDataSource = iWishListDataSource;
    }

    private  static WishListRepository instance;

    public   static WishListRepository getInstance(IWishListDataSource iWishListDataSource)
    {
        if (instance ==null)
            instance=new WishListRepository(iWishListDataSource);
        return instance;

    }

    @Override
    public Flowable<List<WishList>> getFavItems() {
        return iWishListDataSource.getFavItems();
    }

    @Override
    public int isFavourite(int itemId) {
        return iWishListDataSource.isFavourite(itemId);
    }

    @Override
    public void insertFav(WishList... wishLists) {
        iWishListDataSource.insertFav(wishLists);
    }

    @Override
    public void delete(WishList wishList) {
        iWishListDataSource.delete(wishList);
    }
}
