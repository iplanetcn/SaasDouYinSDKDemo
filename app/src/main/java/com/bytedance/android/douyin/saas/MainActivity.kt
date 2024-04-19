package com.bytedance.android.douyin.saas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bytedance.android.douyin.saas.fragments.FeedFragment
import com.bytedance.android.douyin.saas.fragments.LiveFragment
import com.bytedance.android.douyin.saas.fragments.MainFragment
import com.bytedance.android.douyin.saas.fragments.SeriesHomeFragment
import com.bytedance.android.douyin.saas.fragments.SeriesLightFragment
import com.bytedance.android.dy.sdk.DouYinSDK
import com.bytedance.android.dy.sdk.api.log.DYLogger
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var fragmentList: List<Fragment>
    private var currentIndex = 0
    private var mCurrentFragment: Fragment? = null

    var bottomNavigationView: BottomNavigationView? = null
    private lateinit var feedFragment: FeedFragment
    private lateinit var seriesHomeFragment: SeriesHomeFragment
    private lateinit var seriesLightFragment: SeriesLightFragment
    private lateinit var liveFragment: LiveFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DYLogger.openDebugMode()
        DouYinSDK.getInstance().start()
        feedFragment = FeedFragment()
        seriesHomeFragment = SeriesHomeFragment()
        seriesLightFragment = SeriesLightFragment()
        liveFragment = LiveFragment()
        fragmentList = listOf(
            MainFragment(),
            liveFragment,
            feedFragment,
            seriesHomeFragment,
            seriesLightFragment
        )

        bottomNavigationView = findViewById(R.id.bottom_navi_view)
        bottomNavigationView?.setOnNavigationItemSelectedListener(this)

        replaceFragment(0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when (item.itemId) {
            R.id.type0 -> replaceFragment(0)
            R.id.type1 -> replaceFragment(1)
            R.id.type2 -> replaceFragment(2)
            R.id.type3 -> replaceFragment(3)
            R.id.type4 -> replaceFragment(4)
        }
        return false
    }

    private fun replaceFragment(fragmentIndex: Int) {
        currentIndex = fragmentIndex
        val fragment = fragmentList[fragmentIndex]
        if (mCurrentFragment != fragment) {
            val ft = supportFragmentManager.beginTransaction()
            if (null != mCurrentFragment) {
                ft.hide(mCurrentFragment!!)
                if (mCurrentFragment === feedFragment) {
                    feedFragment?.feedLayout?.deliverHiddenChanged(true)
                }
                if (mCurrentFragment === seriesHomeFragment) {
                    seriesHomeFragment.seriesHomeLayout?.deliverHiddenChanged(true)
                }
                if (mCurrentFragment === seriesLightFragment) {
                    seriesLightFragment.seriesLightManager?.deliverHiddenChanged(true)
                }
            }
            mCurrentFragment = fragment
            if (mCurrentFragment === feedFragment) {
                feedFragment?.feedLayout?.deliverHiddenChanged(false)
            }
            if (mCurrentFragment === seriesHomeFragment) {
                seriesHomeFragment.seriesHomeLayout?.deliverHiddenChanged(false)
            }
            if (mCurrentFragment === seriesLightFragment) {
                seriesLightFragment.seriesLightManager?.deliverHiddenChanged(false)
            }
            if (!fragment.isAdded) {
                ft.add(R.id.main_fragment_container, fragment, fragment::class.java.name)
            } else {
                ft.show(fragment)
            }
            ft.commit()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 10000) {
            bottomNavigationView?.selectedItemId = R.id.type2
        }
    }

    override fun onBackPressed() {
        if (mCurrentFragment == feedFragment) {
            if (feedFragment?.onBackPressed() ?: false) {
                return
            }
        }
        super.onBackPressed()
    }

}