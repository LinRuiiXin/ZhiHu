package com.sz.zhihu.fragment.mine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.UserUtils;

import org.w3c.dom.Text;

public class MineCardIsLoggedFragment extends Fragment implements CustomFragmentFunction {
    private User user;
    private View view;
    private Activity activity;
    private RoundedImageView portrait;
    private TextView userName;
    private LinearLayout personalPage;
    private TextView careNum;
    private TextView collectNum;
    private TextView historyNum;

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
            user = UserUtils.queryUserHistory();
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
        personalPage = view.findViewById(R.id.mine_card_il_personal_page);
        careNum = view.findViewById(R.id.mine_card_il_care_num);
        collectNum = view.findViewById(R.id.mine_card_il_collect_num);
        historyNum = view.findViewById(R.id.mine_card_il_history_num);
        String portraitFileName = user.getPortraitFileName();
        if(portraitFileName != null) {
            String url = serverLocation + "/res/User/" + user.getId() + "/" + portraitFileName;
            Glide.with(getContext()).load(url).into(portrait);
        }else {
            Glide.with(getContext()).load(R.drawable.default_portrait).into(portrait);
        }
        userName.setText(user.getUserName());
    }

    @Override
    public void refreshPage() {

    }
}
