/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.controller;

import leaptest.model.Grid;
import leaptest.view.MaterialManager;
import leaptest.view.GridRing;

/**
 *
 * @author silvandeleemput
 */
public class GridRingColorControl implements Updatable {

    private float rotation;
    private Grid grid;
    private GridRing gco;
    
    public GridRingColorControl(Grid grid, GridRing gco)
    {
        this.grid = grid;
        this.gco = gco;
        this.rotation = grid.getRotation();
    }
    
    public void update(float tpf) {
        gco.setMaterial((rotation == grid.getRotation()? MaterialManager.gridnormal : MaterialManager.ringrotate ));
        rotation = grid.getRotation();
    }
    
}
