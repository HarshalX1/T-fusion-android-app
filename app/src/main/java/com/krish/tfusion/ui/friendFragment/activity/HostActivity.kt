package com.krish.tfusion.ui.friendFragment.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.krish.tfusion.databinding.ActivityHostBinding
import com.krish.tfusion.ui.friendFragment.AddFragment
import com.krish.tfusion.ui.friendFragment.AllFragment
import com.krish.tfusion.ui.friendFragment.PendingFragment
import com.krish.tfusion.ui.friendFragment.adapter.ViewPagerAdapter

private const val TAG = "HostActivity"
class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    private val args by navArgs<HostActivityArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val tabLayout = binding.tabLayout
        val viewPager2 = binding.viewPager2

        val resultBundle = Bundle()
        resultBundle.putParcelable("currentUser", args.currentUser)
        val fragments = ArrayList<Fragment>()
        fragments.add(AddFragment())
        fragments.add(PendingFragment())
        fragments.add(AllFragment())
        val adapter = ViewPagerAdapter(resultBundle,fragments,supportFragmentManager, lifecycle)

        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Add"
                }
                1 -> {
                    tab.text = "Pending"
                }
                2 -> {
                    tab.text = "All"
                }
            }
        }.attach()

    }
}