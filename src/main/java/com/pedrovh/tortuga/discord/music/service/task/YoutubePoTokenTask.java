package com.pedrovh.tortuga.discord.music.service.task;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.scheduler.Task;
import dev.lavalink.youtube.clients.Web;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

@Slf4j
@Task(initialDelay = "0", period = "12", unit = "HOURS")
public class YoutubePoTokenTask implements Runnable {

    @Override
    public void run() {
        try {
            String[] output = readPoTokenFile();
            Web.setPoTokenAndVisitorData(output[1], output[0]);
        } catch (FileNotFoundException e) {
            log.error("Error retrieving PoToken info...");
        }
    }

    public static String[] readPoTokenFile() throws FileNotFoundException {
        String[] output = new String[2];
        Scanner in = new Scanner(new FileReader(DiscordResource.get("potoken.path", "token")));
        while(in.hasNextLine()) {
            String line = in.nextLine();
            if (line.contains("visitor_data")) output[0] = line.split(": ")[1];
            else if (line.contains("po_token")) output[1] = line.split(": ")[1];
        }
        in.close();
        return output;
    }

}
