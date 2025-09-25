package blackjack.engine.scene;

import org.joml.Matrix4f;

import blackjack.engine.Consts;

public class Projection {


    private Matrix4f projMatrix;

    public Projection(int width, int height){
        
        projMatrix = new Matrix4f();
        updateProjMatrix(width, height);

    }

    public void updateProjMatrix(int width, int height){
        float aspecRatio = (float) width / (float) height;
        projMatrix.setPerspective(Consts.FOV, aspecRatio, Consts.Z_NEAR, Consts.Z_FAR);
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

}
