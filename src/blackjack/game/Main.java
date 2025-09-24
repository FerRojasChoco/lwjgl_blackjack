package blackjack.game;

import blackjack.engine.*;
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
        throw new UnsupportedOperationException("Unimplemented method 'cleanup'");
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'input'");
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
