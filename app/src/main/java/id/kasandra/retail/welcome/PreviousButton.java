package id.kasandra.retail.welcome;

import android.view.View;

public class PreviousButton extends WelcomeScreenViewWrapper {

    private boolean shouldShow = false;

    public PreviousButton(View button) {
        super(button);
    }

    @Override
    public void setup(WelcomeScreenConfiguration config) {
        super.setup(config);
        this.shouldShow = config.getShowPrevButton();
    }

    @Override
    public void onPageSelected(int pageIndex, int firstPageIndex, int lastPageIndex) {
        setVisibility(shouldShow && pageIndex != firstPageIndex);
    }


}
