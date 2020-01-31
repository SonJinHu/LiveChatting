package com.example.livechatting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.api.ApiService;
import com.example.livechatting.api.Charger;
import com.example.livechatting.api.ChargerList;
import com.example.livechatting.api.RetroClient;
import com.example.livechatting.data.Constant;
import com.example.livechatting.data.messages.DateObject;
import com.example.livechatting.data.messages.ListObject;
import com.example.livechatting.data.messages.MessagesObject;
import com.example.livechatting.function.ItemDecorationVertical;
import com.example.livechatting.function.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class D_ChatMessages extends AppCompatActivity {

    //private final String TAG = getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_messages);

        String roomNum = getIntent().getStringExtra("roomNum");
        String roomName = getIntent().getStringExtra("roomName");

        Toolbar toolbar = findViewById(R.id.d_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(roomName);

        loadMessages(roomNum);

        EditText inputMsg = findViewById(R.id.d_et_inputMsg);
        inputMsg.addTextChangedListener(new TextWatcher() {

            ImageButton sendMsg = findViewById(R.id.d_ib_sendMsg);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    sendMsg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
                    sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                    sendMsg.setOnClickListener(null);
                } else {
                    sendMsg.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    sendMsg.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                    sendMsg.setOnClickListener(v -> sendMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadMessages(String roomNum) {
        ApiService api = RetroClient.getApiService();
        Call<ChargerList> call = api.messages(roomNum);
        call.enqueue(new Callback<ChargerList>() {
            @Override
            public void onResponse(@NonNull Call<ChargerList> call, @NonNull Response<ChargerList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ArrayList<Charger.Messages> items = response.body().getMessages();
                        List<ListObject> list = generateMessages(items);

                        RecyclerView recycler = findViewById(R.id.d_recycler);
                        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recycler.setAdapter(new MessagesAdapter(list));
                        recycler.addItemDecoration(new ItemDecorationVertical(getApplicationContext(), 16));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChargerList> call, @NonNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    // 채팅창에서 날짜, 타인 메세지, 자기 메세지
    // 구분해서 다르게 보여줄 수 있도록 data 재정렬
    private List<ListObject> generateMessages(List<Charger.Messages> items) {

        LinkedHashMap<String, Set<Charger.Messages>> hashMap = new LinkedHashMap<>();
        List<ListObject> list = new ArrayList<>();

        for (Charger.Messages item : items) {
            String dateKey = null;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.KOREA);
                Date date = inputFormat.parse(item.getMsgTime());
                if (date != null)
                    dateKey = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Set<Charger.Messages> set;
            if (hashMap.containsKey(dateKey))
                set = hashMap.get(dateKey);
            else
                set = new LinkedHashSet<>();

            assert set != null;
            set.add(item);
            hashMap.put(dateKey, set);
        }

        for (String dateKey : hashMap.keySet()) {
            DateObject dateObject = new DateObject();
            dateObject.setDate(dateKey);
            list.add(dateObject);

            Set<Charger.Messages> set = hashMap.get(dateKey);
            assert set != null;
            for (Charger.Messages item : set) {
                MessagesObject msgObject = new MessagesObject();
                msgObject.setMessages(item);
                list.add(msgObject);
            }
        }
        return list;
    }

    private void sendMessage() {

    }

    class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ListObject> list;

        MessagesAdapter(List<ListObject> list) {
            this.list = list;
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                default:
                case ListObject.TYPE_CHAT_LEFT:
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_item_chat_left, parent, false);
                    return new LeftViewHolder(view);
                case ListObject.TYPE_CHAT_RIGHT:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_item_chat_right, parent, false);
                    return new RightViewHolder(view);
                case ListObject.TYPE_DATE:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_item_date, parent, false);
                    return new DateViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case ListObject.TYPE_CHAT_LEFT:
                    MessagesObject object = (MessagesObject) list.get(position);
                    LeftViewHolder leftViewHolder = (LeftViewHolder) holder;
                    leftViewHolder.bind(object.getMessages());
                    break;
                case ListObject.TYPE_CHAT_RIGHT:
                    object = (MessagesObject) list.get(position);
                    RightViewHolder rightViewHolder = (RightViewHolder) holder;
                    rightViewHolder.bind(object.getMessages());
                    break;
                case ListObject.TYPE_DATE:
                    DateObject dateObject = (DateObject) list.get(position);
                    DateViewHolder dateViewHolder = (DateViewHolder) holder;
                    dateViewHolder.bind(dateObject.getDate());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if (list != null)
                return list.size();
            return 0;
        }

        class LeftViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_profile;
            TextView tv_nick;
            TextView tv_leftMsg;
            TextView tv_leftTime;
            TextView tv_leftCount;

            LeftViewHolder(View itemView) {
                super(itemView);
                iv_profile = itemView.findViewById(R.id.d_item_iv_profile);
                tv_nick = itemView.findViewById(R.id.d_item_tv_nick);
                tv_leftMsg = itemView.findViewById(R.id.d_item_tv_leftMsg);
                tv_leftTime = itemView.findViewById(R.id.d_item_tv_leftTime);
                tv_leftCount = itemView.findViewById(R.id.d_item_tv_leftCount);
            }

            void bind(Charger.Messages item) {
                if (item.getUserImg() == null) {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.profile_default)
                            .apply(RequestOptions.circleCropTransform())
                            .into(iv_profile);
                } else {
                    Glide.with(getApplicationContext())
                            .load(Constant.URL + item.getUserImg())
                            .placeholder(R.drawable.profile_default)
                            .error(R.drawable.profile_default)
                            .apply(RequestOptions.circleCropTransform())
                            .into(iv_profile);
                }
                tv_nick.setText(item.getUserNick());
                tv_leftMsg.setText(item.getMessage());
                tv_leftTime.setText(Time.convertMsgTime(item.getMsgTime()));
            }
        }

        class RightViewHolder extends RecyclerView.ViewHolder {

            TextView tv_rightMsg;
            TextView tv_rightTime;
            TextView tv_rightCount;

            RightViewHolder(View itemView) {
                super(itemView);
                tv_rightMsg = itemView.findViewById(R.id.d_item_tv_rightMsg);
                tv_rightTime = itemView.findViewById(R.id.d_item_tv_rightTime);
                tv_rightCount = itemView.findViewById(R.id.d_item_tv_rightCount);
            }

            void bind(Charger.Messages item) {
                tv_rightMsg.setText(item.getMessage());
                tv_rightTime.setText(Time.convertMsgTime(item.getMsgTime()));
            }
        }

        class DateViewHolder extends RecyclerView.ViewHolder {

            TextView tv_date;

            DateViewHolder(View itemView) {
                super(itemView);
                tv_date = itemView.findViewById(R.id.d_item_tv_date);
            }

            void bind(String date) {
                tv_date.setText(date);
            }
        }
    }
}
