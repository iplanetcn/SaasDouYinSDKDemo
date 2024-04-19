package com.bytedance.android.douyin.saas.series.ad;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.bytedance.android.douyin.saas.DouyinDemoApplication;
import com.bytedance.android.douyin.saas.series.ad.view.FakeSeriesDrawAdView;
import com.bytedance.android.dy.sdk.api.series.ad.IHostSeriesInsertAd;

import org.json.JSONException;
import org.json.JSONObject;

public class HostSeriesInsertAdImpl implements IHostSeriesInsertAd {

    private FakeSeriesDrawAdView view = null;

    private AdListener mListener = null;

    private final FakeSeriesDrawAdView.AdEventListener listener = new FakeSeriesDrawAdView.AdEventListener() {
        @Override
        public void onStartPlay() {
            if (mListener != null) {
                mListener.onPlayStart(view, new JSONObject());
            }
        }

        @Override
        public void onStopPlay() {
            if (mListener != null) {
                mListener.onPlayStop(view, new JSONObject());
            }
        }

        @Override
        public void onCompletePlay() {
            if (mListener != null) {
                mListener.onPlayComplete(view, new JSONObject());
            }
        }

        @Override
        public void onReportCPM(int cpm) {
            if (mListener != null) {
                JSONObject extra = new JSONObject();
                try {
                    extra.put("cpm", cpm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mListener.onReportCPM(view, extra);
            }
        }

        @Override
        public void onClick() {
            if (mListener != null) {
                Toast.makeText(DouyinDemoApplication.application, "点击广告", Toast.LENGTH_LONG).show();
                mListener.onAdClick(view, new JSONObject());
            }
        }

        @Override
        public void onShow() {
            if (mListener != null) {
                mListener.onAdShow(view, new JSONObject());
            }
        }
    };

    @Override
    public View createView(Context context) {
        initViewIfNeed(context);
        return view;
    }

    @Override
    public void onBind(View itemView, JSONObject model) {
        if (view != null) {
            view.addListener(listener);
        }
    }

    @Override
    public void onUnBind(View itemView) {
        if (view != null) {
            view.removeListener(listener);
        }
    }

    @Override
    public void onSelected() {
        if (view != null) {
            view.startPlay();
            view.onShow();
        }
    }

    @Override
    public void onUnSelected() {
        if (view != null) {
            view.stopPlay();
        }
    }

    @Override
    public void setAdEventListener(AdListener listener) {
        mListener = listener;
    }

    private void initViewIfNeed(Context context) {
        if (view != null) {
            return;
        }
        view = new FakeSeriesDrawAdView(context);
    }
}
