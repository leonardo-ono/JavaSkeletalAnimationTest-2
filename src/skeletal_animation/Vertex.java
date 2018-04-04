package skeletal_animation;

import bvh.Bone;
import java.util.ArrayList;
import java.util.List;
import math.Vec4;

/**
 *
 * @author leonardo
 */
public class Vertex {

    public final Vec4 position = new Vec4();
    public final Vec4 positionTransf = new Vec4();
    public static final List<Vec4> positionsTmp = new ArrayList<Vec4>();
    public List<Bone> bones = new ArrayList<Bone>();
    public List<Double> weights = new ArrayList<Double>();

    public Vertex(double px, double py, double pz) {
        position.set(px, py, pz, 1);
    }
    
    public void calculate() {
        positionTransf.set(0, 0, 0, 0);
        for (int i = 0; i < bones.size(); i++) {
            positionsTmp.get(i).set(position);
            bones.get(i).getInverseTransform().multiply(positionsTmp.get(i));
            bones.get(i).getTransform().multiply(positionsTmp.get(i));
            positionsTmp.get(i).multiply(weights.get(i));
            positionTransf.add(positionsTmp.get(i));
        }
        positionTransf.setW(1);
    }

    public List<Bone> getBones() {
        return bones;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public static List<Vec4> getPositionsTmp() {
        return positionsTmp;
    }
    
}
