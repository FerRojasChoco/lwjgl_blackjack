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
        
        /*      A
         *      /\
         *     /  \
         *  B /____\ C
         */
        float[] positions = new float[]{
            0.0f, 0.5f, 0.0f,   //A
            -0.5f, -0.5f, 0.0f,  //B
            0.5f, -0.5f, 0.0f   //C
        };

        Mesh mesh = new Mesh(positions, 3);
        
        scene.addMesh("triangle", mesh);
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
