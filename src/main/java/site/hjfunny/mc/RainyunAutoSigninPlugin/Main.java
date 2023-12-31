import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends JavaPlugin implements CommandExecutor {

    private static final String API_URL_REWARD_TASKS = "https://api.v2.rainyun.com/user/reward/tasks";
    private static final String API_URL_POINT_RENEW = "https://api.v2.rainyun.com/product/point_renew";

    @Override
    public void onEnable() {
        getLogger().info("雨云签到插件 已启用！");
        getCommand("rsi").setExecutor(this);
        getCommand("rrn").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("雨云签到插件 已禁用！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rsi")) {
            if (args.length < 1) {
                sender.sendMessage("用法: /rsi <XAPIKEY>");
                return false;
            }

            String apiKey = args[0];
            String taskName = "每日签到";

            // Create the JSON payload
            JSONObject payload = new JSONObject();
            payload.put("task_name", taskName);

            // Send the POST request
            try {
                URL url = new URL(API_URL_REWARD_TASKS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("x-api-key", apiKey);
                connection.setRequestProperty("User-Agent", "Rainyun-AutoSignin/2.0 (https://codezhangborui.eu.org/2023/06/rainyun-auto-python-scripts/)");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(payload.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    sender.sendMessage("成功发送了每日签到请求！");
                } else {
                    sender.sendMessage("无法发送此次请求，错误代码：" + responseCode + "（如果是五位数则可能表示已完成签到）");
                }

                connection.disconnect();
            } catch (IOException e) {
                sender.sendMessage("处理过程中发生了错误，请查看后台并反馈给作者。");
                e.printStackTrace();
            }

            return true;
        } else if (command.getName().equalsIgnoreCase("rrn")) {
            if (args.length < 2) {
                sender.sendMessage("用法: /rrn <产品ID> <XAPIKEY>");
                return false;
            }

            int productId = Integer.parseInt(args[0]);
            String apiKey = args[1];
            int durationDay = 1;
            String productType = "rgs";

            // Create the JSON payload
            JSONObject payload = new JSONObject();
            payload.put("duration_day", durationDay);
            payload.put("product_id", productId);
            payload.put("product_type", productType);

            // Send the POST request
            try {
                URL url = new URL(API_URL_POINT_RENEW);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("x-api-key", apiKey);
                connection.setRequestProperty("User-Agent", "Rainyun-AutoSignin/2.0 (https://codezhangborui.eu.org/2023/06/rainyun-auto-python-scripts/)");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(payload.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    sender.sendMessage("成功发送了产品续期一天请求！");
                } else {
                    sender.sendMessage("无法发送此次请求，错误代码：" + responseCode + "（如果是五位数，请查看您的服务器离到期是否小于7天，或者积分是否充足）");
                }

                connection.disconnect();
            } catch (IOException e) {
                sender.sendMessage("处理过程中发生了错误，请查看后台并反馈给作者。");
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }
}
