package blackjack.engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import java.util.concurrent.Callable;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final long windowHandle;
    private int height;
    private Callable<Void> resizeFunc;
    private int width;

    private MouseInput mouseInput;


    public Window(String title, WindowOptions opts, Callable<Void> resizeFunc){
        
        this.resizeFunc = resizeFunc;
        

        if(!glfwInit()){
            throw new IllegalStateException("Unable to init GLFW");
        }

        //window not visible yet, and IS resizable
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        
        //anti-aliasing
        if(opts.antiAliasing){
            glfwWindowHint(GLFW_SAMPLES, 4);
        }
        
        //set OpenGL version
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);

        //either core or compatible profeile depending on window options
        if (opts.compatibleProfile){
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } 
        else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        //set the size of the window if widht and height are given
        //if not, set screen size for the window
        if (opts.width > 0 && opts.height > 0){
            this.width = opts.width;
            this.height = opts.height;
        } 
        else {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            width = vidMode.width();
            height = vidMode.height();    
        }

        //create window and set some callbacks for when window is resized or to detect window closure (esc)
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);

        //check if this should be here, it works for my laptop hehe 
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowHandle, (vidMode.width()- width) / 2 , (vidMode.height() - height) / 2 );  

        if (windowHandle == NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetFramebufferSizeCallback(windowHandle, (_, w, h) -> resized(w, h));

        glfwSetErrorCallback((int errorCode, long msgPtr) ->
                Logger.error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr))
        );

        glfwSetKeyCallback(windowHandle, (ignore0, key, ignore1, action, _) -> {
            keyCallBack(key, action);
        });

        glfwMakeContextCurrent(windowHandle);
        
        //swap interval 0 if we want to set a target fps and disable v-sync
        //otherwise, enable v-sync
        if (opts.fps > 0){
            glfwSwapInterval(0);
        }
        else{
            glfwSwapInterval(1);
        }

        //show the window duh
        glfwShowWindow(windowHandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        
        //get the portion of the window used to render()
        glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);

        width = arrWidth[0];
        height = arrHeight[0];
        
        mouseInput = new MouseInput(windowHandle);

    }

    //key callback 
    public void keyCallBack(int key, int action){

        //close the window if escp is pressed
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
            glfwSetWindowShouldClose(windowHandle, true);
        }

    }
    
    //resize callback
    protected void resized(int width, int height){

        this.width = width;
        this.height = height;

        try {
            resizeFunc.call();
        } catch (Exception e) {
            Logger.error("Error calling resize callback", e);
        }

    }
    
    //cleaning up resources
    public void cleanup(){

        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        
        glfwTerminate();

        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null){
            callback.free();
        }

    }

    public void update(){
        glfwSwapBuffers(windowHandle);
    }

    //getters and setter
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public void pollEvents(){
        glfwPollEvents();
    }

    public boolean windowShouldClose(){
        return glfwWindowShouldClose(windowHandle);
    }


    public static class WindowOptions {

        public boolean compatibleProfile;   //controls wheter we want to use old functions from previous versions
        public boolean antiAliasing;        

        public int fps;     //target frames per second

        //desired windows size
        // public int height = Consts.HEIGHT;  
        // public int width = Consts.WIDTH;

        //default window size
        public int height = 0;
        public int width = 0;

        public int ups = Consts.TARGET_UPS; //target numbers of updates per second (inti to default)

    }
}
