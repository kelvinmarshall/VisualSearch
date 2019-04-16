package dev.marshall.visualsearch;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import dev.marshall.visualsearch.Interface.RecyclerTouchHelperListener;
import dev.marshall.visualsearch.viewholder.ViewHolderWishList;


public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {


    private RecyclerTouchHelperListener listener;


    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener !=null)
            listener.onSwipe(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foregroundView=((ViewHolderWishList)viewHolder).view_foreground;
        getDefaultUIUtil().clearView(foregroundView);

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder !=null)
        {
            View foregroundview=((ViewHolderWishList)viewHolder).view_foreground;
            getDefaultUIUtil().onSelected(foregroundview);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundview=((ViewHolderWishList)viewHolder).view_foreground;
        getDefaultUIUtil().onDraw(c,recyclerView,foregroundview,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundview=((ViewHolderWishList)viewHolder).view_foreground;
        getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundview,dX,dY,actionState,isCurrentlyActive);
    }
}
