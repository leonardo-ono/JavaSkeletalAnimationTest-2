package skeletal_animation;

import br.ol.mesh.Mesh;
import bvh.Bone;
import bvh.Skeleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.Vec4;

/**
 *
 * @author leonardo
 */
public class WeightLoader {

    public static void load(String resource, Mesh mesh, Skeleton skeleton) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(WeightLoader.class.getResourceAsStream("/res/" + resource)));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println("line: " + line);
                if (line.startsWith("bone ")) {
                        String boneName = line.split("\\ ")[1].trim(); 
                        String indexes = br.readLine(); 
                        String weights = br.readLine();
                        attachVertexToBone(boneName, indexes.split(","), weights.split(","), mesh, skeleton);
                }
                    
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(WeightLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        
    }
    
    private static void attachVertexToBone(String boneName, String[] indexesStr, String[] weightsStr, Mesh mesh, Skeleton skeleton) {
        Bone bone = skeleton.getNodes().get(boneName);
        int[] indexes = new int[indexesStr.length];
        double[] weights = new double[weightsStr.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = Integer.parseInt(indexesStr[i]);
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Double.parseDouble(weightsStr[i]);
        }
        
        for (int i = 0; i < indexes.length; i++) {
            Vertex vertex = mesh.getVertices().get(indexes[i]);
            vertex.getBones().add(bone);
            vertex.getWeights().add(weights[i]);
            vertex.getPositionsTmp().add(new Vec4());
        }
        
        System.out.println("weights loaded ok :)");
    }
    
    public static void convertFBXtoWeight(String resource) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(WeightLoader.class.getResourceAsStream("/res/" + resource)));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Deformer:")) {
                    processDeformer(line, br);
                }
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(WeightLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    private static void processDeformer(String line, BufferedReader br) {
        try {
            line = line.replace("Deformer: ", "");
            line = line.split(",")[0].replace("\"", "");
            String[] data = line.split("\\ ");
            if (data.length < 3) {
                return;
            }
            String boneName = data[2];
            System.out.println("bone " + replaceBoneName(boneName));
            while ((line = br.readLine().trim()) != null && !line.startsWith("Indexes: ")) {};
            System.out.print(line.replace("Indexes: ", ""));
            while ((line = br.readLine().trim()) != null && !line.startsWith("Weights: ")) {
                System.out.print(line.trim());
            };
            System.out.println();
            System.out.print(line.replace("Weights: ", ""));
            while ((line = br.readLine().trim()) != null && !line.startsWith("Transform: ")) {
                System.out.print(line.trim());
            };
            System.out.println();
            while ((line = br.readLine().trim()) != null && !line.startsWith("}")) { };
        } catch (Exception ex) {
            //Logger.getLogger(WeightLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    private static String replaceBoneName(String boneName) {
        boneName = boneName.trim();
        for (int i = 9; i >= 0; i--) {
            String istr = "00" + i;
            istr = istr.substring(istr.length() - 2, istr.length());
            
            String left = (i > 0 ? "_" + istr : "") + "_L";
            String repl = left.replace("_", ".");
            if (boneName.endsWith(left)) {
                boneName = boneName.substring(0, boneName.length() - repl.length()) + repl;
                return boneName;
            }
            
            String right = (i > 0 ? "_" + istr : "") + "_R";
            repl = right.replace("_", ".");
            if (boneName.endsWith(right)) {
                boneName = boneName.substring(0, boneName.length() - repl.length()) + repl;
                return boneName;
            }
        }
        return boneName;
    }

    public static void main(String[] args) {    
        convertFBXtoWeight("test.fbx");
    }
    
}
