package id.kasandra.retail.welcome;

import android.support.v4.app.Fragment;

public abstract class WelcomeFragmentHolder {

    private Fragment mFragment = null;

    public Fragment createFragment() {
        mFragment = fragment();
        return mFragment;
    }

    protected abstract Fragment fragment();

    public Fragment getFragment() {
        return mFragment;
    }

}
