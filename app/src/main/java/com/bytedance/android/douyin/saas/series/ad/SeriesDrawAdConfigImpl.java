package com.bytedance.android.douyin.saas.series.ad;

import com.bytedance.android.dy.sdk.api.series.ad.SeriesDrawAdConfig;

public class SeriesDrawAdConfigImpl implements SeriesDrawAdConfig {
    @Override
    public boolean getEnable() {
        return false;
    }

    @Override
    public int getFreeSeriesInterval() {
        return 1;
    }

    @Override
    public int getAdEncourageSeriesInterval() {
        return 2;
    }

    @Override
    public int getChargeSeriesInterval() {
        return 2;
    }
}
