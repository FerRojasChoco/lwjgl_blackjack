package blackjack.engine.scene;

import org.joml.*;

public class Camera {

    private Vector3f position;
    private Vector2f rotation;
    private Matrix4f viewMatrix;

    public Camera(){
        
        position = new Vector3f();
        rotation = new Vector2f();
        viewMatrix = new Matrix4f();
    }

    //"camera" movement methods
    public void addRotation(float pitch, float yaw){
        rotation.x += pitch;
        rotation.y += yaw;

        // Clamp pitch
        float limit = (float) java.lang.Math.toRadians(89.0);
        if (rotation.x > limit) rotation.x = limit;
        if (rotation.x < -limit) rotation.x = -limit;

        recalculate();
    }
    
    public void moveBackwards(float inc) {
        float dx = (float) java.lang.Math.sin(rotation.y) * inc;
        float dz = (float) java.lang.Math.cos(rotation.y) * inc;
        position.sub(dx, 0, -dz);
        recalculate();
    }

    public void moveDown(float inc) {
        position.y -= inc;
        recalculate();
    }

    public void moveForward(float inc) {
        float dx = (float) java.lang.Math.sin(rotation.y) * inc;
        float dz = (float) java.lang.Math.cos(rotation.y) * inc;
        position.add(dx, 0, -dz);
        recalculate();
    }

    public void moveLeft(float inc) {
        float dx = (float) java.lang.Math.sin(rotation.y - java.lang.Math.PI / 2) * inc;
        float dz = (float) java.lang.Math.cos(rotation.y - java.lang.Math.PI / 2) * inc;
        position.add(dx, 0, -dz);
        recalculate();
    }

    public void moveRight(float inc) {
        float dx = (float) java.lang.Math.sin(rotation.y + java.lang.Math.PI / 2) * inc;
        float dz = (float) java.lang.Math.cos(rotation.y + java.lang.Math.PI / 2) * inc;
        position.add(dx, 0, -dz);
        recalculate();
    }

    public void moveUp(float inc) {
        position.y += inc;
        recalculate();
    }


    private void recalculate(){
        viewMatrix.identity()
                    .rotateX(rotation.x)
                    .rotateY(rotation.y)
                    .translate(-position.x, -position.y, -position.z);
    }


    //getters and setters
    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }


    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }

    public void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }

}
