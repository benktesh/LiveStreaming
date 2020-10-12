package Main;

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class Streaming {
    static Logger logger = LogManager.getLogger(Streaming.class);

    String sourceFile="C:\\Dev\\FFMPEG\\A2.mp4";
    String ffmpegExePath = "C:\\Dev\\FfMpeg\\windows\\ffmpeg"; //path to exe;
    String streamingDestinaionPath = "C:\\Dev\\FFMpeg";
    String streamingRoot = "C:\\Dev\\FFMpeg";
    String fileNameWithoutExtension = "";

    public String formatFolder = "";

    public Streaming( JsonObject input){
        String ffmpegpath = input.getString("ffmpegPath");
        String sourceFile =input.getString("sourceFile");
        String streamingRoot = input.getString("streamingRoot");
        this.sourceFile = sourceFile;
        this.ffmpegExePath = ffmpegpath;
        this.streamingRoot = streamingRoot;
        this.fileNameWithoutExtension = FilenameUtils.getBaseName(sourceFile);
    }

    public Streaming( String ffmpegpath, String sourceFile , String streamingRoot){
        this.sourceFile = sourceFile;
        this.ffmpegExePath = ffmpegpath;
        this.streamingRoot = streamingRoot;
        this.fileNameWithoutExtension = FilenameUtils.getBaseName(sourceFile);
        this.formatFolder = this.getClass().getSimpleName();
    }


    public void start(){
        ArrayList<String> command = new ArrayList<>();

        Path streaming = Paths.get(streamingRoot, fileNameWithoutExtension, formatFolder);
        File directory = new File( new File(streaming.toString()).getParent());
        if(!directory.exists()){
            logger.info("Creating directory");
            directory.mkdir();
        }


        directory = new File(new File(getDestination()).getParent());
        if(!directory.exists()){
            logger.info("Creating directory");
            directory.mkdir();
        }


        System.out.println("Starting the process \n Source: " + sourceFile.toString() + "\n" + "Destination: "+ getDestination());
        try {
            createCommand(ffmpegExePath, sourceFile, command, getDestination());
            executeFfmpeg(command, getDestination());
        }
        catch(Exception e){
            //if exception delete the directory
            logger.error(e.getMessage().toString() + " removing the directory");
            File removeDirectory = new File(new File(getDestination()).getParent());
            try {
                FileUtils.deleteDirectory(removeDirectory);
            }
            catch(Exception ee){
                logger.error("Could not remove the directory. "+ee.toString());
            }
        }

    }
    protected String getDestination(){
        Path streamingDestinationPath = Paths.get(streamingRoot, fileNameWithoutExtension,  formatFolder);
        return streamingDestinationPath.toString();
    }
    protected abstract void createCommand(String ffmpegExePath, String sourceFile, ArrayList<String> command, String streamingDestination);
    protected void executeFfmpeg(ArrayList<String> command, String sourceFile) throws IOException {
        String line = null , errl = null;
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(sourceFile).getParentFile());
        Process proc;
        BufferedReader bufferedErrorStream = null;
        BufferedReader bufferedInputStream = null;
        
        try {
            proc = pb.start();
            InputStreamReader errorStreamReader = new InputStreamReader(proc.getErrorStream());
            bufferedErrorStream = new BufferedReader(errorStreamReader);
            InputStreamReader inputStreamReader = new InputStreamReader(proc.getInputStream());
            bufferedInputStream = new BufferedReader(inputStreamReader);
            String buffer = "";

            while (((line = bufferedErrorStream.readLine()) != null) || ((errl = bufferedInputStream.readLine()) != null)) {
                buffer += "[STD]" + line + "\n";
                if (errl != null) {
                    buffer += "[ERR]" + errl + "\n";
                }
            }
            bufferedErrorStream.close();
            bufferedInputStream.close();
            System.out.println(buffer);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (bufferedErrorStream != null) bufferedErrorStream.close();
            if (bufferedInputStream != null) bufferedInputStream.close();

        }
    }

}
