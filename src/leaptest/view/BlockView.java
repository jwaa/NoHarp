/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leaptest.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import leaptest.model.Block;


/**
 *
 * @author silvandeleemput
 */
public class BlockView extends Geometry {
    
    public BlockView(AssetManager assetManager, Block b)
    {
        super("Block", new Box(b.getDimensions().x,b.getDimensions().y,b.getDimensions().z));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Materials/blockwires.j3m"); 
        //Material mat = new Material();
        //mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", (b.isLifted() ? ColorRGBA.Orange : ColorRGBA.Blue));
        this.setMaterial(mat);
        this.setLocalTranslation(b.getPosition());
        this.rotate(0, b.getRotation(), 0);
    }   
    
}
