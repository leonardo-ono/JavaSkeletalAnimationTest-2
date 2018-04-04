package view;

import br.ol.mesh.Face;
import br.ol.mesh.Mesh;
import bvh.Bone;
import bvh.Skeleton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import math.Mat4;
import math.Vec4;
import skeletal_animation.Vertex;
import skeletal_animation.WeightLoader;

/**
 *
 * @author leonardo
 */
public class View extends JPanel implements MouseMotionListener, KeyListener {
    
    public static double SCALE = 120;

    private Vec4 mouse = new Vec4();
    
    private Mesh mesh;
    private Skeleton skeleton;
    private List<Bone> bonesList = new ArrayList<Bone>();
    private final Polygon polygon = new Polygon();
    
    private final Mat4 modelMatrix = new Mat4();
    private final Mat4 modelMatrix2 = new Mat4();
    private final Vec4 screenPosition = new Vec4();
    
    private final Stroke stroke = new BasicStroke(3);
    
    private boolean drawMesh = true;
    private boolean drawSkeleton = true;
    private boolean drawVertices = false;
    private boolean useTPosition = false;
    
    public View() {
        addKeyListener(this);
        addMouseMotionListener(this);
        loadTest();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
                repaint();
            }
        }, 100, 1000 / 30);
    }
    
    private void loadTest() {
        SCALE = 20;
        mesh = new Mesh("test.obj", SCALE * 0.05, 0);
        
        //skeleton = new Skeleton("test.bvh");
        skeleton = new Skeleton("69_10.bvh");
        
        skeleton.getRootNode().fillNodesList(bonesList);
        skeleton.getRootNode().calculateInverseTransformation();
        WeightLoader.load("test.w", mesh, skeleton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g2d.translate(getWidth() / 2, getHeight() / 2);
        if (drawMesh) {
            drawFaces(g2d);
        }
        if (drawSkeleton) {
            drawBones(g2d);
        }
        if (drawVertices) {
            drawVertices(g2d);
        }
    }

    private double frame = 0;
    
    private void update() {
        modelMatrix.setTranslation(0, -10, -600);
        modelMatrix2.setRotationX(Math.toRadians(15));
        modelMatrix.multiply(modelMatrix2);
        modelMatrix2.setRotationY(Math.toRadians(-30));
        modelMatrix.multiply(modelMatrix2);
        
        
        if (useTPosition) {
            skeleton.setPose(-1); // T pose
        }
        else {
            skeleton.setPose((int) frame);
        }
        
        mesh.recalculatePose();
        frame += (1000 / 30) * 0.1;
        if (frame > skeleton.getFrameSize()) {
            frame = 0;
        }
    }
    
    public static final double DIST = 300;
    
    private void drawBones(Graphics2D g) {
        Stroke originalStroke = g.getStroke();
        g.setStroke(stroke);
        for (Bone parent : bonesList) {
            screenPosition.set(parent.getPosition());
            modelMatrix.multiply(screenPosition);
            int x1 = (int) ((screenPosition.getX()/screenPosition.getZ()) * DIST * SCALE);
            int y1 = (int) ((screenPosition.getY()/screenPosition.getZ()) * DIST * SCALE);
            for (Bone child : parent.getChildren()) {
            screenPosition.set(child.getPosition());
            modelMatrix.multiply(screenPosition);
                int x2 = (int) ((screenPosition.getX()/screenPosition.getZ()) * DIST * SCALE);
                int y2 = (int) ((screenPosition.getY()/screenPosition.getZ()) * DIST * SCALE);
                g.setColor(Color.BLACK);
                g.drawLine(x1, y1, x2, y2);
                g.setColor(Color.RED);
                g.fillOval((int) (x2 - 3), (int) (y2 - 3), 6, 6);
            }
            g.setColor(Color.RED);
            g.fillOval((int) (x1 - 3), (int) (y1 - 3), 6, 6);
        }
        g.setStroke(originalStroke);
    }
    
    
    private void drawFaces(Graphics2D g) {
        g.setColor(Color.BLUE);
        for (Face face : mesh.getFaces()) {
            polygon.reset();
            for (int i = 0; i < 3; i++) {
                Vertex v = face.getVertex(i);
                screenPosition.set(v.positionTransf);
                modelMatrix.multiply(screenPosition);
                
                int x = (int) ((screenPosition.getX()/screenPosition.getZ()) * DIST  * SCALE);
                int y = (int) ((screenPosition.getY()/screenPosition.getZ()) * DIST  * SCALE);
                polygon.addPoint(x, y);
            }
            g.draw(polygon);
        }
    }
    
    private void drawVertices(Graphics2D g) {
        for (Vertex v : mesh.getVertices()) {
            g.setColor(Color.BLUE);
            screenPosition.set(v.positionTransf);
            modelMatrix.multiply(screenPosition);
            
            int x = (int) ((screenPosition.getX()/screenPosition.getZ()) * DIST  * SCALE);
            int y = (int) ((screenPosition.getY()/screenPosition.getZ()) * DIST  * SCALE);
            g.fillOval((int) (x - 3), (int) (y - 3), 6, 6);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouse.set(e.getX(), e.getY(), 0, 1);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse.set(e.getX(), e.getY(), 0, 1);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_M:
                drawMesh = !drawMesh;
                break;
            case KeyEvent.VK_S:
                drawSkeleton = !drawSkeleton;
                break;
            case KeyEvent.VK_V:
                drawVertices = !drawVertices;
                break;
            case KeyEvent.VK_T:
                useTPosition = !useTPosition;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
