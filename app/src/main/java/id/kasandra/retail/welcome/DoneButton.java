package id.kasandra.retail.welcome;

import android.view.View;
import android.widget.TextView;

public class DoneButton extends WelcomeScreenViewWrapper {

    private boolean shouldShow = true;

    public DoneButton(View button) {
        super(button);
        hideImmediately();
    }

    @Override
    public void setup(WelcomeScreenConfiguration config) {
        super.setup(config);
        shouldShow = !config.getUseCustomDoneButton();
        WelcomeUtils.setTypeface((TextView) this.getView(), config.getDoneButtonTypefacePath(), config.getContext());
    }

    @Override
    public void onPageSelected(int pageIndex, int firstPageIndex, int lastPageIndex) {
        setVisibility(shouldShow && pageIndex == lastPageIndex);
    }


}
