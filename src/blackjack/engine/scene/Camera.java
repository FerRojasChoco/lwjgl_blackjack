package blackjack.engine.scene;

import org.joml.*;

public class Camera {

    private Vector3f direction;
    private Vector3f position;
    private Vector3f right;
    private Vector2f rotation;
    private Vector3f up;
    private Matrix4f viewMatrix;
    private Matrix4f invViewMatrix;

    public Camera() {
        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        rotation = new Vector2f();
        invViewMatrix = new Matrix4f();
    }

    
    public void addRotation(float pitch, float yaw){
        rotation.x += pitch;  // Assuming x is pitch (vertical)
        rotation.y += yaw;    // Assuming y is yaw (horizontal)

        // Clamp pitch to ±89 degrees to prevent over-rotation
        float limit = (float) java.lang.Math.toRadians(89.0);
        if (rotation.x > limit) rotation.x = limit;
        if (rotation.x < -limit) rotation.x = -limit;

        recalculate();
    }
    
    public void moveBackwards(float inc) {
        viewMatrix.positiveZ(direction).negate();
        direction.y = 0;
        direction.normalize();
        direction.mul(inc);
        position.sub(direction);
        recalculate();
    }

    public void moveDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    public void moveForward(float inc) {
        viewMatrix.positiveZ(direction).negate();
        //fixed "flying" player by assigning 0 to the vertical component
        direction.y = 0;
        direction.normalize();
        direction.mul(inc);
        position.add(direction);
        recalculate();
    }

    public void moveLeft(float inc) {
        viewMatrix.positiveX(right);
        right.y = 0;
        right.normalize();
        right.mul(inc);
        position.sub(right);
        recalculate();
    }

    public void moveRight(float inc) {
        viewMatrix.positiveX(right);
        right.y = 0;
        right.normalize();
        right.mul(inc);
        position.add(right);
        recalculate();
    }

    public void moveUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }

    private void recalculate() {
        viewMatrix.identity()
                    .rotateX(rotation.x)
                    .rotateY(rotation.y)
                    .translate(-position.x, -position.y, -position.z);
        invViewMatrix.set(viewMatrix).invert();
    }

    //getters and setters
    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getInvViewMatrix() {
        return invViewMatrix;
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