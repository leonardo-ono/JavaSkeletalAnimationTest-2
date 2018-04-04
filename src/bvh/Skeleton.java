package bvh;

import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Skeleton {
    
    private Bone rootNode;
    private Motion motion;
    private final Map<String, Bone> nodes = new HashMap<String, Bone>();

    public Skeleton(String resource) {
        Parser parser = new Parser();
        parser.load(resource);
        rootNode = new Bone(parser);
        motion = new Motion(parser);
        rootNode.fillNodesMap(nodes);
        parser.close();
    }

    public Bone getRootNode() {
        return rootNode;
    }

    public Motion getMotion() {
        return motion;
    }

    public Map<String, Bone> getNodes() {
        return nodes;
    }
    
    public int getFrameSize() {
        return motion.getFrameSize();
    }
    
    public void setPose(int frameIndex) {
        if (frameIndex < 0) {
            rootNode.setPose(null);
        }
        else {
            rootNode.setPose(motion.getData(frameIndex));
        }
    }
    
}
