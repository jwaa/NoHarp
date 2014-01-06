package leaptest.utils;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import leaptest.model.Block;

/**
 *
 * @author Janne Weijkamp
 */
public class ModelChecker {

    public ModelChecker() {
    }

    //transform from arraylist of blocks to matrix
    public float[][] transform(ArrayList<Block> pb) {
        ArrayList<Vector3f> positions_build = new ArrayList<Vector3f>();
        int nr_blocks = pb.size();

        for (int i = 0; i < nr_blocks; i++) {
            positions_build.add(pb.get(i).getPosition());
        }
        float minx = positions_build.get(0).getX();
        float maxx = positions_build.get(0).getX();
        float miny = positions_build.get(0).getY();
        float maxy = positions_build.get(0).getY();

        for (int i = 1; i < nr_blocks; i++) {
            if (positions_build.get(i).getX() < minx) {
                minx = positions_build.get(i).getX();
            } else if (positions_build.get(i).getX() > maxx) {
                maxx = positions_build.get(i).getX();
            }
            if (positions_build.get(i).getY() < miny) {
                miny = positions_build.get(i).getY();
            } else if (positions_build.get(i).getY() > maxy) {
                maxy = positions_build.get(i).getY();
            }
        }

        float size_x = maxx - minx + 1;
        float size_y = maxy - miny + 1;

        float[][] build = new float[(int) size_x][(int) size_y];
        // fill matrix with zeros
        for (int i = 0; i < size_x; i++) {
            for (int j = 0; j < size_y; j++) {
                build[i][j] = 0;
            }
        }

        // every time that on a block is on that position add 1 to the number in the matrix at that position
        for (int i = 0; i < nr_blocks; i++) {
            build[(int) positions_build.get(i).getX()][(int) positions_build.get(i).getY()]++;
        }
        return build;
    }

    public float[][] transpose(float[][] old, int k) {
        switch (k) {
            case 1:
                float[][] nieuw = new float[old.length][old[0].length];
                for (int i = 0; i < old.length; i++) {
                    for (int j = 0; j < old[0].length; j++) {
                        nieuw[nieuw.length - 1 - i][nieuw[0].length - 1 - j] = old[i][j];
                    }
                }
                return nieuw;
            case 2:
                float[][] nieuw2 = new float[old[0].length][old.length];
                for (int i = 0; i < old.length; i++) {
                    for (int j = 0; j < old[0].length; j++) {
                        nieuw2[nieuw2.length - 1 - j][i] = old[i][j];
                    }
                }
                return nieuw2;
            case 3:
                float[][] nieuw3 = new float[old.length][old[0].length];
                for (int i = 0; i < old.length; i++) {
                    for (int j = 0; j < old[0].length; j++) {
                        nieuw3[nieuw3.length - 1 - i][nieuw3[0].length - 1 - j] = old[i][j];
                    }
                }
                return nieuw3;
        }
        return old;
    }

    public boolean equal(ArrayList<Block> pb, float[][] model) {
        float[][] build;
		
		int nr_blocks_model = model.length * model[0].length;
		
        if (pb.size() == nr_blocks_model) {
            build = transform(pb);

            // check for equality in 4 different views
            for (int k = 0; k < 4; k++) {
                if (k != 0) {
                    build = transpose(build, k);
                }

                outerloop:
                if (build.length == model.length && build[0].length == model[0].length) // dimensions the same?, unncessasary?
                {
                    for (int i = 0; i < model.length; i++) {
                        for (int j = 0; j < model[0].length; j++) {
                            if (build[i][j] != model[i][j]) {
                                break outerloop;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    
    public boolean equaltest(float[][] build, float[][] model) {
	
        int nr_blocks_model = model.length * model[0].length;
        if (build.length * build[0].length == nr_blocks_model) {
            
            // check for equality in 4 different views
            for (int k = 0; k < 4; k++) {
                if (k != 0) {
                    build = transpose(build, k);
                }

                outerloop:
                if (build.length == model.length && build[0].length == model[0].length) // dimensions the same?, unncessasary?
                {
                    for (int i = 0; i < model.length; i++) {
                        for (int j = 0; j < model[0].length; j++) {
                            if (build[i][j] != model[i][j]) {
                                break outerloop;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        } else {return false;
        }
    }
}