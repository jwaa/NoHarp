/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import leaptest.Main;

/**
 *
 * @author silvandeleemput
 */
public class StopTimerControl implements Updatable {

    private Main main;
    private long begintime, duration;
    
    public StopTimerControl(Main main, long duration)
    {
        this.main = main;
        this.begintime = System.currentTimeMillis();
        this.duration = duration;
    }
    
    public void update(float tpf) 
    {
        if (System.currentTimeMillis() - begintime > duration)
            main.setShutDown(true);
    }
    
}
