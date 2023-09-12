package com.dawn.libcamera;

import android.content.Context;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.dawn.beauty.utils.Constant;

import java.util.Objects;

public class CombineUtil {
    public static void combineGif(Context context){
        String filePath = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/record/";


//        String command = "-y -f -i color=size=360*720:duration=10 -i " + filePath + "1.mp4 -i " + filePath + "2.mp4 -i " + filePath + "3.mp4 -i " + filePath + "4.mp4 -i " + filePath + "bg.png " +
//                " -filter_complex \"[0:v][1:v]overlay=50:87[video1]; [video1][2:v]overlay=196:107[video2]; [video2][3:v]overlay=31:267[video3]; [video3][4:v]overlay=182:318[video4]; [video4][5:v]overlay=0:0[output]\" -map '[output]' -c:v libx264 -t 2 " + filePath + "combine.mp4";

//        String command = "-y quiet -f lavfi -i color=size=360*720:duration=10 -i " + filePath + "1.mp4 -i " + filePath + "2.mp4 -i " + filePath + "3.mp4 -i " + filePath + "4.mp4 -i " + filePath + "bg.png " +
//                " -filter_complex \"[0:v][1:v]overlay=50:87[video1]; [video1][2:v]overlay=196:107[video2]; [video2][3:v]overlay=31:267[video3]; [video3][4:v]overlay=182:318[video4]; [video4][5:v]overlay=0:0[output]\" -map '[output]' -c:v libx264 -t 2 " + filePath + "combine.mp4";
        String[] command = {"-y", "-f", "lavfi", "-i", "color=size=1920*1080:duration=10",
                "-i", filePath + "1.mp4", "-i", filePath + "2.mp4", "-i", filePath + "3.mp4", "-i", filePath + "4.mp4",
                "-i", filePath + "bg.png",
                "-filter_complex",
                "[0:v][1:v]overlay=50:87[video1]; [video1][2:v]overlay=196:107[video2]; [video2][3:v]overlay=31:267[video3]; [video3][4:v]overlay=182:318[video4]; [video4][5:v]overlay=0:0[output]",
                "-map", "[output]", "-t", "2", filePath + "combine.mp4"};
        int rc = FFmpeg.execute(command);
        Config.printLastCommandOutput(Log.INFO);
        Log.i("dawn", command.toString());
        Log.i("dawn", String.format("Command execution %s.", (rc == 0?"completed successfully":"failed with rc=" + rc)));
    }
}
