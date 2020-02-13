package com.example.livechatting.broadcast.liveVideoBroadcaster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.livechatting.CaA_BroadcastLive;
import com.example.livechatting.R;

import java.util.ArrayList;

import io.antmedia.android.broadcaster.utils.Resolution;

public class CameraResolutionsFragment extends DialogFragment {

    private static final String CAMERA_RESOLUTIONS = "CAMERA_RESOLUTIONS";
    private static final String SELECTED_SIZE_WIDTH = "SELECTED_SIZE_WIDTH";
    private static final String SELECTED_SIZE_HEIGHT = "SELECTED_SIZE_HEIGHT";
    private ListView mCameraResolutionsListView;
    private CameResolutionsAdapter mResolutionAdapter = new CameResolutionsAdapter();
    private ArrayList<Resolution> mCameraResolutions;
    private int mSelectedSizeWidth;
    private int mSelectedSizeHeight;

    public void setCameraResolutions(ArrayList<Resolution> cameraResolutions, Resolution selectedSize) {
        this.mCameraResolutions = cameraResolutions;
        this.mSelectedSizeWidth = selectedSize.width;
        this.mSelectedSizeHeight = selectedSize.height;
        mResolutionAdapter.setCameResolutions(mCameraResolutions);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CAMERA_RESOLUTIONS, mCameraResolutions);
        outState.putInt(SELECTED_SIZE_WIDTH, mSelectedSizeWidth);
        outState.putInt(SELECTED_SIZE_HEIGHT, mSelectedSizeHeight);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CAMERA_RESOLUTIONS)) {
                this.mCameraResolutions = (ArrayList<Resolution>) savedInstanceState.getSerializable(CAMERA_RESOLUTIONS);
            }

            if (savedInstanceState.containsKey(SELECTED_SIZE_WIDTH) &&
                    savedInstanceState.containsKey(SELECTED_SIZE_WIDTH)) {
                mSelectedSizeWidth = savedInstanceState.getInt(SELECTED_SIZE_WIDTH);
                mSelectedSizeHeight = savedInstanceState.getInt(SELECTED_SIZE_HEIGHT);
            }
            mResolutionAdapter.setCameResolutions(mCameraResolutions);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        View v = inflater.inflate(R.layout.caa_camera_resolutions, container, false);

        mCameraResolutionsListView = v.findViewById(R.id.camera_resolutions_listview);
        mCameraResolutionsListView.setAdapter(mResolutionAdapter);
        mCameraResolutionsListView.setOnItemClickListener((parent, view, position, id) -> {
            Resolution size = mResolutionAdapter.getItem(position);
            if (getActivity() instanceof CaA_BroadcastLive) {
                ((CaA_BroadcastLive) getActivity()).setResolution(size);
            }
        });
        mCameraResolutionsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return v;
    }

    class CameResolutionsAdapter extends BaseAdapter {

        ArrayList<Resolution> mCameraResolutions;

        public void setCameResolutions(ArrayList<Resolution> cameraResolutions) {
            this.mCameraResolutions = cameraResolutions;
        }

        @Override
        public int getCount() {
            return mCameraResolutions.size();
        }

        @Override
        public Resolution getItem(int i) {
            //reverse order. Highest resolution is at top
            return mCameraResolutions.get(getCount() - 1 - i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_single_choice, null);
                holder = new ViewHolder();
                holder.resolutionText = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //reverse order. Highest resolution is at top
            Resolution size = getItem(i);

            if (size.width == mSelectedSizeWidth && size.height == mSelectedSizeHeight) {
                mCameraResolutionsListView.setItemChecked(i, true);
            }
            String resolutionText = size.width + " x " + size.height;
            // adding auto resolution adding it to the first
            holder.resolutionText.setText(resolutionText);
            return convertView;
        }

        class ViewHolder {
            TextView resolutionText;
        }
    }
}