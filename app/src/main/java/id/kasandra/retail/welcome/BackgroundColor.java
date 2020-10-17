package id.kasandra.retail.welcome;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

public class BackgroundColor {

    private int mColor = Color.GRAY;

    public BackgroundColor(@Nullable @ColorInt Integer color) {
        if (color != null)
            this.mColor = color;
    }

    public BackgroundColor(@Nullable @ColorInt Integer color, int defaultColor) {
        mColor = defaultColor;
        if (color != null)
            mColor = color;
    }

    public int value() {
        return this.mColor;
    }

}
