package org.exallium.tradetracker.app.view.widgets

import android.R
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 *
 *
 * To use the component, simply add it to your view hierarchy. Then in your
 * [android.app.Activity] or [android.support.v4.app.Fragment] call
 * [.setViewPager] providing it the ViewPager this layout is being used for.
 *
 *
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via [.setSelectedIndicatorColors] and [.setDividerColors]. The
 * alternative is via the [TabColorizer] interface which provides you complete control over
 * which color is used for any individual position.
 *
 *
 * The views used as tabs can be customized by calling [.setCustomTabView],
 * providing the layout ID of your custom layout.
 */
public class SlidingTabLayout : HorizontalScrollView {

    constructor (context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}
    constructor (context: Context, attrs: AttributeSet) : this(context, attrs, 0) {}

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * [.setCustomTabColorizer].
     */
    public trait TabColorizer {

        /**
         * @return return the color of the indicator used when `position` is selected.
         */
        public fun getIndicatorColor(position: Int): Int

        /**
         * @return return the color of the divider drawn to the right of `position`.
         */
        public fun getDividerColor(position: Int): Int

    }

    private val mTitleOffset: Int

    private var mTabViewLayoutId: Int = 0
    private var mTabViewTextViewId: Int = 0

    private var mViewPager: ViewPager? = null
    private var mViewPagerPageChangeListener: ViewPager.OnPageChangeListener? = null

    private val mTabStrip: SlidingTabStrip

    init {

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false)
        // Make sure that the Tab Strips fills this View
        setFillViewport(true)

        mTitleOffset = (TITLE_OFFSET_DIPS.toFloat() * getResources().getDisplayMetrics().density).toInt()

        mTabStrip = SlidingTabStrip(getContext())
        addView(mTabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Set the custom [TabColorizer] to be used.

     * If you only require simple custmisation then you can use
     * [.setSelectedIndicatorColors] and [.setDividerColors] to achieve
     * similar effects.
     */
    public fun setCustomTabColorizer(tabColorizer: TabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer)
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public fun setSelectedIndicatorColors(vararg colors: Int) {
        mTabStrip.setSelectedIndicatorColors(*colors)
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public fun setDividerColors(vararg colors: Int) {
        mTabStrip.setDividerColors(*colors)
    }

    /**
     * Set the [ViewPager.OnPageChangeListener]. When using [SlidingTabLayout] you are
     * required to set any [ViewPager.OnPageChangeListener] through this method. This is so
     * that the layout can update it's scroll position correctly.

     * @see ViewPager.setOnPageChangeListener
     */
    public fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mViewPagerPageChangeListener = listener
    }

    /**
     * Set the custom layout to be inflated for the tab views.

     * @param layoutResId Layout id to be inflated
     * *
     * @param textViewId id of the [TextView] in the inflated view
     */
    public fun setCustomTabView(layoutResId: Int, textViewId: Int) {
        mTabViewLayoutId = layoutResId
        mTabViewTextViewId = textViewId
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public fun setViewPager(viewPager: ViewPager?) {
        mTabStrip.removeAllViews()

        mViewPager = viewPager
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(InternalViewPagerListener())
            populateTabStrip()
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * [.setCustomTabView].
     */
    protected fun createDefaultTabView(context: Context): TextView {
        val textView = TextView(context)
        textView.setGravity(Gravity.CENTER)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP.toFloat())
        textView.setTypeface(Typeface.DEFAULT_BOLD)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue()
            getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            textView.setBackgroundResource(outValue.resourceId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true)
        }

        val padding = (TAB_VIEW_PADDING_DIPS.toFloat() * getResources().getDisplayMetrics().density).toInt()
        textView.setPadding(padding, padding, padding, padding)

        return textView
    }

    private fun populateTabStrip() {
        val adapter = mViewPager!!.getAdapter()
        val tabClickListener = TabClickListener()

        for (i in 0..adapter.getCount() - 1) {
            var tabView: View? = null
            var tabTitleView: TextView? = null

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false)
                tabTitleView = tabView!!.findViewById(mTabViewTextViewId) as TextView
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext())
            }

            if (tabTitleView == null && javaClass<TextView>().isInstance(tabView)) {
                tabTitleView = tabView as TextView
            }

            tabTitleView!!.setText(adapter.getPageTitle(i))
            tabView!!.setOnClickListener(tabClickListener)

            mTabStrip.addView(tabView)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (mViewPager != null) {
            scrollToTab(mViewPager!!.getCurrentItem(), 0)
        }
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Int) {
        val tabStripChildCount = mTabStrip.getChildCount()
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }

        val selectedChild = mTabStrip.getChildAt(tabIndex)
        if (selectedChild != null) {
            var targetScrollX = selectedChild.getLeft() + positionOffset

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset
            }

            scrollTo(targetScrollX, 0)
        }
    }

    private inner class InternalViewPagerListener : ViewPager.OnPageChangeListener {
        private var mScrollState: Int = 0

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val tabStripChildCount = mTabStrip.getChildCount()
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset)

            val selectedTitle = mTabStrip.getChildAt(position)
            val extraOffset = if ((selectedTitle != null))
                (positionOffset * selectedTitle.getWidth().toFloat()).toInt()
            else
                0
            scrollToTab(position, extraOffset)

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0.toFloat())
                scrollToTab(position, 0)
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageSelected(position)
            }
        }

    }

    private inner class TabClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            for (i in 0..mTabStrip.getChildCount() - 1) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager!!.setCurrentItem(i)
                    return
                }
            }
        }
    }

    companion object {

        private val TITLE_OFFSET_DIPS = 24
        private val TAB_VIEW_PADDING_DIPS = 16
        private val TAB_VIEW_TEXT_SIZE_SP = 12
    }

}