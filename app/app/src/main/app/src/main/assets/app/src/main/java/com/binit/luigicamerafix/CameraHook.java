package com.binit.luigicamerafix;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class CameraHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.oplus.camera")) return;
        try {
            Class<?> cameraMetadataNative = XposedHelpers.findClass("android.hardware.camera2.impl.CameraMetadataNative", lpparam.classLoader);
            XposedBridge.hookAllMethods(cameraMetadataNative, "get", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.hasThrowable()) {
                        Throwable t = param.getThrowable();
                        if (t instanceof IllegalArgumentException && 
                            t.getMessage() != null && 
                            t.getMessage().contains("com.oplus.sensor.timestamp")) {
                            param.setThrowable(null);
                            param.setResult(null);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("LuigiCameraFix: Failed to hook method");
        }
    }
}
