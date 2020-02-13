package com.example.livechatting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.api.ApiService;
import com.example.livechatting.api.Charger;
import com.example.livechatting.api.ChargerList;
import com.example.livechatting.api.RetroClient;
import com.example.livechatting.data.Constants;
import com.example.livechatting.data.UserInfo;
import com.example.livechatting.function.ItemDecorationHorizontal;
import com.example.livechatting.function.ItemDecorationVertical;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ca_FriendsFragment extends Fragment {

    //private final String TAG = getClass().getName();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ca_friends, container, false);
        setHasOptionsMenu(true);

        ApiService api = RetroClient.getApiService();
        Call<ChargerList> call = api.broadcast(UserInfo.num);
        call.enqueue(new Callback<ChargerList>() {
            @Override
            public void onResponse(@NonNull Call<ChargerList> call, @NonNull Response<ChargerList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ArrayList<Charger.Broadcast> list = response.body().getBroadcast();
                        RecyclerView recycler = root.findViewById(R.id.cc_recycler);
                        // 표시할 방송 목록이 없으면 setVisibility(View.GONE)
                        if (list.size() != 0) {
                            recycler.setVisibility(View.VISIBLE);
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            recycler.setAdapter(new BroadcastAdapter(getActivity(), list));
                            recycler.addItemDecoration(new ItemDecorationHorizontal(getActivity(), 16));
                            return;
                        }
                        recycler.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChargerList> call, @NonNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
            }
        });

        call = api.friends(UserInfo.num);
        call.enqueue(new Callback<ChargerList>() {
            @Override
            public void onResponse(@NonNull Call<ChargerList> call, @NonNull Response<ChargerList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ArrayList<Charger.Friends> list = response.body().getFriends();

                        // 내 프로필 정보 추가
                        Charger charger = new Charger();
                        Charger.Friends myProfile = charger.new Friends();
                        myProfile.setNum(UserInfo.num);
                        myProfile.setNick(UserInfo.nick);
                        myProfile.setImg(UserInfo.img);
                        list.add(0, myProfile);

                        //TODO: 내 프로필과 친구 프로필 분류해서 표시
                        //TODO: 내 프로필 선택하면 내 정보 수정, 친구 프로필 선택하면 친구 정보와 영상통화 요청
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.ca_friends_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friend:
                //TODO: 친구 추가
                return true;
            case R.id.action_broadcast:
                Intent intent = new Intent(getActivity(), CaA_BroadcastLive.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class BroadcastAdapter extends RecyclerView.Adapter<BroadcastAdapter.ViewHolder> {

        private Activity activity;
        private ArrayList<Charger.Broadcast> items;

        BroadcastAdapter(Activity activity, ArrayList<Charger.Broadcast> items) {
            this.activity = activity;
            this.items = items;
        }

        @NonNull
        @Override
        public BroadcastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_item_broadcaster, parent, false);
            return new BroadcastAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(activity)
                    .load(R.drawable.rainbow)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.iv_gradient);
            if (items.get(position).getImg() == null) {
                Glide.with(activity)
                        .load(R.drawable.profile_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.iv_profile);
            } else {
                Glide.with(activity)
                        .load(Constants.URL + items.get(position).getImg())
                        .placeholder(R.drawable.profile_default)
                        .error(R.drawable.profile_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.iv_profile);
            }
            holder.tv_nick.setText(items.get(position).getNick());
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_gradient;
            ImageView iv_profile;
            TextView tv_nick;

            ViewHolder(View itemView) {
                super(itemView);
                iv_gradient = itemView.findViewById(R.id.cc_item_iv_gradient);
                iv_profile = itemView.findViewById(R.id.cc_item_iv_profile);
                tv_nick = itemView.findViewById(R.id.cc_item_tv_nick);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(activity, CaB_LiveVideoPlayerActivity.class);
                    intent.putExtra("nick", items.get(position).getNick());
                    intent.putExtra("img", items.get(position).getImg());
                    intent.putExtra("rtmp", items.get(position).getRtmp());
                    startActivity(intent);
                });
            }
        }
    }

    private class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

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
                        .load(Constants.URL + items.get(position).getImg())
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

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(activity, Da_MakeVideoCall.class);
                    intent.putExtra("num", items.get(position).getNum());
                    intent.putExtra("nick", items.get(position).getNick());
                    intent.putExtra("img", items.get(position).getImg());
                    startActivity(intent);
                });
            }
        }
    }
}