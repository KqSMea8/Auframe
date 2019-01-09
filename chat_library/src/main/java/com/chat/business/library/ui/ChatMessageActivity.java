package com.chat.business.library.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.chat.business.library.R;
import com.chat.business.library.model.ChatVoiceEMMessage;
import com.chat.business.library.model.PreviewBean;
import com.chat.business.library.ui.fragment.EmotionMainFragment;
import com.chat.business.library.util.SendUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.maiguoer.component.http.app.BaseSwipeBackActivity;
import com.maiguoer.component.http.utils.BroadCastReceiverConstant;
import com.maiguoer.component.http.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 单聊
 * Create by www.lijin@foxmail.com on 2019/1/3 0003.
 * <br/>
 */
public class ChatMessageActivity extends BaseSwipeBackActivity implements EmotionMainFragment.FragmentListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout vSwipeRefresh;
    /**
     * message的父级 用于获取上级ID
     */
    private LinearLayout vFather;
    /**
     * RecyclerView
     */
    private RecyclerView vCRecyclerView;

    // 对方用户昵称 username 对方用户头像 avatar  对方用户实名认证状态 authStatus 对方用户企业认证状态 businessAuthStatus
    private String mOtherUid, mOtherUsername, mOtherUserAvatar,
            mOtherAuthStatus, mOtherBusinessAuthStatus,
            mOtherNamgeCardBgImage;
    //对方用户VIP等级 vipLevel
    private String mOtherVipLevel;
    //传递进来的环信ID  当前人的环信ID
    private String mHXid;
    //全局上下文
    private Context mContext;
    //底部框 包含输入区域和表情区域
    private EmotionMainFragment mEmotionMainFragment;
    //用于注册关闭的Filter
    private IntentFilter mFilter;
    //用于全局接收的Intent
    private Intent mIn;
    //展示数据的Adapter
    private ChatMessageAdapter mAdapter;
    //语音播放器
    MediaPlayer player = new MediaPlayer();
    //存储播放大图的零时集合
    ArrayList<PreviewBean> mDList;
    //存储大图路径的几个
    ArrayList<String> mPathList;
    //大图的实体
    PreviewBean previewBean;
    //用于保存需要传递到大图的下标
    int mPicPosition = 0;
    //RecyclerView 的 LinearLayoutManager
    LinearLayoutManager linearLayoutManager;
    //用于保存未读语音的数组
    List<ChatVoiceEMMessage> mVoiceBody = new ArrayList<>();
    //用于保存未读语音的动画View
    List<View> mVoiceView = new ArrayList<>();
    //自增的播放下标
    int mVoicePosition = 0;
    //用于保存未读语音的实体
    ChatVoiceEMMessage chatVoiceEMMessage;
    //用于全局保存语音是否在播放的状态
    boolean mIsVoiceStart = false;
    //保存当前播放到的view
    View mFastView;
    //有多少条未读
    private int mMarkingContent;
    List<EMMessage> messages;
    //设置一页展示的数据
    int PageSize = 20;
    //用于判断定位的方式
    int mPageSize = 10;
    String SYSTEM_REASON = "reason";
    String SYSTEM_HOME_KEY = "homekey";
    String SYSTEM_HOME_KEY_LONG = "recentapps";
    //TAG
    private final static String TAG = "ChatMessageActivity_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_sing_layout);
        //赋值上下文
        mContext = this;
        //组件赋值
        initView();
        //数据查询与赋值
        initData();
        //注册收到消息的广播
        mFilter = new IntentFilter(BroadCastReceiverConstant.BROAD_MESSAGERECEIVED);
        mContext.registerReceiver(mReceiver, mFilter);
        initRefresh(mHXid, false, false);
    }

    private void initView() {
        mHXid = "9847571";
        vCRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        vFather = (LinearLayout) findViewById(R.id.chat_ry_father);
        vSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.chat_message_chat_refresh);
        //初始化刷新
        //为SwipeRefreshLayout设置监听事件
        vSwipeRefresh.setOnRefreshListener(this);
        //为SwipeRefreshLayout设置刷新时的颜色变化，最多可以设置4种
        vSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * 总的广播接受类
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取收到的消息
            if (intent.getAction().equals(BroadCastReceiverConstant.BROAD_MESSAGERECEIVED)) {
                //获取是否定位刷新
                final boolean isSendRefrsh = intent.getBooleanExtra(Constant.isSendRefrsh, false);
                ChatMessageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSendRefrsh) {
                            initRefresh(mHXid, false, true);
                        } else {
                            initRefresh(mHXid, false, false);
                        }
                    }
                });
            }
        }
    };


    /**
     * 刷新数据
     *
     * @param mHxId         环信ID
     * @param isRefresh     是否为下拉刷新
     * @param isSendRefresh 是否为发送刷新
     */
    private void initRefresh(String mHxId, boolean isRefresh, boolean isSendRefresh) {
        //内存地址是否为null 在本地地址上进行clear
        if (messages != null) {
            messages.clear();
        } else {
            messages = new ArrayList<>();
        }
        if (isRefresh) {
            PageSize = PageSize + PageSize;
        }
        //获取当前用户的会话数据
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mHxId);
        if (conversation != null) {
            //添加到message
            messages.addAll(conversation.getAllMessages());
            int msgCount = messages != null ? messages.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < PageSize) {
                String msgId = null;
                if (messages != null && messages.size() > 0) {
                    msgId = messages.get(0).getMsgId();
                }
                //load到db
                conversation.loadMoreMsgFromDB(msgId, PageSize - msgCount);
            }
            //去加载 或者 刷新数据
            if (messages.size() > 0 && messages != null) {
                messages.clear();
                messages.addAll(conversation.getAllMessages());
                if (mAdapter != null) {
                    //判断RecyclerView 是否在底部
                    if (!vCRecyclerView.canScrollVertically(1)) {
                        //当数据超过一页的时候 将默认加载到底部的方式变更 经过测试  最短为8条 延长到10条
                        if (messages.size() > mPageSize) {
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            linearLayoutManager.setStackFromEnd(true);
                            vCRecyclerView.setLayoutManager(linearLayoutManager);
                            ((DefaultItemAnimator) vCRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                        } else {
                            //使用Position定位的方式加载到底部 规避商品咨询无法在顶部的问题
                            vCRecyclerView.smoothScrollToPosition(messages.size());
                            ((DefaultItemAnimator) vCRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                        }
                    } else {
                        if (isSendRefresh) {
                            //要定位到最后一条数据
                            if (!isRefresh) {
                                //当数据超过一页的时候 将默认加载到底部的方式变更 经过测试  最短为8条 延长到10条
                                if (messages.size() > mPageSize) {
                                    linearLayoutManager = new LinearLayoutManager(mContext);
                                    linearLayoutManager.setStackFromEnd(true);
                                    vCRecyclerView.setLayoutManager(linearLayoutManager);
                                    ((DefaultItemAnimator) vCRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                                } else {
                                    //使用Position定位的方式加载到底部 规避商品咨询无法在顶部的问题
                                    vCRecyclerView.smoothScrollToPosition(messages.size());
                                    ((DefaultItemAnimator) vCRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                                }
                            }
                        }
                    }
                    //刷新数据
                    mAdapter.notifyDataSetChanged();
                    vSwipeRefresh.setRefreshing(false);
                } else {
                    mAdapter = new ChatMessageAdapter(mContext, messages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            showType = (String) v.getTag();
//                            mPay.startPay();
                        }
                    });
//                    mAdapter.setOnItemClickListener(this);
                    ChatMessageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //去加载数据
                            if (messages.size() > mPageSize) {
                                linearLayoutManager = new LinearLayoutManager(mContext);
                                linearLayoutManager.setStackFromEnd(true);
                                vCRecyclerView.setLayoutManager(linearLayoutManager);
                                ((DefaultItemAnimator) vCRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                                vCRecyclerView.setAdapter(mAdapter);
                            } else {
                                linearLayoutManager = new LinearLayoutManager(mContext);
                                vCRecyclerView.setLayoutManager(linearLayoutManager);
                                ((DefaultItemAnimator) vCRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                                vCRecyclerView.setAdapter(mAdapter);
                                vCRecyclerView.smoothScrollToPosition(messages.size());
                            }
                        }
                    });
                }
            } else {
                //在null的情况下去加载数据
                mAdapter = new ChatMessageAdapter(mContext, messages, null);
//                mAdapter.setOnItemClickListener(this);
                ChatMessageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (messages.size() > mPageSize) {
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            linearLayoutManager.setStackFromEnd(true);
                            vCRecyclerView.setLayoutManager(linearLayoutManager);
                            vCRecyclerView.setAdapter(mAdapter);
                        } else {
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            vCRecyclerView.setLayoutManager(linearLayoutManager);
                            vCRecyclerView.setAdapter(mAdapter);
                            vCRecyclerView.smoothScrollToPosition(messages.size());
                        }
                    }
                });
            }
        } else {
            if (vSwipeRefresh != null) {
                vSwipeRefresh.setRefreshing(false);
            }
            LogUtils.d(TAG, "data null");
        }
    }

    @Override
    public void onRefresh() {
        initRefresh(mHXid, true, false);
    }

    /**
     * 初始化布局数据
     */
    private void initData() {
        //初始化表情列表
        initEmotionMainFragment();
    }

    /**
     * 初始化表情面板
     */
    public void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT, true);
        //隐藏控件
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN, false);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_HXID, mHXid);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHRTUID, mOtherUid);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHERUSERNAME, mOtherUsername);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHERUSERAVATAR, mOtherUserAvatar);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHERAURHSTATUS, mOtherAuthStatus);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHERABUSINESSAU, mOtherBusinessAuthStatus);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHERNAMGECARSBGIMAGE, mOtherNamgeCardBgImage);
        bundle.putString(Constant.MEG_INTNT_CHATMESSAGE_OTHERVIPLEVEL, mOtherVipLevel);
        //替换fragment
        //创建修改实例
        mEmotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class, bundle);
        mEmotionMainFragment.bindToContentView(vFather);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chat_fr, mEmotionMainFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }

    /**
     * 拦截返回键
     */
    @Override
    public void onBackPressed() {
        mEmotionMainFragment.isShowInterceptBackPress();
//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mHXid);
//        //指定会话消息未读数清零
//        if (conversation != null) {
//            conversation.markAllMessagesAsRead();
//        }
    }

    @Override
    public void thank(String text, String UserName) {
        SendUtil.SendText(mContext, text, mHXid, mOtherUid, mOtherUsername, mOtherUserAvatar,
                mOtherVipLevel, mOtherAuthStatus, mOtherBusinessAuthStatus, mOtherNamgeCardBgImage, null);
        //这里发送方的应该在成功后刷新消息界面  但是  环信的消息回调有时候会特别慢  先用本地的
        initRefresh(mHXid, false, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //回到当前页面注销原先的  再申请
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        if (mFilter != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
