package com.krish.tfusion.ui.friendFragment.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.krish.tfusion.ui.friendFragment.AddFragment
import com.krish.tfusion.ui.friendFragment.AllFragment
import com.krish.tfusion.ui.friendFragment.PendingFragment

private const val TAG = "ViewPagerAdapter"
class ViewPagerAdapter(
    private val resultBundle: Bundle,
    private val fragments : ArrayList<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        fragments[position].arguments = resultBundle
        return fragments[position]
    }
}