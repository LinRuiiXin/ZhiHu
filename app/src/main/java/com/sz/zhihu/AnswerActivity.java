package com.sz.zhihu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.sz.zhihu.adapter.AnswerFragmentAdapter;
import com.sz.zhihu.fragment.answer.AnswerFragment;
import com.sz.zhihu.po.Answer;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.UserUtils;
import com.sz.zhihu.vo.AnswerVo;
import com.sz.zhihu.vo.RecommendViewBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sz.zhihu.R.id.answer_tool_bar;

public class AnswerActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        RecommendViewBean viewBean = (RecommendViewBean) intent.getSerializableExtra("viewBean");
        Toolbar toolbar = findViewById(answer_tool_bar);
        toolbar.setTitle(viewBean.getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v-> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        viewPager = findViewById(R.id.answer_view_pager);
        initViewPager();
    }
    public void initViewPager(){
        fragments = new ArrayList<>();
        for(int i = 0;i < 3;i++){
            AnswerFragment answerFragment = new AnswerFragment(new AnswerVo(UserUtils.queryUserHistory(), new Answer(23L, 10L, 31L, "<font size=\"2\">四川是官话区里被吐槽不说普通话的典型。知乎上一堆挑剔四川人说方言的热门问题，我等四川人早被搞得不胜其烦。题主是看不到？搞完上海广东，想挑拨四川？上海、广东富，所以他们说方言是高人一等、搞duli。四川经济相对落后，说方言就是素质低、没礼貌。总之理都被某些人占了呗。<br><br>问题在于，挑剔的人大多并不是以普通话普及程度为准绳，而是以他们是否能听懂为标准。不管山东话、河南话、东北口音，只要他们听得懂，在他们眼里就是普通话。比如很多时候我接到驻地是北京、面向全国的客服热线，一口一个京片子。北方某些地区，旅游业服务业泛滥成灾的儿化音、地方词汇、地方口音。这些真的不是普通话，但他们并没被斤斤计较。<br><br>没错，我们四川也是官话区，所以我们“以为”只要说慢点你们就能如其它官话一样听懂。当年老设计师在台上讲话，你敢挑剔听不懂？四川自古地理封闭，和西南以外的地方交流少。同时西南官话2亿多人使用，交流几乎无障碍。99%（虚数）的时间我们都在和语言相通者交流，没形成那么强的普通话意识。我不止一次看到东部某省网友自我标榜素质高，普通话普及，并以此挑剔四川。拜托，贵省十里不同音，本省人聚在一起，不说普通话没法交流，你们只是对普通话的需求迫切罢了。<br><br>四川人说普通话意识不够强烈，其原因不仅有普通话能力上的、习惯上的，还有心理上的障碍，但总体原因是与其它地区交流不够。我们可以欣然接受批评，但这个批评得不含双重标准。不能以普通话普及程度暗示我们四川人素质低。我们低，那某些省市也没高到哪里去。<br><br>四川人从来没有想过要靠方言隔离谁、区分谁。也没有想为方言争取什么地位。如果未来普通话成了本地主流，我个人也不拒绝。我们只想你们理解、尊重我们的现状，懂得习惯养成需要不断相互加深交往的时间过程。你说普通话，那些憋红了脸反复与你说四川话的大妈大叔，不是抗拒交流。因为他本可以懒得搭理你，交流不了就不交流。换位思考一下也许你就不觉得是不礼貌或排挤了。如果你理解四川的客观条件，那么四川人很乐意承认自己的不足，如果居高临下胡乱批评一通，我们拒不接受。<br><br>就我而言，我非常能理解广东、江浙沪人民那些合理范围的诉求。如果确实令我介意，我也会换位思考一下，试图理解一下。另外，就我观察四川人绝对不是批评广东、江浙沪说方言/语言的主力军。</font><img src=\"http://192.168.137.1:8080/ZhiHu/res/Image/158693060172231.jpg\" alt=\"picvision\" style=\"margin-top:10px;max-width:100%;\"><br>&nbsp;<video src=\"http://192.168.137.1:8080/ZhiHu/res/Video/158693061022931.mp4\" controls=\"controls\" initial-time=\"0.01\" height=\"300\" style=\"margin-top:10px;max-width:100%;\"></video>&nbsp;<br><br>", 1, 2301L, 12321L, new Date())));
            fragments.add(answerFragment);
        }
        viewPager.setAdapter(new AnswerFragmentAdapter(getSupportFragmentManager(), fragments));
    }

}
