package com.bluetooth.php.ui.actions;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.bluetooth.php.R;

public class Sign {
    @StringRes int stringResourceId;
    @DrawableRes int imageResourceId;

    public Sign(int stringResource, int imageResource){
        this.stringResourceId = stringResource;
        this.imageResourceId = imageResource;
    }
    public int getStringResourceId(){
        return this.stringResourceId;
    }
    public int getImageResourceId(){
        return this.imageResourceId;
    }
}
