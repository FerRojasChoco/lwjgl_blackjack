package blackjack.engine.graph;

import org.lwjgl.opengl.GL;
import blackjack.engine.Window;
import blackjack.engine.scene.Scene;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Render {

    private SceneRender sceneRender;
    private SkyBoxRender skyBoxRender;
    private GuiRender guiRender;

    public Render(Window window) {
        GL.createCapabilities();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        sceneRender = new SceneRender();
        guiRender = new GuiRender(window);
        skyBoxRender = new SkyBoxRender();
    }

    public void cleanup() {
        sceneRender.cleanup();
        guiRender.cleanup();
    }

    public void render(Window window, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        skyBoxRender.render(scene);
        sceneRender.render(scene);
        guiRender.render(scene);
    }

    public void resize(int width, int height) {
        guiRender.resize(width, height);
    }
    
    @SuppressWarnings("unused")
    private void renderCrosshair(Window window){
        float crosshairSize = 10.0f;
        float thickness = 2.0f;

        float centerX = window.getWidth() / 2.0f;
        float centerY = window.getHeight() / 2.0f;

        glDisable(GL_DEPTH_TEST);
        
        // Switch to a Projection Matrix that maps window coordinates (0, 0 to width, height)
        // Note: We need to use glMatrixMode for fixed-function pipeline setup.
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // Set up orthographic projection (left, right, bottom, top, near, far)
        glOrtho(0.0, window.getWidth(), window.getHeight(), 0.0, -1.0, 1.0);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity(); // Reset the modelview matrix

        // 2. Draw the Crosshair
        
        // Set white color
        glColor3f(1.0f, 1.0f, 1.0f); 
        glLineWidth(thickness);

        glBegin(GL_LINES);

        // Horizontal Line
        glVertex2f(centerX - (crosshairSize / 2.0f), centerY);
        glVertex2f(centerX + (crosshairSize / 2.0f), centerY);

        // Vertical Line
        glVertex2f(centerX, centerY - (crosshairSize / 2.0f));
        glVertex2f(centerX, centerY + (crosshairSize / 2.0f));

        glEnd();

        // 3. Restore 3D State
        glEnable(GL_DEPTH_TEST);
    }
}