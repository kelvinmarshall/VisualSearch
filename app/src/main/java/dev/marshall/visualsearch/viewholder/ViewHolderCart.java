package dev.marshall.visualsearch.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Interface.ItemClickListener;
import dev.marshall.visualsearch.R;

public class ViewHolderCart extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView product_tittle,price;
    public ImageView remove_cart_item,product_image;
    public ElegantNumberButton quantity_no;
    private ItemClickListener itemClickListener;
    public ConstraintLayout bottomsheet_cart;


    public ViewHolderCart(@NonNull View itemView) {
        super(itemView);
        product_tittle = itemView.findViewById(R.id.product_title);
        price = itemView.findViewById(R.id.price);
        product_image = itemView.findViewById(R.id.img_product);
        quantity_no = itemView.findViewById(R.id.quantity_no);
        remove_cart_item = itemView.findViewById(R.id.remove_cart_item);
        bottomsheet_cart=itemView.findViewById(R.id.bottomsheet_cart);


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
