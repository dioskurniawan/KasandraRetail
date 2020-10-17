package id.kasandra.retail.welcome;

import android.content.Context;
import android.util.AttributeSet;

public class WelcomeScreenViewPagerIndicator extends SimpleViewPagerIndicator implements OnWelcomeScreenPageChangeListener {

    public WelcomeScreenViewPagerIndicator(Context context) {
        super(context);
    }

    public WelcomeScreenViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WelcomeScreenViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setup(WelcomeScreenConfiguration config) {
        setTotalPages(config.viewablePageCount());
    }
}
