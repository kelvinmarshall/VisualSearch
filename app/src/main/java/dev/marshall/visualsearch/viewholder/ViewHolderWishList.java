package dev.marshall.visualsearch.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Interface.ItemClickListener;
import dev.marshall.visualsearch.R;

public class ViewHolderWishList extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName,txtPrice;
    public ImageView imageView,img_cart;
    private ItemClickListener itemClickListener;

    public ConstraintLayout view_background;
    public ConstraintLayout view_foreground;

    public ViewHolderWishList(@NonNull View itemView) {
        super(itemView);
        txtMenuName = itemView.findViewById(R.id.product_name);
        txtPrice = itemView.findViewById(R.id.price);
        imageView = itemView.findViewById(R.id.image_product);
        img_cart = itemView.findViewById(R.id.add_cart);
        view_background= itemView.findViewById(R.id.view_background);
        view_foreground= itemView.findViewById(R.id.view_foreground);

        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
