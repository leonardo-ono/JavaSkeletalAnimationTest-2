package br.ol.mesh;

import skeletal_animation.Vertex;

/**
 * Face class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Face {

    private final Mesh mesh;
    private final int[] vertexIndexes = new int[3];

    public Face(Mesh mesh, int i0, int i1, int i2) {
        this.mesh = mesh;
        vertexIndexes[0] = i0;
        vertexIndexes[1] = i1;
        vertexIndexes[2] = i2;
    }

    public int[] getVertexIndexes() {
        return vertexIndexes;
    }
    
    public Vertex getVertex(int index) {
        return mesh.getVertices().get(vertexIndexes[index]);
    }
    
}
