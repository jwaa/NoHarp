package leaptest.model;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Janne Weijkamp
 */
public class BlockModel
{

    private int[][] model;
    private int elements;
    private static final int EXPORT_SCALING = 21;

    public BlockModel(Grid g)
    {
        this(g, g.getCellDimensions());
    }

    public BlockModel(BlockContainer bc, Vector3f blocksize)
    {
        model = convert(bc.getBlocks(), blocksize);
        elements = bc.getBlocks().size();
    }

    public BlockModel(String filename)
    {
        load(filename);
    }

    public void populateGrid(Material blockmat, Grid g)
    {
        Vector3f offset = new Vector3f(
                -FastMath.floor((float) (model.length) / 2f),
                0.5f,
                -FastMath.floor((float) (model[0].length) / 2f));
        for (int i = 0; i < model.length; i++)
            for (int j = 0; j < model[0].length; j++)
                for (int k = 0; k < model[i][j]; k++)
                {
                    Vector3f pos = offset.add(i, k, j);
                    g.addBlock(new Block(blockmat, pos.mult(g.getCellDimensions()), g.getCellDimensions()));
                }
    }

    public boolean save(String filename)
    {
        File f = new File(filename);
        try
        {
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);

            br.write(this.toString());
            br.close();
            fr.close();
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean load(String filename)
    {
        File f = new File(filename);
        if (!f.exists())
            return false;
        try
        {
            elements = 0;
            Scanner scan = new Scanner(f);
            String[] s = scan.nextLine().replace("\t", " ").split(" ");
            int height = Integer.parseInt(s[1]), width = Integer.parseInt(s[0]);
            this.model = new int[height][width];
            for (int i = 0; i < height; i++)
            {
                s = scan.nextLine().replace("\t", " ").split(" ");
                for (int j = 0; j < width; j++)
                {
                    model[i][j] = Integer.parseInt(s[j]);
                    elements += model[i][j];
                }
            }
            scan.close();
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //transform from arraylist of blocks to matrix
    private static int[][] convert(ArrayList<Block> pb, Vector3f blocksize)
    {
        ArrayList<Vector3f> positions_build = new ArrayList<Vector3f>();
        int nr_blocks = pb.size();

        for (int i = 0; i < nr_blocks; i++)
            positions_build.add(pb.get(i).getPosition());
        int minx = Math.round(positions_build.get(0).getX() / blocksize.x);
        int maxx = Math.round(positions_build.get(0).getX() / blocksize.x);
        int miny = Math.round(positions_build.get(0).getZ() / blocksize.z);
        int maxy = Math.round(positions_build.get(0).getZ() / blocksize.z);

        for (int i = 1; i < nr_blocks; i++)
        {
            if (Math.round(positions_build.get(i).getX() / blocksize.x) < minx)
                minx = Math.round(positions_build.get(i).getX() / blocksize.x);
            else if (Math.round(positions_build.get(i).getX() / blocksize.x) > maxx)
                maxx = Math.round(positions_build.get(i).getX() / blocksize.x);
            if (Math.round(positions_build.get(i).getZ() / blocksize.z) < miny)
                miny = Math.round(positions_build.get(i).getZ() / blocksize.z);
            else if (Math.round(positions_build.get(i).getZ() / blocksize.z) > maxy)
                maxy = Math.round(positions_build.get(i).getZ() / blocksize.z);
        }

        int size_x = maxx - minx + 1;
        int size_y = maxy - miny + 1;

        int[][] build = new int[size_x][size_y];

        // every time that on a block is on that position add 1 to the number in the matrix at that position
        for (int i = 0; i < nr_blocks; i++)
            build[Math.round(positions_build.get(i).getX() / blocksize.x) - minx][Math.round(positions_build.get(i).getZ() / blocksize.z) - miny]++;

        return build;
    }

    private static int[][] rotate(int[][] old, int k)
    {
        switch (k)
        {
            case 1:
                int[][] nieuw = new int[old.length][old[0].length];
                for (int i = 0; i < old.length; i++)
                    for (int j = 0; j < old[0].length; j++)
                        nieuw[nieuw.length - 1 - i][nieuw[0].length - 1 - j] = old[i][j];
                return nieuw;
            case 2:
                int[][] nieuw2 = new int[old[0].length][old.length];
                for (int i = 0; i < old.length; i++)
                    for (int j = 0; j < old[0].length; j++)
                        nieuw2[nieuw2.length - 1 - j][i] = old[i][j];
                return nieuw2;
            case 3:
                int[][] nieuw3 = new int[old.length][old[0].length];
                for (int i = 0; i < old.length; i++)
                    for (int j = 0; j < old[0].length; j++)
                        nieuw3[nieuw3.length - 1 - i][nieuw3[0].length - 1 - j] = old[i][j];
                return nieuw3;
            default:
                return old;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        BlockModel bm;
        if (o instanceof BlockModel)
            bm = (BlockModel) o;
        else if (o instanceof Grid)
            bm = new BlockModel((Grid) o);
        else
            return false;


        if (this.elements == bm.elements)
        {

            // check for equality in 4 different views
            for (int k = 0; k < 4; k++)
            {
                //System.out.println(k);
                int[][] build = rotate(model, k);
                if (exactequal(build, bm.model))
                    return true;
            }
            return false;
        }
        return false;
    }

    private static boolean exactequal(int[][] build, int[][] model)
    {
        if (build.length == model.length && build[0].length == model[0].length) // dimensions the same
        {
            //System.out.println("Dims are the same");
            for (int i = 0; i < model.length; i++)
                for (int j = 0; j < model[0].length; j++)
                    if (build[i][j] != model[i][j])
                        return false;
            //System.out.println("Are the same!");
            return true;
        }
        return false;
    }

    /**
     * Gives Sum of all non-zero elements
     * @return number of elements
     */
    public int getElements()
    {
        return elements;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(model[0].length).append(" ").append(model.length).append("\n");
        for (int i = 0; i < model.length; i++)
        {
            for (int j = 0; j < model[0].length; j++)
                sb.append(model[i][j]).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public int getWidth()
    {
        return model.length;
    }
    
    public int getHeight()
    {
        return model[0].length;
    }

    public void export(String filename)
    {
        int width = model.length * EXPORT_SCALING + 1;
        int height = model[0].length * EXPORT_SCALING + 1;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgbWhite = 16777215;
        int rgbBlack = 0;

        // Make lines
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (i % EXPORT_SCALING == 0 || j % EXPORT_SCALING == 0)
                    img.setRGB(i, j, rgbBlack);
                else
                    img.setRGB(i, j, rgbWhite);

        // Set font
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 24);
        Graphics g = img.getGraphics();
        g.setColor(Color.black);
        g.setFont(f);

        //Draw numbers
        for (int i = 0; i < model.length; i++)
            for (int j = 0; j < model[0].length; j++)
                if (model[i][j] != 0)
                {
                    String str = Integer.toString(model[i][j]);
                    g.drawString(str, (EXPORT_SCALING / 4) + i * EXPORT_SCALING, 20 + j * EXPORT_SCALING);
                }
        g.dispose();
        File output = new File(filename);
        try
        {
            ImageIO.write(img, "jpg", output);
        } catch (IOException ex)
        {
            System.err.println("Did not write image to file.");
        }
    }
}