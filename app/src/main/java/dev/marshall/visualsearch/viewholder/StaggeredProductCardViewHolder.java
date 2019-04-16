package dev.marshall.visualsearch.viewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Interface.ItemClickListener;
import dev.marshall.visualsearch.R;

public class StaggeredProductCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView productImage,add_cart,add_wishlist;
    public TextView productTitle;
    public TextView productPrice;
    private ItemClickListener itemClickListener;

    public StaggeredProductCardViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.product_image);
        add_cart = itemView.findViewById(R.id.add_cart);
        add_wishlist = itemView.findViewById(R.id.add_wishlist);
        productTitle = itemView.findViewById(R.id.product_title);
        productPrice = itemView.findViewById(R.id.product_price);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
