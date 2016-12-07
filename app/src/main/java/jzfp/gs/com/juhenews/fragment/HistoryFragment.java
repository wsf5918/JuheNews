package jzfp.gs.com.juhenews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import jzfp.gs.com.juhenews.R;
import jzfp.gs.com.juhenews.adapter.HistoryAdapter;
import jzfp.gs.com.juhenews.utils.OkhttpUtils;
import jzfp.gs.com.juhenews.gsonbean.historybean.HistoryBean;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lisa on 2016/12/2.
 */

public class HistoryFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private HistoryAdapter historyAdapter;

    /*
     * new instance 方法 HistoryFragment
     */
    public static HistoryFragment newInstance(String params) {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //      create main panel for fragment
        View view = inflater.inflate(R.layout.base_fragment, null, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        recyclerView = (RecyclerView) view.findViewById(R.id.tv_content);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        historyAdapter = new HistoryAdapter();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(historyAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                pullHistoryData();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        pullHistoryData();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light));
        return view;
    }


    /*获取头条信息*/
    public void pullHistoryData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String response = OkhttpUtils.getHistory();
                System.err.println("yidong -- history = " + response);
                subscriber.onNext(response);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onNext(String response) {
                swipeRefreshLayout.setRefreshing(true);
                Gson gson = new Gson();
                HistoryBean historyBean = gson.fromJson(response, HistoryBean.class);
                historyAdapter.setHistory(historyBean);
            }

            @Override
            public void onCompleted() {

                System.err.println("yidong -- onNext");
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                //handle exception
            }
        });
    }
}