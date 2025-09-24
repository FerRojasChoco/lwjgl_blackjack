package blackjack.engine.graph;

import org.lwjgl.opengl.GL;
import blackjack.engine.Window;
import blackjack.engine.scene.Scene;

import static org.lwjgl.opengl.GL11.*;

//for now just clears the screen

public class Render {

    public Render(){
        GL.createCapabilities();
    }

    public void cleanup(){

    }

    public void render(Window window, Scene scene){
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }

}
