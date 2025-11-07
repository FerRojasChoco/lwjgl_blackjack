package blackjack.engine;

import org.joml.Vector4f;

public class Consts {


    /*~~~ ENGINE/WINDOW RELATED ~~~*/
    public static final int TARGET_UPS = 60;
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    /*~~~ CAMERA VIEW AND PROJECTION RELATED ~~~*/
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.f;

    /*~~~ TEXTURE RELATED ~~~*/
    public static final String DEFAULT_TEXTURE = "resources/models/default/stonewall.png";
    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    
    /*~~~ LIGHT RELATED ~~~*/
    public static final int MAX_POINT_LIGHTS = 5;
    public static final int MAX_SPOT_LIGHTS = 5;
    
    /*~~~ MOUSE RELATED ~~~ */
    public static final float MOUSE_SENS = 0.05f;
    public static final float MOVEMENT_SPEED = 0.001f;
}
