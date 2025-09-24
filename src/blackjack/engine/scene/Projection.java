package blackjack.engine.scene;

import org.joml.Matrix4f;

public class Projection {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private Matrix4f projMatrix;

    public Projection(int width, int height){
        
        projMatrix = new Matrix4f();
        updateProjMatrix(width, height);

    }

    public void updateProjMatrix(int width, int height){
        float aspecRatio = (float) width / (float) height;
        projMatrix.setPerspective(FOV, aspecRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

}
