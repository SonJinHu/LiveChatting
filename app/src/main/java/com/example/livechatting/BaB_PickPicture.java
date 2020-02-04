package com.example.livechatting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.data.Constants;
import com.example.livechatting.function.ItemDecorationHorizontal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class BaB_PickPicture extends AppCompatActivity {

    ImageView iv_selected;
    String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bab_pick_picture);

        String timeStamp = getIntent().getStringExtra("timeStamp");

        Toolbar toolbar = findViewById(R.id.c_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("프로필 사진 선택");

        iv_selected = findViewById(R.id.bab_iv_selected);
        Glide.with(getApplicationContext())
                .load(R.drawable.profile_default)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_selected);

        File file = new File(Constants.DIRECTORY_PATH);
        File[] files = file.listFiles(pathname -> {
            assert timeStamp != null;
            return pathname.getName().startsWith(timeStamp);
        });
        ArrayList<File> fileList = new ArrayList<>();
        if (files != null)
            Collections.addAll(fileList, files);

        RecyclerView recycler = findViewById(R.id.bab_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new PictureListAdapter(fileList));
        recycler.addItemDecoration(new ItemDecorationHorizontal(this, 16));
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.bab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_complete) {
            Intent intent = new Intent();
            intent.putExtra("path", path);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {

        private ArrayList<File> items;
        private int lastSelectedPosition;

        PictureListAdapter(ArrayList<File> items) {
            this.items = items;
            lastSelectedPosition = 0;

            path = items.get(lastSelectedPosition).getAbsolutePath();
            Glide.with(getApplicationContext())
                    .load(BitmapFactory.decodeFile(path))
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv_selected);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bab_item_picture, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            File file = items.get(position);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Glide.with(getApplicationContext())
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.iv_profile);
            }

            if (position == lastSelectedPosition) {
                holder.v_checked.setVisibility(View.VISIBLE);
            } else {
                holder.v_checked.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_profile;
            View v_checked;

            ViewHolder(View itemView) {
                super(itemView);
                iv_profile = itemView.findViewById(R.id.bab_item_iv);
                v_checked = itemView.findViewById(R.id.bab_item_view_selected);

                iv_profile.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    lastSelectedPosition = position;
                    notifyDataSetChanged();

                    path = items.get(position).getAbsolutePath();
                    Glide.with(getApplicationContext())
                            .load(BitmapFactory.decodeFile(path))
                            .apply(RequestOptions.circleCropTransform())
                            .into(iv_selected);
                });
            }
        }
    }
}