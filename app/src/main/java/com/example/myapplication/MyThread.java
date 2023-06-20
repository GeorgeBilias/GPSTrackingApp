package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MyThread extends Thread {

    String filepath;
    Handler handler;
    TextView label;
    TextView label2;
    Context context;

    private static final String SERVER_ADDRESS = "192.168.1.4"; //INSERT YOUR IP HERE
    private static final int PORT = 600;

    public MyThread(String filepath, Handler handler, TextView label, TextView label2, Context context) {
        this.filepath = filepath;
        this.handler = handler;
        this.label = label;
        this.label2 = label2;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            try {
                Log.d("myTag", "about to connect");
                Socket socket = null;
                try {
                    socket = new Socket(SERVER_ADDRESS, PORT);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Log.d("myTag", "connected");

                File file = new File(this.filepath);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.e("PRINTING,", " LINE: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("path trim", "trimmed: " + this.filepath.trim());
                Log.e("existence", "does it exist: " + file.exists());
                FileInputStream fileInput = new FileInputStream(file);
                byte[] fileData = new byte[(int) file.length()];
                fileInput.read(fileData);
                fileInput.close();
                String fileString = new String(fileData, StandardCharsets.UTF_8);

                // Write the file data to the server's output stream
                OutputStream outputStream = socket.getOutputStream();

                PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
                out.println(fileString);


                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                while (true) { // keep looping until you receive the info
                    try {
                        resultInfo route_stats;
                        route_stats = (resultInfo) in.readObject();

                        String route_stats_string;
                        route_stats_string = "Current route Activity stats: \n";

                        route_stats_string += "Total distance: " + route_stats.getTotalDistance() + " km\n";
                        route_stats_string += "Average Speed: " + route_stats.getAverageSpeed() * 60 * 60 + " km/h\n";
                        route_stats_string += "Total Elevation: " + route_stats.getTotalelevation() + " m\n";
                        route_stats_string += ("Total Time: " + route_stats.getTotalTime() + " sec");

                        Intent intent = new Intent(context, MainActivity2.class);
                        intent.putExtra("route_stats", route_stats_string);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        ArrayList<resultInfo> avg_stats = (ArrayList<resultInfo>) in.readObject();

                        String user_stats;
                        user_stats = "\nUser stats: \n";
                        user_stats += "Average Exercise Time: " + avg_stats.get(0).getTotalTime() + " sec\n";
                        user_stats += "Average Distance Time: " + avg_stats.get(0).getTotalDistance() + " km\n";
                        user_stats += "Average Elevation Time: " + avg_stats.get(0).getTotalelevation() + " meters\n\n";

                        user_stats += "Global Average Stats: \n";
                        user_stats += "Average Exercise Time: " + avg_stats.get(1).getTotalTime() + " sec\n";
                        user_stats += "Average Distance: " + avg_stats.get(1).getTotalDistance() + " km\n";
                        user_stats += "Average Elevation: " + avg_stats.get(1).getTotalelevation() + " meters\n";

                        MainActivity.rslts_tobeshown=user_stats;

                        Log.e("user stats", user_stats);

                        break;
                    } catch (ClassNotFoundException e) {
                        System.out.println(e);
                    }
                }

                socket.close();
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
