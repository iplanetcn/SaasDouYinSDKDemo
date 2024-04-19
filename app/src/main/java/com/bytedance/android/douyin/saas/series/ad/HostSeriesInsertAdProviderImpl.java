package com.bytedance.android.douyin.saas.series.ad;

import com.bytedance.android.dy.sdk.api.series.ad.IHostSeriesInsertAd;
import com.bytedance.android.dy.sdk.api.series.ad.IHostSeriesInsertAdProvider;

public class HostSeriesInsertAdProviderImpl implements IHostSeriesInsertAdProvider {
    @Override
    public IHostSeriesInsertAd getHostSeriesDrawAd() {
        return new HostSeriesInsertAdImpl();
    }
}
