package sg.edu.nus.iss.vttp5_mini_project.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

import sg.edu.nus.iss.vttp5_mini_project.model.ChannelInfo;
import sg.edu.nus.iss.vttp5_mini_project.model.TopLevelCommentThread;
import sg.edu.nus.iss.vttp5_mini_project.model.VideoInfo;
import sg.edu.nus.iss.vttp5_mini_project.model.VideoStatistics;

@Service
public class YouTubeApiService {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    @Autowired
    VideoInfoService videoInfoService;

    @Autowired
    ChannelInfoService channelInfoService;

    public void listMostPopularVideos(YouTube youtube, String categoryId) {
        try {
            YouTube.Videos.List request = youtube.videos().list("snippet,contentDetails,status,statistics,player");
            VideoListResponse response = request
                    .setChart("mostPopular")
                    .setRegionCode("SG")
                    .setVideoCategoryId(categoryId)
                    .setMaxResults(NUMBER_OF_VIDEOS_RETURNED)
                    .execute();

            List<Video> responseList = response.getItems();

            if (responseList != null) {
                String searchTerm = "";
                saveVideos(youtube, responseList.iterator(), searchTerm);
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void searchVideos(YouTube youtube, String searchTerm) {
        try {
            YouTube.Search.List request = youtube.search().list("snippet");
            SearchListResponse response = request
                    .setQ(searchTerm)
                    .setType("video")
                    .setMaxResults(NUMBER_OF_VIDEOS_RETURNED)
                    .execute();

            List<SearchResult> searchResultList = response.getItems();

            if (searchResultList != null) {
                saveSearchResults(youtube, searchResultList.iterator(), searchTerm);
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void saveVideo(YouTube youtube, Video v, String query) {
        VideoInfo videoInfo = videoInfoService.getByVideoId(v.getId());

        if (videoInfo != null) {
            System.out.println("Video Already exists... ");
        } else {
            videoInfo = new VideoInfo();

            videoInfo.setKeyword(query);
            videoInfo.setDescription(v.getSnippet().getDescription());

            Date publishedDate = new Date(v.getSnippet().getPublishedAt().getValue());
            videoInfo.setPublishedDate(publishedDate);
            videoInfo.setPrettyDate(new PrettyTime().format(publishedDate));

            if (v.getKind().equals("youtube#video")) {
                Thumbnail thumbnail;
                if (null != v.getSnippet().getThumbnails().getMaxres()) {
                    thumbnail = v.getSnippet().getThumbnails().getMaxres();
                } else if (null != v.getSnippet().getThumbnails().getStandard()) {
                    thumbnail = v.getSnippet().getThumbnails().getStandard();
                } else {
                    thumbnail = v.getSnippet().getThumbnails().getHigh();
                }

                videoInfo.setVideoId(v.getId());
                videoInfo.setTitle(v.getSnippet().getTitle());
                videoInfo.setThumbnailUrl(thumbnail.getUrl());
                videoInfo.setVideoDuration(parseDuration(v.getContentDetails().getDuration()));
                videoInfo.setCategoryId(v.getSnippet().getCategoryId());
                if (v.getStatus().getEmbeddable()) {
                    videoInfo.setPlayerEmbedHtml(v.getPlayer().getEmbedHtml());
                } else {
                    videoInfo.setPlayerEmbedHtml("");
                }

                videoInfo.setChannelInfo(
                        saveChannelInfo(getChannelByChannelId(youtube, v.getSnippet().getChannelId())));

                videoInfo.setVideoStatistics(saveVideoStatistics(v));
            }
            videoInfoService.save(videoInfo);
        }
    }

    private void saveVideos(YouTube youtube, Iterator<Video> videos, String query)
            throws IOException, ParseException {

        if (!videos.hasNext()) {
            System.out.println(" There aren't any results for your list query.");
        }

        while (videos.hasNext()) {
            Video v = videos.next();
            saveVideo(youtube, v, query);
        }

    }

    private void saveSearchResults(YouTube youtube, Iterator<SearchResult> iteratorSearchResults, String query)
            throws IOException, ParseException {

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your search query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            Video v = getVideoFromVideoId(youtube, singleVideo.getId().getVideoId());
            saveVideo(youtube, v, query);

        }
    }

    private VideoStatistics saveVideoStatistics(Video v) {

        VideoStatistics statistics = new VideoStatistics();

        statistics.setVideoId(v.getId());
        statistics.setLikeCount(
                v.getStatistics().getLikeCount() != null ? v.getStatistics().getLikeCount().longValue() : 0);
        statistics.setDislikeCount(
                v.getStatistics().getDislikeCount() != null ? v.getStatistics().getDislikeCount().longValue() : 0);
        statistics.setCommentCount(
                v.getStatistics().getCommentCount() != null ? v.getStatistics().getCommentCount().longValue() : 0);
        statistics.setViewCount(
                v.getStatistics().getViewCount() != null ? v.getStatistics().getViewCount().longValue() : 0);

        return statistics;
    }

    private ChannelInfo saveChannelInfo(Channel c) {
        ChannelInfo youtubeChannelInfo = new ChannelInfo();
        youtubeChannelInfo.setChannelId(c.getId());
        youtubeChannelInfo.setName(c.getSnippet().getTitle());
        youtubeChannelInfo.setThumbnailUrl(c.getSnippet().getThumbnails().getHigh().getUrl());
        youtubeChannelInfo.setSubscriptionCount(c.getStatistics().getSubscriberCount().longValue());

        ChannelInfo channelInfo = channelInfoService.get(c.getId());

        if (channelInfo == null) {
            channelInfoService.save(youtubeChannelInfo);
        } else {
            return channelInfo;
        }

        return youtubeChannelInfo;
    }

    public Map<String, String> getAllCategoriesMap(YouTube youtube) {
        Map<String, String> categoriesMap = new LinkedHashMap<>();
        for (String categoryId : videoInfoService.getAllCategoryIds()) {
            categoriesMap.put(categoryId, getCategoryTitleById(youtube, categoryId));
        }
        return categoriesMap;
    }

    private String getCategoryTitleById(YouTube youtube, String categoryId) {
        YouTube.VideoCategories.List request;
        try {
            request = youtube.videoCategories()
                    .list("snippet");
            VideoCategoryListResponse response = request
                    .setId(categoryId)
                    // .setRegionCode("SG")
                    .execute();
            return response.getItems().get(0).getSnippet().getTitle();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Video getVideoFromVideoId(YouTube youtube, String id) {
        YouTube.Videos.List list;
        try {
            list = youtube.videos().list("snippet,contentDetails,status,statistics,player");
            list.setId(id);
            Video v = list.execute().getItems().get(0);

            return v;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<TopLevelCommentThread> getCommentThreadsFromVideoId(YouTube youtube, String videoId) {
        YouTube.CommentThreads.List request;
        try {
            request = youtube.commentThreads()
                    .list("snippet,replies");
            CommentThreadListResponse response = request
                    .setVideoId(videoId)
                    .execute();

            List<TopLevelCommentThread> topLevelCommentThreadList = new ArrayList<>();

            Iterator<CommentThread> commentThreadIterator = response.getItems().iterator();
            if (!commentThreadIterator.hasNext()) {
                System.out.println(" There aren't any results for your list query.");
            }

            while (commentThreadIterator.hasNext()) {
                CommentThread commentThread = commentThreadIterator.next();

                TopLevelCommentThread topLevelCommentThread = new TopLevelCommentThread();
                topLevelCommentThread.setTopLevelComment(
                        commentThread.getSnippet().getTopLevelComment().getSnippet().getTextOriginal());
                topLevelCommentThread.setAuthorName(
                        commentThread.getSnippet().getTopLevelComment().getSnippet().getAuthorDisplayName());
                topLevelCommentThread.setAuthorImageUrl(
                        commentThread.getSnippet().getTopLevelComment().getSnippet().getAuthorProfileImageUrl());

                topLevelCommentThreadList.add(topLevelCommentThread);
            }
            return topLevelCommentThreadList;
            
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error when retrieving comments: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            throw new RuntimeException(e);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Channel getChannelByChannelId(YouTube youtube, String channelId) {
        YouTube.Channels.List channels;
        try {
            channels = youtube.channels().list("snippet, statistics");
            channels.setId(channelId);
            ChannelListResponse channelResponse = channels.execute();
            Channel c = channelResponse.getItems().get(0);

            return c;
        } catch (IOException e) {
            System.err.println("IOException: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getUserPictureFromUserinfo(Userinfo userinfo) {
        String userPicture;
        if (null != userinfo)
            userPicture = userinfo.getPicture();
        else
            userPicture = "/images/default.jpg";
        return userPicture;
    }

    private String parseDuration(String durationISO) {
        Duration duration = Duration.parse(durationISO);
        long HH = duration.toHours();
        long MM = duration.toMinutesPart();
        long SS = duration.toSecondsPart();
        if (HH > 0) {
            return String.format("%02d:%02d:%02d", HH, MM, SS);
        } else {
            return String.format("%02d:%02d", MM, SS);
        }
    }

}
