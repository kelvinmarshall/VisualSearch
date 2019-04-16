package dev.marshall.visualsearch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Database.Model.Cart;
import dev.marshall.visualsearch.Database.Model.WishList;
import dev.marshall.visualsearch.R;
import dev.marshall.visualsearch.model.Model_product;
import dev.marshall.visualsearch.utils.Utils;
import dev.marshall.visualsearch.viewholder.ViewHolderSearch;

public class SearchResultAdapter extends RecyclerView.Adapter<ViewHolderSearch> {

    private Context context;
    private List<Model_product> result_list;

    public SearchResultAdapter(Context context, List<Model_product> model_products) {
        this.context = context;
        this.result_list = model_products;
    }

    @NonNull
    @Override
    public ViewHolderSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderSearch(LayoutInflater.from(context).inflate(R.layout.item_result,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSearch holder, int position) {
        holder.result_price.setText(result_list.get(position).getImUrl());
        holder.result_title.setText(result_list.get(position).getTitle());

        Glide.with(context).load(result_list.get(position)).into(holder.result_image);

        Cart cart=new Cart();
        cart.id= Integer.parseInt(result_list.get(position).getId());
        cart.price=result_list.get(position).getPrice();
        cart.name=result_list.get(position).getTitle();
        cart.amount=1;
        cart.image=result_list.get(position).getImUrl();

        if (Utils.wishListRepository.isFavourite(Integer.parseInt(result_list.get(position).getId()))==1){
            holder.add_wishlist.setImageResource(R.drawable.ic_favorite);
        }
                else{
            holder.add_wishlist.setImageResource(R.drawable.ic_favorite_border);
        }


        WishList wishList=new WishList();
        wishList.id= Integer.parseInt(result_list.get(position).getId());
        wishList.image=result_list.get(position).getImUrl();
        wishList.name=result_list.get(position).getTitle();
        wishList.price= String.valueOf(result_list.get(position).getPrice());

        String name=result_list.get(holder.getAdapterPosition()).getTitle();

        holder.add_cart.setOnClickListener(v -> {
            Utils.cartRepository.insertToCart(cart);
            final int addedIndex=holder.getAdapterPosition();


            Snackbar snackbar = Snackbar.make(holder.itemView, name + "added to cart", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v1 -> {
                result_list.remove(addedIndex);
                Utils.cartRepository.deleteCartItem(cart);
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        });

        holder.add_wishlist.setOnClickListener(v -> {
            Utils.wishListRepository.insertFav(wishList);
        });


    }

    @Override
    public int getItemCount() {
        return result_list.size();
    }
}
