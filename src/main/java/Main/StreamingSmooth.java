package Main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class StreamingSmooth extends Streaming {
    static Logger logger = LogManager.getLogger(StreamingDash.class);

    public StreamingSmooth(String ffmpegpath, String sourceFile , String streamingRoot){
        super(ffmpegpath, sourceFile , streamingRoot);
        start();
    }

    @Override
    protected void createCommand(String ffmpgfilePath, String sourceFilePath,  ArrayList<String> command, String destination) {
        command.add(ffmpgfilePath);
        command.add("-i");
        command.add(sourceFilePath);
        command.add("-c:v");
        command.add("libx264");
        command.add("-b:v");
        command.add("2M");
        command.add("-maxrate");
        command.add("2M");
        command.add("-bufsize");
        command.add("1M");
        command.add("-crf");
        command.add("0");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-f");
        command.add("smoothstreaming");
        command.add(destination);

        String listString = String.join(" ", command);

        System.out.println("Command to execute:\n" + listString);
    }
}
