package blackjack.engine.graph;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;

import blackjack.engine.Window;
import blackjack.engine.scene.Scene;

import static org.lwjgl.opengl.GL11.*;

//for now just clears the screen

public class Render {

    private SceneRender sceneRender;
    private GuiRender guiRender;

    public Render(Window window){
        GL.createCapabilities();

        glEnable(GL20.GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        glCullFace(GL_BACK);

        sceneRender = new SceneRender();
        guiRender = new GuiRender(window);
    }

    public void cleanup(){
        sceneRender.cleanup();
        guiRender.cleanup();
    }

    public void render(Window window, Scene scene){
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        sceneRender.render(scene);
        guiRender.render(scene);
    }

    public void resize(int width, int height){
        guiRender.resize(width, height);
    }
}
