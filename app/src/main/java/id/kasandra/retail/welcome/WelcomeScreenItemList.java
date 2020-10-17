package id.kasandra.retail.welcome;

import java.util.ArrayList;
import java.util.Arrays;

/*package*/ class WelcomeScreenItemList implements OnWelcomeScreenPageChangeListener {

    private ArrayList<OnWelcomeScreenPageChangeListener> mItems;

    public WelcomeScreenItemList(OnWelcomeScreenPageChangeListener... items) {
        mItems = new ArrayList<>(Arrays.asList(items));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (OnWelcomeScreenPageChangeListener changeListener : mItems) {
            changeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (OnWelcomeScreenPageChangeListener changeListener : mItems) {
            changeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        for (OnWelcomeScreenPageChangeListener changeListener : mItems) {
            changeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void setup(WelcomeScreenConfiguration config) {
        for (OnWelcomeScreenPageChangeListener changeListener : mItems) {
            changeListener.setup(config);
        }
    }
}
