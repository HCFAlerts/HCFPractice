package me.hcfalerts.practice.utilities.license;

import lombok.Getter;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.rank.IRank;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

@Getter
public class License {

    private final String license;
    private final String ip;
    private final Plugin pluginClass;
    private final String apiKey;
    private final String server;
    private LicenseErrorType errorType;

    private String buyer;
    private String generateDate;

    private boolean valid = false;
    private final boolean debug = false;

    public License(String license, String ip, Plugin pluginClass) {
        this.server = "http://license.pandacommunity.org:9999";
        this.license = license;
        this.ip = ip;
        this.pluginClass = pluginClass;
        this.apiKey = "TOPssjRDkpFMyuHYBFCfYtOczILxDgWZIIUttDGCPphMPZqnoZ";
    }

    public List<String> getMessages(IRank rank) {
        return Arrays.asList(
                CC.CHAT_BAR,
                "<color>Your license is " + (valid ? "&avalid" : "&cinvalid") + "<color>.",
                "",
                "<color>&l" + pluginClass.getDescription().getName(),
                " <color>Author&7: &f" + pluginClass.getDescription().getAuthors(),
                " <color>Version&7: &f" + pluginClass.getDescription().getVersion(),
                " <color>Rank System&7: &f" + rank.getRankSystem(),
                valid ? "<color>Thanks for purchase in Panda Development." : " <color>Reason: &f" + errorType.toString(),
                "<color>https://discord.pandacommunity.org/development",
                CC.CHAT_BAR
        );
    }

    public void sendMessage(String color, IRank rank) {
        for (String message : getMessages(rank)) {
            Bukkit.getLogger().info(message.replace("<color>", color));
        }
    }

    public void check() {
        try {
            String pluginName = pluginClass.getDescription().getName();
            URL url;
            try {
                url = new URL(server + "/api/check/request/licenses?keyAPI=" + apiKey + "&license=" + license + "&plugin=" + pluginName + "&ip=" + ip);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                valid = false;
                return;
            }

            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = in.readLine()) != null) {
                builder.append(line);
            }

            String response = builder.toString();

            if (response.equalsIgnoreCase("API_KEY_NOT_VALID")) {
                errorType = LicenseErrorType.API_KEY_NOT_VALID;
            }
            else if (response.equalsIgnoreCase("INVALID_LICENSE")) {
                errorType = LicenseErrorType.INVALID_LICENSE;
            }
            else if (response.equalsIgnoreCase("INVALID_PLUGIN_NAME")) {
                errorType = LicenseErrorType.INVALID_PLUGIN_NAME;
            }
            else if (response.equalsIgnoreCase("INVALID_IP")) {
                errorType = LicenseErrorType.INVALID_IP;
            }
            else if (response.equalsIgnoreCase("INVALID_ID")) {
                errorType = LicenseErrorType.MAXIMUM_IP_REACHED;
            }
            else if (response.equalsIgnoreCase("EXPIRED")) {
                errorType = LicenseErrorType.EXPIRED;
            }
            else if (response.startsWith("VALID")) {
                errorType = LicenseErrorType.VALID;
                valid = true;
                String[] split = response.split(";");
                this.buyer = split[1];
                this.generateDate = split[3];
            }
            else {
                errorType = LicenseErrorType.PAGE_ERROR;
            }
        }
        catch (IOException e) {
            if (debug) e.printStackTrace();
            valid = false;
            errorType = LicenseErrorType.PAGE_ERROR;
        }
    }
}
