package com.example.livechatting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livechatting.api.ApiService;
import com.example.livechatting.api.Charger;
import com.example.livechatting.api.ChargerList;
import com.example.livechatting.api.RetroClient;
import com.example.livechatting.data.UserInfo;
import com.example.livechatting.function.Time;
import com.example.livechatting.function.ItemDecorationVertical;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cb_ChatRoomsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cb_rooms, container, false);

        ApiService api = RetroClient.getApiService();
        Call<ChargerList> call = api.rooms(UserInfo.num);
        call.enqueue(new Callback<ChargerList>() {
            @Override
            public void onResponse(@NonNull Call<ChargerList> call, @NonNull Response<ChargerList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ArrayList<Charger.Rooms> list = response.body().getRooms();

                        RecyclerView recycler = root.findViewById(R.id.cb_recycler);
                        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recycler.setAdapter(new RoomsAdapter(getActivity(), list));
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

    class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

        private Activity activity;
        private ArrayList<Charger.Rooms> items;

        RoomsAdapter(Activity activity, ArrayList<Charger.Rooms> items) {
            this.activity = activity;
            this.items = items;
        }

        @NonNull
        @Override
        public RoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cb_item_rooms, parent, false);
            return new RoomsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomsAdapter.ViewHolder holder, int position) {
            holder.tv_roomName.setText(items.get(position).getRoomName());
            // 방 인원이 2명일 경우, 숫자를 표시하지 않음
            if (items.get(position).getUserCount().equals("2")) {
                holder.iv_roomImage.setImageResource(R.drawable.ic_person_black_24dp);
                holder.iv_roomImage.setImageTintList(ContextCompat.getColorStateList(activity, android.R.color.white));
                holder.tv_userCount.setVisibility(View.GONE);
            } else {
                holder.iv_roomImage.setImageResource(R.drawable.ic_supervisor_account_black_24dp);
                holder.iv_roomImage.setImageTintList(ContextCompat.getColorStateList(activity, android.R.color.white));
                holder.tv_userCount.setVisibility(View.VISIBLE);
                holder.tv_userCount.setText(items.get(position).getUserCount());
            }

            holder.tv_lastMsg.setText(items.get(position).getLastMsg());
            holder.tv_lastMsgTime.setText(Time.convertLastMsgTime(items.get(position).getLastMsgTime()));
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_roomImage;
            TextView tv_roomName;
            TextView tv_userCount;
            TextView tv_lastMsg;
            TextView tv_lastMsgTime;

            ViewHolder(View itemView) {
                super(itemView);
                iv_roomImage = itemView.findViewById(R.id.cb_item_iv_roomImage);
                tv_roomName = itemView.findViewById(R.id.cb_item_tv_roomName);
                tv_userCount = itemView.findViewById(R.id.cb_item_tv_userCount);
                tv_lastMsg = itemView.findViewById(R.id.cb_item_tv_lastMsg);
                tv_lastMsgTime = itemView.findViewById(R.id.cb_item_tv_lastMsgTime);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(activity, D_ChatMessages.class);
                    intent.putExtra("roomNum", items.get(position).getRoomNum());
                    intent.putExtra("roomName", items.get(position).getRoomName());
                    intent.putExtra("userCount", items.get(position).getUserCount());
                    startActivity(intent);
                });
            }
        }
    }
}