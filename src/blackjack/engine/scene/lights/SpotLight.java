package blackjack.engine.scene.lights;

import org.joml.Vector3f;

/*
 * This type of light models a light source that’s emitted from a point 
 * in space, but instead of emitting in all directions is restricted 
 * to a cone. Just include a point light reference plus light cone parameters
 */
public class SpotLight {

    private Vector3f coneDirection;
    private float cutOff;
    private float cutOffAngle;
    private PointLight pointLight;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle){
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutOffAngle = cutOffAngle;
        setCutOffAngle(cutOffAngle);
    }

    //getters & setters

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public float getCutOff() {
        return cutOff;
    }

    public float getCutOffAngle() {
        return cutOffAngle;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }
    public void setConeDirection(float x, float y ,float z) {
        coneDirection.set(x, y, z);
    }

    public void setCutOffAngle(float cutOffAngle) {
        this.cutOffAngle = cutOffAngle;
        cutOff = (float) Math.cos(Math.toRadians(cutOffAngle));
    }
    
    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    
}
