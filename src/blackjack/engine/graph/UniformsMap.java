package blackjack.engine.graph;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.*;

import static org.lwjgl.opengl.GL20.*;

//Uniforms are global GLSL variables that shaders can use and that we will employ to pass data that is common to all elements or to a model

public class UniformsMap {

    private int programID;
    private Map<String, Integer> uniforms;

    public UniformsMap(int programID){

        this.programID = programID;
        uniforms = new HashMap<>();

    }

    //uniform creation is independent on the data type associated to it that is the reason of why there are many functions but receive different types of values

    public void createUniform(String uniformName){

        int uniformLocation = glGetUniformLocation(programID, uniformName);

        if (uniformLocation < 0){
            throw new RuntimeException("COULD NOT FIND UNIFORM: [" + uniformName + "] in shader program [" + programID + "]");
        }

        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value){

        try (MemoryStack stack = MemoryStack.stackPush()) {

            Integer location = uniforms.get(uniformName);

            if (location == null){
                throw new RuntimeException("COULD NOT FIND UNIFORM [" + uniformName + "]");
            }

            glUniformMatrix4fv(location.intValue(), false, value.get(stack.mallocFloat(16)));

        }

    }

}
