package com.sz.zhihu.fragment.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.BrowseActivity;
import com.sz.zhihu.HomePageActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;

public class MineCardIsLoggedFragment extends Fragment implements CustomFragmentFunction {
    private User user;
    private View view;
    private Activity activity;
    private RoundedImageView portrait;
    private TextView userName;
    private TextView careNum;
    private TextView collectNum;
    private TextView historyNum;
    private RelativeLayout care;
    private RelativeLayout collect;
    private RelativeLayout browse;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.activity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_mine_card_logged, container, false);
            user = DBUtils.queryUserHistory();
            if(user != null){
                init();
            }
        }
        return view;
    }

    private void init() {
        String serverLocation = activity.getResources().getString(R.string.server_location);
        portrait = view.findViewById(R.id.mine_card_il_portrait);
        userName = view.findViewById(R.id.mine_card_il_username);
        careNum = view.findViewById(R.id.mine_card_il_care_num);
        collectNum = view.findViewById(R.id.mine_card_il_collect_num);
        historyNum = view.findViewById(R.id.mine_card_il_history_num);
        care = view.findViewById(R.id.mine_card_il_care_rl);
        collect = view.findViewById(R.id.mine_card_il_collect_rl);
        browse = view.findViewById(R.id.mine_card_il_browse_rl);
        String portraitFileName = user.getPortraitFileName();
        if(portraitFileName != null) {
            String url = serverLocation + "/res/User/" + user.getUserId() + "/" + portraitFileName;
            Glide.with(getContext()).load(url).into(portrait);
        }else {
            Glide.with(getContext()).load(R.drawable.default_portrait).into(portrait);
        }
        userName.setText(user.getUserName());
        int browseRecordCount = DBUtils.getBrowseRecordCount();
        careNum.setText(String.valueOf(user.getFollowSum()));
        collectNum.setText(String.valueOf(user.getCollectSum()));
        historyNum.setText(String.valueOf(browseRecordCount));
        view.setOnClickListener(v->{
            Intent intent = new Intent(activity,HomePageActivity.class);
            activity.startActivity(intent);
        });
        care.setOnClickListener(v->{});
        collect.setOnClickListener(v->{});
        browse.setOnClickListener(v->{
            Intent intent = new Intent(activity, BrowseActivity.class);
            activity.startActivity(intent);
        });
    }

    @Override
    public void refreshPage() {

    }
}
