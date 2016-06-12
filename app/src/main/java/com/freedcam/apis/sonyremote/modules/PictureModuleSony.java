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
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureStates;
import com.freedcam.apis.sonyremote.CameraHolder;
import com.freedcam.apis.sonyremote.parameters.ParameterHandler;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by troop on 22.12.2014.
 */
public class PictureModuleSony extends AbstractModule implements I_PictureCallback, I_CameraStatusChanged
{
    private static String TAG = PictureModuleSony.class.getSimpleName();
    private CameraHolder cameraHolder;

    public PictureModuleSony(Context context, I_CameraUiWrapper cameraUiWrapper) {
        super(context, cameraUiWrapper);
        name = KEYS.MODULE_PICTURE;
        cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();


    }

    @Override
    public String ModuleName() {
        return super.ModuleName();
    }

    @Override
    public boolean DoWork()
    {
        if (cameraUiWrapper.GetParameterHandler().ContShootMode != null && cameraUiWrapper.GetParameterHandler().ContShootMode.IsSupported()) {
            String shootmode = ((ParameterHandler) cameraUiWrapper.GetParameterHandler()).ContShootMode.GetValue();
            if (!isWorking && shootmode.equals("Single"))
            {
                changeCaptureState(CaptureStates.image_capture_start);
                takePicture();
            }
            else if (!isWorking)
            {
                changeCaptureState(CaptureStates.continouse_capture_start);
                cameraHolder.startContShoot(this);
                return true;
            } else
            {
                changeCaptureState(CaptureStates.cont_capture_stop_while_working);
                cameraHolder.stopContShoot(this);
                return false;
            }
        }
        else
            if (!isWorking)
            {
                changeCaptureState(CaptureStates.image_capture_start);
                takePicture();
            }
        return true;
    }

    @Override
    public void InitModule()
    {
        Logger.d(TAG, "InitModule");
        cameraHolder.CameraStatusListner = this;
        onCameraStatusChanged(cameraHolder.GetCameraStatus());
    }

    @Override
    public void DestroyModule() {

    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }


    private void takePicture()
    {
        cameraHolder.TakePicture(this);
        Logger.d(TAG, "Start Take Picture");
    }

    @Override
    public void onPictureTaken(URL url)
    {
        File file = new File(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), ".jpg"));
        try {
            file.createNewFile();
        } catch (IOException e) {
            Logger.exception(e);
        }
        InputStream inputStream = null;
        OutputStream output = null;
        try {
            inputStream = new BufferedInputStream(url.openStream());
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal())
                output = new FileOutputStream(file);
            else
            {
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
                DocumentFile wr = df.createFile("image/jpeg", file.getName());
                output = context.getContentResolver().openOutputStream(wr.getUri());
            }
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Logger.exception(e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Logger.exception(e);
            }

            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

        MediaScannerManager.ScanMedia(context.getApplicationContext(), file);
        cameraUiWrapper.GetModuleHandler().WorkFinished(file);
    }


    @Override
    public void onCameraStatusChanged(String status)
    {
        Logger.d(TAG, "Status:"+status);
        if (status.equals("IDLE") && isWorking)
        {
            isWorking = false;
            if (currentWorkState == CaptureStates.image_capture_start)
                changeCaptureState(CaptureStates.image_capture_stop);
            else if (currentWorkState == CaptureStates.continouse_capture_work_start || currentWorkState == CaptureStates.continouse_capture_start)
                changeCaptureState(CaptureStates.continouse_capture_work_stop);
        }
        else if ((status.equals("StillCapturing") || status.equals("StillSaving")) && !isWorking) {
            isWorking = true;
            if (currentWorkState == CaptureStates.image_capture_stop)
                changeCaptureState(CaptureStates.image_capture_start);
            else if (currentWorkState == CaptureStates.continouse_capture_work_stop || currentWorkState == CaptureStates.continouse_capture_stop)
                changeCaptureState(CaptureStates.continouse_capture_work_start);
        }

    }
}
