package br.ol.mesh;

import bvh.Parser;
import java.util.ArrayList;
import java.util.List;
import math.Vec4;
import skeletal_animation.Vertex;

/**
 * (Wavefront) Mesh class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Mesh {

    private double ty;
    private double scale;
    private final List<Vertex> vertices = new ArrayList<Vertex>();
    private final List<Face> faces = new ArrayList<Face>();
    
    public Mesh(String resource, double scale, double ty) {
        this.scale = scale;
        this.ty = ty;
        Parser parser = new Parser();
        parser.load(resource);
        while (!parser.isEndOfFile()) {
            if (parser.getLine().startsWith("v ")) {
                addVertex(parser.expect("v "));
            }
            else if (parser.getLine().startsWith("f ")) {
                addFace(parser.expect("f "));
            }
            else {
                parser.nextLine();
            }
        }
    }
    
    private void addVertex(String[] values) {
        double x = Double.valueOf(values[1]) * scale;
        double y = Double.valueOf(values[2]) * scale;
        double z = Double.valueOf(values[3]) * scale + ty;
        Vertex vertex = new Vertex(x, y, z);
        vertices.add(vertex);
    }

    private void addFace(String[] values) {
        int i0 = Integer.valueOf(values[1].split("/")[0]) - 1;
        int i1 = Integer.valueOf(values[2].split("/")[0]) - 1;
        int i2 = Integer.valueOf(values[3].split("/")[0]) - 1;
        Face face = new Face(this, i0, i1, i2);
        faces.add(face);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void recalculatePose() {
        for (Vertex v : vertices) {
            v.calculate();
        }        
    }
    
}
