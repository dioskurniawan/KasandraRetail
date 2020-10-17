package id.kasandra.retail.welcome;

import android.support.v4.view.ViewPager;

public interface OnWelcomeScreenPageChangeListener extends ViewPager.OnPageChangeListener {
    void setup(WelcomeScreenConfiguration config);
}

