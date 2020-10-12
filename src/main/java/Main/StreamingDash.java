package Main;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StreamingDash extends Streaming {
    static Logger logger = LogManager.getLogger(StreamingDash.class);

    public StreamingDash(String ffmpegpath, String sourceFile, String streamingRoot){
        super(ffmpegpath, sourceFile , streamingRoot);
        start();
    }


    @Override
    protected String getDestination() {
        Path streamingDestinationPath = Paths.get(streamingRoot, fileNameWithoutExtension,  formatFolder, fileNameWithoutExtension+".mpd");
        return streamingDestinationPath.toString();
    }

    public StreamingDash(JsonObject input) {
        super(input);
    }

    @Override
    protected void createCommand(String ffmpgfilePath, String sourceFilePath,  ArrayList<String> command, String destination) {
        command.add(ffmpgfilePath);
        command.add("-i");
        command.add(sourceFilePath);
        command.add("-map");
        command.add("0");
        command.add("-map");
        command.add("0");
        command.add("-c:a");
        command.add("copy");
        command.add("-c:v");
        command.add("libx264");
        command.add("-b:v:0");
        command.add("800k");
        command.add("-b:v:1");
        command.add("300k");
        command.add("-s:v:1");
        command.add("320x170");
        command.add("-profile:v:1");
        command.add("baseline");
        command.add("-profile:v:0");
        command.add("main");

        command.add("-bf");
        command.add("1");

        command.add("-keyint_min");
        command.add("120");

        command.add("-g");
        command.add("120");

        command.add("-sc_threshold");
        command.add("0");


        command.add("-b_strategy");
        command.add("0");


        command.add("-ar:a:1");
        command.add("22050");
        command.add("-use_timeline");
        command.add("1");
        command.add("-use_template");
        command.add("1");
        command.add("-b_strategy");
        command.add("0");

        command.add("-ar:a:1");
        command.add("22050");

        command.add("-use_timeline");
        command.add("1");
        command.add("-use_template");
        command.add("1");
        command.add("-window_size");
        command.add("5");

        //DASH-templated name to used for the initialization segment. Default is "init-stream$RepresentationID$.$ext$". "$ext$" is replaced with the file name extension specific for the segment format.
        command.add("-init_seg_name");
        command.add(fileNameWithoutExtension+"init-$RepresentationID$.$ext$");

        //media_seg_name segment_name
        //DASH-templated name to used for the media segments. Default is "chunk-stream$RepresentationID$-$Number%05d$.$ext$". "$ext$" is replaced with the file name extension specific for the segment format.

        command.add("-media_seg_name");
        command.add(fileNameWithoutExtension+"-chunk-$RepresentationID$-$Number%05d$.$ext$");

        command.add("-adaptation_sets");
        command.add("\"id=0,streams=v id=1,streams=a\"");

        command.add("-f");
        command.add("dash");
        command.add(destination);

        String listString = String.join(" ", command);
        System.out.println("Command to execute:\n" + listString);
    }

}
