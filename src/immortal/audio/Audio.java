package immortal.audio;

import javax.sound.sampled.AudioFormat;

public interface Audio {
    int BUFFER_SIZE = 1200;
    AudioFormat FORMAT = new AudioFormat(11025.0f, 16, 1, true, true);
}
