package dev.marshall.visualsearch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Database.Model.Cart;
import dev.marshall.visualsearch.R;
import dev.marshall.visualsearch.utils.Utils;
import dev.marshall.visualsearch.viewholder.ViewHolderCart;

public class CartAdapter extends RecyclerView.Adapter<ViewHolderCart> {

    private Context context;
    private List<Cart> cartList;
    private RecyclerView recyclerView;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false);
        return new ViewHolderCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderCart holder, final int position) {

        holder.product_tittle.setText(cartList.get(position).name);
        holder.price.setText("KES "+String.valueOf(cartList.get(position).price));
        holder.quantity_no.setNumber(String.valueOf(cartList.get(position).amount));

        holder.quantity_no.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Cart cart=cartList.get(position);
                cart.amount=newValue;

                Utils.cartRepository.updateCart(cart);
            }
        });

        Glide.with(context).load(cartList.get(position).image).into(holder.product_image);

        holder.remove_cart_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=cartList.get(holder.getAdapterPosition()).name;
                final Cart deletedItem=cartList.get(holder.getAdapterPosition());
                final int deletedIndex=holder.getAdapterPosition();

                //delete item from adapter
                cartList.remove(deletedIndex);
                //delete from room database
                Utils.cartRepository.deleteCartItem(deletedItem);

                Snackbar snackbar = Snackbar.make(holder.itemView, name + "removed from favourites!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cartList.add(deletedIndex,deletedItem);
                        Utils.cartRepository.insertToCart(deletedItem);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position)
    {
        cartList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Cart item, int position)
    {
        cartList.add(position,item);
        notifyItemInserted(position);
    }

}
