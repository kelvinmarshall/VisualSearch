package dev.marshall.visualsearch;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Database.Model.WishList;
import dev.marshall.visualsearch.Interface.RecyclerTouchHelperListener;
import dev.marshall.visualsearch.adapter.WishListAdapter;
import dev.marshall.visualsearch.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class WishListFragment extends Fragment implements RecyclerTouchHelperListener{

    private RecyclerView recycler_wishlist;
    private List<WishList> wishListList=new ArrayList<>();
    private WishListAdapter wishListAdapter;
    private FrameLayout wishlist;
    RecyclerTouchHelperListener recyclerTouchHelperListener;

    private ContentLoadingProgressBar wait;
    private CompositeDisposable compositeDisposable;


    public WishListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compositeDisposable=new CompositeDisposable();

        wishlist=view.findViewById(R.id.wishlist);

        recycler_wishlist=view.findViewById(R.id.recycler_WishList);
        wait=view.findViewById(R.id.wait);

        recycler_wishlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_wishlist.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback=new
                RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_wishlist);

        loadwishlistItem();
    }

    private void loadwishlistItem() {
        compositeDisposable.add(Utils.wishListRepository.getFavItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<WishList>>() {
                    @Override
                    public void accept(List<WishList> wishLists) throws Exception {
                        displaywishlist(wishLists);
                    }
                })
        );
    }

    private void displaywishlist(List<WishList> wishLists) {
        wishListList=wishLists;
        wishListAdapter=new WishListAdapter(getActivity(),wishLists);
        recycler_wishlist.setAdapter(wishListAdapter);

    }
    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadwishlistItem();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        String name=wishListList.get(viewHolder.getAdapterPosition()).name;
        final WishList deletedItem=wishListList.get(viewHolder.getAdapterPosition());
        final int deletedIndex=viewHolder.getAdapterPosition();

        //delete item from adapter
        wishListAdapter.removeItem(deletedIndex);
        //delete from room database
        Utils.wishListRepository.delete(deletedItem);

        Snackbar snackbar = Snackbar.make(wishlist, name + "removed from favourites!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishListAdapter.restoreItem(deletedItem, deletedIndex);
                Utils.wishListRepository.insertFav(deletedItem);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}