package bvh;

import static bvh.Bone.Type.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import math.Mat4;
import math.Vec4;

/**
 * Bone class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Bone {
    
    public static enum Type { ROOT, JOINT, END }
    
    private final Type type;
    private String name;
    private final Vec4 offset = new Vec4();
    private String[] channels;
    private Bone parent;
    private final List<Bone> children = new ArrayList<Bone>();
    
    private static final Mat4 transformTmp = new Mat4();
    private final Mat4 transform = new Mat4();
    private final Mat4 inverseTransform = new Mat4();
    private final Vec4 position = new Vec4();

    public Bone(Type type) {
        this.type = type;
    }
    
    public Bone(Parser parser) {
        this(parser, ROOT, null);
    }
    
    public Bone(Parser parser, Type type, Bone parent) {
        this.type = type;
        this.parent = parent;
        switch (type) {
            case ROOT:
                parser.expect("HIERARCHY");
                name = parser.expect("ROOT")[1];
                break;
            case JOINT:
                name = parser.expect("JOINT")[1];
                break;
            case END:
                name = parser.expect("End")[1];
        }
        parser.expect("{");
        setOffset(parser.expect("OFFSET"));
        if (parser.getLine().startsWith("CHANNELS")) {
            setChannels(parser.expect("CHANNELS"));
        }
        while (parser.getLine().startsWith("JOINT")) {
            children.add(new Bone(parser, Type.JOINT, this));
        }
        if (parser.getLine().startsWith("End")) {
            children.add(new Bone(parser, Type.END, this));
        }
        parser.expect("}");
    }
    
    private void setOffset(String[] offsetStr) {
        offset.setX(Double.parseDouble(offsetStr[1]));
        offset.setY(Double.parseDouble(offsetStr[2]));
        offset.setZ(Double.parseDouble(offsetStr[3]));
        offset.setW(1);
    }

    public void setChannels(String[] channelsTmp) {
        int size = Integer.parseInt(channelsTmp[1]);
        this.channels = new String[size];
        for (int i = 0; i < size; i++) {
            this.channels[i] = channelsTmp[2 + i].toLowerCase();
        }
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Vec4 getOffset() {
        return offset;
    }

    public Vec4 getPosition() {
        return position;
    }

    public Mat4 getTransform() {
        return transform;
    }

    public Mat4 getInverseTransform() {
        return inverseTransform;
    }

    public String[] getChannels() {
        return channels;
    }

    public Bone getParent() {
        return parent;
    }

    public void setParent(Bone parent) {
        this.parent = parent;
    }

    public List<Bone> getChildren() {
        return children;
    }
    
    public void fillNodesList(List<Bone> nodes) {
        if (type == Type.ROOT) {
            nodes.clear();
        }
        nodes.add(this);
        for (Bone child : children) {
            child.fillNodesList(nodes);
        }
    }
    
    public void fillNodesMap(Map<String, Bone> nodes) {
        if (type == Type.ROOT) {
            nodes.clear();
        }
        nodes.put(this.getName(), this);
        for (Bone child : children) {
            child.fillNodesMap(nodes);
        }
    }
    
    private static final int[] DATA_INDEX = { 0 };
    
    public void setPose(double[] data) {
        transform.setIdentity();
        DATA_INDEX[0] = 0;
        setPose(data, DATA_INDEX);
    }

    private void setPose(double[] data, int[] dataIndex) {
        if (type == ROOT) {
            transform.setTranslation(offset);
        }
        else {
            transform.set(parent.getTransform());
            transformTmp.setTranslation(offset);
            transform.multiply(transformTmp);
        }
        
        if (channels != null && data != null) {
            for (int c = 0; c < channels.length; c++) {
                String channel = channels[c];
                double value = data[dataIndex[0]++];
                if (channel.equals("xposition")) {
                    transformTmp.setTranslation(value, 0, 0);
                }
                else if (channel.equals("yposition")) {
                    transformTmp.setTranslation(0, value, 0);
                }
                else if (channel.equals("zposition")) {
                    transformTmp.setTranslation(0, 0, value);
                }
                else if (channel.equals("zrotation")) {
                    transformTmp.setRotationZ(Math.toRadians(value));
                }
                else if (channel.equals("yrotation")) {
                    transformTmp.setRotationY(Math.toRadians(value));
                }
                else if (channel.equals("xrotation")) {
                    transformTmp.setRotationX(Math.toRadians(value));
                }
                transform.multiply(transformTmp);
            }
        }
        
        position.set(0, 0, 0, 1);
        transform.multiply(position);
        
        for (Bone child : children) {
            child.setPose(data, dataIndex);
        }
    }

    public void calculateInverseTransformation() {
        if (type == ROOT) {
            setPose(null);
        }
        inverseTransform.set(transform);
        inverseTransform.invert();
        for (Bone child : children) {
            child.calculateInverseTransformation();
        }
    }
    
}
