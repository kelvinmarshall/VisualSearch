package dev.marshall.visualsearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Database.Model.Cart;
import dev.marshall.visualsearch.Database.Model.WishList;
import dev.marshall.visualsearch.R;
import dev.marshall.visualsearch.utils.Utils;
import dev.marshall.visualsearch.viewholder.ViewHolderWishList;

public class WishListAdapter extends RecyclerView.Adapter<ViewHolderWishList> {

    private Context context;
    private List<WishList> wishLists;

    public WishListAdapter(Context context, List<WishList> wishLists) {
        this.context = context;
        this.wishLists = wishLists;
    }

    @NonNull
    @Override
    public ViewHolderWishList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context)
                .inflate(R.layout.item_wishlist,parent,false);
        return new ViewHolderWishList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWishList holder, int position) {
        holder.txtMenuName.setText(wishLists.get(position).name);
        holder.txtPrice.setText("KES "+wishLists.get(position).price);

        Glide.with(context).load(wishLists.get(position).image)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart=new Cart();
                cart.amount=1;
                cart.id=wishLists.get(position).id;
                cart.image=wishLists.get(position).image;
                cart.name=wishLists.get(position).name;
                cart.price= Double.parseDouble(wishLists.get(position).price);

                Utils.cartRepository.insertToCart(cart);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishLists.size();
    }
    public void removeItem(int position)
    {
        wishLists.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(WishList item,int position)
    {
        wishLists.add(position,item);
        notifyItemInserted(position);
    }

    public WishList getItem(int position)
    {
        return  wishLists.get(position);
    }
}
