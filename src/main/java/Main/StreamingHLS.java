package Main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StreamingHLS extends Streaming {
    static Logger logger = LogManager.getLogger(StreamingDash.class);


    public StreamingHLS(String ffmpegpath, String sourceFile , String streamingRoot){
        super(ffmpegpath, sourceFile , streamingRoot);
        start();
    }

    @Override
    protected String getDestination() {
        Path streamingDestinationPath = Paths.get(streamingRoot, fileNameWithoutExtension,  formatFolder, fileNameWithoutExtension+".m3u8");
        return streamingDestinationPath.toString();
    }


    @Override
    protected void createCommand(String ffmpgfilePath, String sourceFilePath,  ArrayList<String> command, String destination) {
            String segmentTime = "10";
            int index = destination.lastIndexOf(".");
            String segmentTemplate = String.format("%s%%03d.ts",destination);
            if (index >= 0) {
                segmentTemplate = String.format("%s%%03d.ts",destination.substring(0, index));
            }
            command.add(ffmpgfilePath);
            command.add("-hide_banner");
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
            command.add("-f");
            command.add("ssegment");
            command.add("-segment_list");
            command.add(destination); //.m3u8
            command.add("-segment_list_flags");
            command.add("+live");
            command.add("-segment_time");
            command.add(String.valueOf(segmentTime));
            command.add(segmentTemplate); //.ts
            System.out.println("Command to execute:\n" + command.toString());
        }
}
