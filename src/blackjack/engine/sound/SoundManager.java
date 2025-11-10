package blackjack.engine.sound;

import org.joml.*;
import org.lwjgl.openal.*;
import blackjack.engine.scene.Camera;

import java.nio.*;
import java.util.*;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
 * this class has references to the SoundBuffer and SoundSource instances to track and later cleanup
 * buffers are stored in a list and sources in a map (so they can be retrieved by name)
 * 
 * the constructor opens the default device, creates the capabilities for it, and create a sound
 * context and sets it as the current one
 * 
 * some of the methods defined here are for: 
 * adding sound sources/buffers, cleanup, manage listener and sources,
 * and also a function to active a sound using its name
 */
public class SoundManager {

    private final List<SoundBuffer> soundBufferList;
    private final Map<String, SoundSource> soundSourceMap;

    private long context;
    private long device;

    private SoundListener listener;

    public SoundManager(){

        soundBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();

        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL){
            throw new IllegalStateException("Failed to open the default OpenAL device");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL){
            throw new IllegalStateException("Failed to create OpenAL contet");
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public void addSoundBuffer(SoundBuffer soundBuffer){
        this.soundBufferList.add(soundBuffer);
    }

    public void addSoundSource(String name, SoundSource soundSource){
        this.soundSourceMap.put(name, soundSource);
    }

    public void playSoundSource(String name){
        SoundSource soundSource = this.soundSourceMap.get(name);
        if (soundSource != null && !soundSource.isPlaying()){
            soundSource.play();
        }
    }

    public void removeSoundSource(String name){
        this.soundSourceMap.remove(name);
    }

    // update listener orientation given a camera position
    public void updateListenerPosition(Camera camera){
        Matrix4f viewMatrix = camera.getViewMatrix();
        listener.setPosition(camera.getPosition());

        Vector3f at = new Vector3f();
        Vector3f up = new Vector3f();

        viewMatrix.positiveZ(at).negate();
        viewMatrix.positiveY(up);

        listener.setOrientation(at, up);
    }


    //free resources
    public void cleanup(){
        soundSourceMap.values().forEach(SoundSource::cleanup);
        soundSourceMap.clear();

        soundBufferList.forEach(SoundBuffer::cleanup);
        soundBufferList.clear();

        if (context != NULL){
            alcDestroyContext(context);
        }
        
        if (device != NULL){
            alcCloseDevice(device);
        }
    }
    //getters and setters

    public SoundListener getListener() {
        return this.listener;
    }

    public SoundSource getSoundSource(String name){
        return this.soundSourceMap.get(name);
    }

    public void setAttenuationModel(int model){
        alDistanceModel(model);
    }

    public void setListener(SoundListener listener) {
        this.listener = listener;
    }



}
