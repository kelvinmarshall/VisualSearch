package dev.marshall.visualsearch.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.R;

public class ViewHolderSearch extends RecyclerView.ViewHolder {

    public TextView result_title,result_price;
    public ImageView add_wishlist,add_cart,result_image;
    public ViewHolderSearch(@NonNull View itemView) {
        super(itemView);

        result_image=itemView.findViewById(R.id.image_result);
        result_title=itemView.findViewById(R.id.name);
        result_price=itemView.findViewById(R.id.price);
        add_wishlist=itemView.findViewById(R.id.add_wishlist);
        add_cart=itemView.findViewById(R.id.add_cart);


    }
}
