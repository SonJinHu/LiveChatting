package com.example.livechatting;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.api.ApiService;
import com.example.livechatting.api.Charger;
import com.example.livechatting.api.ChargerList;
import com.example.livechatting.api.RetroClient;
import com.example.livechatting.data.Constant;
import com.example.livechatting.data.UserInfo;
import com.example.livechatting.function.ItemDecorationVertical;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ca_FriendsFragment extends Fragment {

    private final String TAG = getClass().getName();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ca_friends, container, false);

        ApiService api = RetroClient.getApiService();
        Call<ChargerList> call = api.friends(UserInfo.num);
        call.enqueue(new Callback<ChargerList>() {
            @Override
            public void onResponse(@NonNull Call<ChargerList> call, @NonNull Response<ChargerList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ArrayList<Charger.Friends> list = response.body().getFriends();

                        RecyclerView recycler = root.findViewById(R.id.ca_recycler);
                        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recycler.setAdapter(new FriendsAdapter(getActivity(), list));
                        recycler.addItemDecoration(new ItemDecorationVertical(getActivity(), 16));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChargerList> call, @NonNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
            }
        });

        return root;
    }

    class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

        private Activity activity;
        private ArrayList<Charger.Friends> items;

        FriendsAdapter(Activity activity, ArrayList<Charger.Friends> items) {
            this.activity = activity;
            this.items = items;
        }

        @NonNull
        @Override
        public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_item_friends, parent, false);
            return new FriendsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
            if (items.get(position).getImg() == null) {
                Glide.with(activity)
                        .load(R.drawable.profile_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.iv);
            } else {
                Glide.with(activity)
                        .load(Constant.URL + items.get(position).getImg())
                        .placeholder(R.drawable.profile_default)
                        .error(R.drawable.profile_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.iv);
            }
            holder.tv.setText(items.get(position).getNick());
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;

            ViewHolder(View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.ca_item_iv);
                tv = itemView.findViewById(R.id.ca_item_tv);
            }
        }
    }
}