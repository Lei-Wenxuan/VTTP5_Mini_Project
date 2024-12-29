package sg.edu.nus.iss.vttp5_mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5_mini_project.constant.Constants;
import sg.edu.nus.iss.vttp5_mini_project.model.ChannelInfo;
import sg.edu.nus.iss.vttp5_mini_project.repository.MapRepo;

@Service
public class ChannelInfoService {
    
    @Autowired
    private MapRepo channelInfoRepo;

    public void save(ChannelInfo channelInfo) {
        JsonObject jObject = channelInfo.toJson();
        channelInfoRepo.create(Constants.CHANNELINFO_KEY, channelInfo.getChannelId(), jObject.toString());
        channelInfoRepo.expire(Constants.CHANNELINFO_KEY, Constants.KEY_EXPIRY);
    }

    public List<ChannelInfo> getAll() {
        Map<Object, Object> channelInfoObject = channelInfoRepo.getEntries(Constants.CHANNELINFO_KEY);

        List<ChannelInfo> channelInfo = new ArrayList<>();

        for (Entry<Object, Object> entry : channelInfoObject.entrySet()) {
            String stringValue = entry.getValue().toString();
            channelInfo.add(jsonStringToYouTubeChannelInfo(stringValue));
        }

        return channelInfo;
    }

    public ChannelInfo get(String searchKey) {
        Object obj = channelInfoRepo.get(Constants.CHANNELINFO_KEY, searchKey);
        if (obj != null)
            return jsonStringToYouTubeChannelInfo(obj.toString());
        return null;
    }
    
    public ChannelInfo jsonStringToYouTubeChannelInfo(String jsonString) {
        JsonReader jReader = Json.createReader(new StringReader(jsonString));
        JsonObject jObject = jReader.readObject();

        ChannelInfo i = new ChannelInfo(
                jObject.getString("channelId"),
                jObject.getString("name"),
                jObject.getString("thumbnailUrl"),
                Long.valueOf(jObject.getInt("subscriptionCount")));

        return i;
    }

}
