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
import dev.marshall.visualsearch.R;
import dev.marshall.visualsearch.viewholder.ViewHolderCart;

public class CartSheetAdapter extends RecyclerView.Adapter<ViewHolderCart> {

    private Context context;
    private List<Cart> cartList;

    public CartSheetAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_cart_sheet,parent,false);
        return new ViewHolderCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderCart holder, final int position) {

        Glide.with(context).load(cartList.get(position).image).into(holder.product_image);

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


}
