package blackjack.game;

import blackjack.engine.*;
import blackjack.engine.graph.Mesh;
import blackjack.engine.graph.Render;
import blackjack.engine.scene.Scene;

//create the Engine instance and start it up in the main method
//this class also implements app logic but is empty for now
public class Main implements IAppLogic{

    public static void main(String[] args){

        Main main = new Main();
        
        Engine gameEngine = new Engine("Blackjack LWJGL", new Window.WindowOptions(), main);

        gameEngine.start();


    

    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        
        float[] positions = new float[]{
                -0.5f, 0.5f, -1.0f,
                -0.5f, -0.5f, -1.0f,
                0.5f, -0.5f, -1.0f,
                0.5f, 0.5f, -1.0f,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
        };

        Mesh mesh = new Mesh(positions, colors, indices);
        
        scene.addMesh("quad", mesh);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        // TODO Auto-generated method stub
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        // TODO Auto-generated method stub
    }

}
