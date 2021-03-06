/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.basecamera.parameters.modes;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import java.io.File;

import freed.utils.AppSettingsManager;
import freed.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class SDModeParameter extends AbstractModeParameter
{
    public static final String internal = "Internal";
    public static final String external ="External";
    private final AppSettingsManager appSettingsManager;

    public SDModeParameter(AppSettingsManager appSettingsManager) {
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void addEventListner(I_ModeParameterEvent eventListner) {
        super.addEventListner(eventListner);
    }

    @Override
    public boolean IsSupported()
    {
        try {
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP) {
                File file = new File(StringUtils.GetExternalSDCARD());
                return file.exists();
            }
            else
                return true;
        }
        catch (Exception ex)
        {
            return false;
        }

    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {

    }

    @Override
    public String GetValue()
    {
        if (appSettingsManager.GetWriteExternal())
            return external;
        else
            return internal;
    }

    @Override
    public String[] GetValues() {
        return new String[] {internal, external};
    }

    @Override
    public void BackgroundValueHasChanged(String value) {
        super.BackgroundValueHasChanged(value);
    }

}
