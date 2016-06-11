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

package com.freedcam.apis.sonyremote.modules;

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;
import com.freedcam.apis.sonyremote.CameraHolder;
import com.freedcam.apis.sonyremote.CameraHolder.I_CameraShotMode;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 13.12.2014.
 */
public class ModuleHandlerSony extends AbstractModuleHandler implements I_CameraShotMode
{
    private CameraHolder cameraHolder;
    private final String TAG = ModuleHandlerSony.class.getSimpleName();

    public ModuleHandlerSony(Context context, I_CameraUiWrapper cameraUiWrapper)
    {
        super(context,cameraUiWrapper);
    }

    public void initModules()
    {
        this.cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();
        cameraHolder.cameraShotMode = this;
        PictureModuleSony pic = new PictureModuleSony(context,cameraUiWrapper,moduleEventHandler);
        moduleList.put(pic.ModuleName(), pic);
        VideoModuleSony mov = new VideoModuleSony(context,cameraUiWrapper,moduleEventHandler);
        moduleList.put(mov.ModuleName(), mov);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public void SetModule(String name)
    {
        if (name.equals(KEYS.MODULE_VIDEO))
            cameraHolder.SetShootMode("movie");
        else if (name.equals(KEYS.MODULE_PICTURE))
            cameraHolder.SetShootMode("still");
    }

    @Override
    public void onShootModeChanged(String mode)
    {
        Logger.d(TAG, "ShotmodeChanged:" + mode);
        if (currentModule !=null) {
            currentModule.SetWorkerListner(null);
        }
        if (mode.equals("still"))
        {
            currentModule = moduleList.get(KEYS.MODULE_PICTURE);

            moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetWorkerListner(workerListner);
            currentModule.InitModule();
        }
        else if (mode.equals("movie"))
        {
            currentModule = moduleList.get(KEYS.MODULE_VIDEO);
            moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetWorkerListner(workerListner);
            currentModule.InitModule();
        }
    }

    @Override
    public void onShootModeValuesChanged(String[] modes) {

    }
}
