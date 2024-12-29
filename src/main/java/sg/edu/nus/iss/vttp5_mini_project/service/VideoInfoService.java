package sg.edu.nus.iss.vttp5_mini_project.service;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5_mini_project.constant.Constants;
import sg.edu.nus.iss.vttp5_mini_project.model.ChannelInfo;
import sg.edu.nus.iss.vttp5_mini_project.model.VideoInfo;
import sg.edu.nus.iss.vttp5_mini_project.model.VideoStatistics;
import sg.edu.nus.iss.vttp5_mini_project.repository.MapRepo;

@Service
public class VideoInfoService {

    @Autowired
    private MapRepo videoInfoRepo;

    public void save(VideoInfo videoInfo) {
        JsonObject jObject = videoInfo.toJson();
        videoInfoRepo.create(Constants.VIDEOINFO_KEY, videoInfo.getVideoId(), jObject.toString());
        videoInfoRepo.expire(Constants.VIDEOINFO_KEY, Constants.KEY_EXPIRY);
    }

    public List<VideoInfo> getAll() {
        Map<Object, Object> videoInfoObject = videoInfoRepo.getEntries(Constants.VIDEOINFO_KEY);

        List<VideoInfo> videoInfo = new ArrayList<>();

        for (Entry<Object, Object> entry : videoInfoObject.entrySet()) {
            Object obj = entry.getValue();
            videoInfo.add(jsonStringToYouTubeVideoInfo(obj.toString()));
        }

        return videoInfo;
    }

    public VideoInfo getByVideoId(String videoId) {
        Object obj = videoInfoRepo.get(Constants.VIDEOINFO_KEY, videoId);
        if (obj != null)
            return jsonStringToYouTubeVideoInfo(obj.toString());
        return null;
    }

    public List<VideoInfo> getByQueryTerm(String queryTerm) {
        return getAll().stream()
                .filter(video -> video.getKeyword().equals(queryTerm))
                .collect(Collectors.toList());
    }

    public List<VideoInfo> getByCategoryId(String categoryId) {
        return getByQueryTerm("").stream()
                .filter(video -> video.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<String> getAllCategoryIds() {
        return getAll().stream()
            .map(VideoInfo::getCategoryId)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public VideoInfo jsonStringToYouTubeVideoInfo(String jsonString) {
        JsonReader jReader = Json.createReader(new StringReader(jsonString));
        JsonObject jObject = jReader.readObject();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        Date publishedDate;
        try {
            publishedDate = sdf.parse(jObject.getString("publishedDate"));
        } catch (ParseException e) {
            System.err.println("Error with date parsing: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        VideoInfo v = new VideoInfo(
                jObject.getString("videoId"),
                jObject.getString("title"),
                jObject.getString("thumbnailUrl"),
                jObject.getString("description"),
                publishedDate,
                jObject.getString("prettyDate"),
                jObject.getString("videoDuration"),
                jObject.getString("categoryId"),
                jObject.getString("playerEmbedHtml"),
                jObject.getString("keyword"),
                jsonStringToYouTubeChannelInfo(jObject.getString("channelInfo")),
                jsonStringToYouTubeVideoStatistics(jObject.getString("videoStatistics")));

        return v;
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

    public VideoStatistics jsonStringToYouTubeVideoStatistics(String jsonString) {
        JsonReader jReader = Json.createReader(new StringReader(jsonString));
        JsonObject jObject = jReader.readObject();

        VideoStatistics s = new VideoStatistics(
                Long.valueOf(jObject.getInt("likeCount")),
                Long.valueOf(jObject.getInt("dislikeCount")),
                Long.valueOf(jObject.getInt("viewCount")),
                Long.valueOf(jObject.getInt("commentCount")),
                jObject.getString("videoId"));

        return s;
    }

}
