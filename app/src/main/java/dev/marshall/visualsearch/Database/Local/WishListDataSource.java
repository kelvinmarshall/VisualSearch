package dev.marshall.visualsearch.Database.Local;


import java.util.List;

import dev.marshall.visualsearch.Database.DataSource.IWishListDataSource;
import dev.marshall.visualsearch.Database.Model.WishList;
import io.reactivex.Flowable;

public class WishListDataSource implements IWishListDataSource {

    private WishListDAO wishListDAO;
    private  static WishListDataSource instance;

    public WishListDataSource(WishListDAO wishListDAO) {
        this.wishListDAO = wishListDAO;
    }

    public  static WishListDataSource getInstance(WishListDAO wishListDAO)
    {
        if (instance == null)
            instance=new WishListDataSource(wishListDAO);
        return instance;
    }

    @Override
    public Flowable<List<WishList>> getFavItems() {
        return wishListDAO.getFavItems();
    }

    @Override
    public int isFavourite(int itemId) {
        return wishListDAO.isFavourite(itemId);
    }

    @Override
    public void insertFav(WishList... wishLists) {
        wishListDAO.insertFav(wishLists);
    }

    @Override
    public void delete(WishList wishList) {
        wishListDAO.delete(wishList);
    }
}
