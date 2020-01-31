package com.example.livechatting.function;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecorationHorizontal extends RecyclerView.ItemDecoration {

    private Context context;
    private int margin;

    public ItemDecorationHorizontal(Context context, @IntRange(from = 0) int margin) {
        this.context = context;
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) != 0)
            outRect.left = pixelToDp(margin);
    }

    private int pixelToDp(int pixel) {
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (pixel * d);
    }
}
