/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.utils;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
//import java.awt.GraphicsEnvironment;

/**
 *
 * @author silvandeleemput
 */
public class DefaultAppSettings {
    
    public static void apply(SimpleApplication app, ConfigSettings config)
    {
        // Grab settings from config
        int fps = Integer.valueOf(config.getSettingValue("FPS")).intValue();
        boolean fullscreen = config.getSetting("FullScreen");
        boolean menu = config.getSetting("Menu");
        boolean fpscounter = config.getSetting("FPScounter");
        int width = Integer.valueOf(config.getSettingValue("ScreenWidth")).intValue();
        int height = Integer.valueOf(config.getSettingValue("ScreenHeight")).intValue();
        int freq = Integer.valueOf(config.getSettingValue("ScreenFrequency")).intValue();                

        AppSettings settings = new AppSettings(true);
        // Relevant settings
        settings.put("FrameRate",fps);         
        if (!menu)
        {
            settings.put("Fullscreen", fullscreen);
            if (fullscreen)
            {
                settings.put("Width", width);
                settings.put("Height", height);
                settings.put("Frequency", freq);
            } else {
                settings.put("Width", 800);
                settings.put("Height", 600);            
                settings.put("Frequency", 0);
            }
            settings.put("Title","HCI NoHarp Experimental environment");
            settings.put("UseInput", true);       

            // Additional default settings
            settings.put("MinHeight", 0);
            settings.put("BitsPerPixel", 16);
            settings.put("DepthBits", 24);
            settings.put("StencilBits", 0);
            settings.put("Samples", 0);
            settings.put("VSync", false);
            settings.put("Renderer", AppSettings.LWJGL_OPENGL2);
            settings.put("AudioRenderer", AppSettings.LWJGL_OPENAL);
            settings.put("DisableJoysticks", true);
            settings.put("SettingsDialogImage", "/com/jme3/app/Monkey.png");
          //  settings.put("Icons", null);
        }
        app.setSettings(settings); 
        app.setShowSettings(menu);
        app.setDisplayFps(fpscounter);
        app.setDisplayStatView(false);
    }
    
}
