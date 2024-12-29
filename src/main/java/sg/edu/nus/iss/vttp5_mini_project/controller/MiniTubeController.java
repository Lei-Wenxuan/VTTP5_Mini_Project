package sg.edu.nus.iss.vttp5_mini_project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5_mini_project.auth.Auth;
import sg.edu.nus.iss.vttp5_mini_project.model.CommentInfo;
import sg.edu.nus.iss.vttp5_mini_project.model.SearchInfo;
import sg.edu.nus.iss.vttp5_mini_project.model.TopLevelCommentThread;
import sg.edu.nus.iss.vttp5_mini_project.model.VideoInfo;
import sg.edu.nus.iss.vttp5_mini_project.service.AuthService;
import sg.edu.nus.iss.vttp5_mini_project.service.ChannelInfoService;
import sg.edu.nus.iss.vttp5_mini_project.service.VideoInfoService;
import sg.edu.nus.iss.vttp5_mini_project.service.YouTubeApiService;

@Controller
@RequestMapping
public class MiniTubeController {

    @Autowired
    AuthService authService;

    @Autowired
    YouTubeApiService youtubeService;

    @Autowired
    VideoInfoService videoInfoService;

    @Autowired
    ChannelInfoService channelInfoService;

    @GetMapping("/")
    public String listMostPopularVideos(HttpSession session, Model model) {
        YouTube youtube = authService.getService();
        String categoryId = "0";
        youtubeService.listMostPopularVideos(youtube, categoryId);

        List<VideoInfo> videos = videoInfoService.getByQueryTerm("");
        Map<String, String> allCategoryMap = youtubeService.getAllCategoriesMap(youtube);

        String userPicture = youtubeService.getUserPictureFromUserinfo(Auth.getUserinfoFromSession(session));

        model.addAttribute("userPicture", userPicture);
        model.addAttribute("videos", videos);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryMap", allCategoryMap);
        model.addAttribute("searchInfo", new SearchInfo());

        return "home";
    }

    @GetMapping("/filter/{categoryId}")
    public String listMostPopularVideosByCategory(@PathVariable String categoryId, HttpSession session, Model model) {
        YouTube youtube = authService.getService();
        youtubeService.listMostPopularVideos(youtube, categoryId);

        List<VideoInfo> videos = videoInfoService.getByCategoryId(categoryId);
        Map<String, String> allCategoryMap = youtubeService.getAllCategoriesMap(youtube);

        String userPicture = youtubeService.getUserPictureFromUserinfo(Auth.getUserinfoFromSession(session));

        model.addAttribute("userPicture", userPicture);
        model.addAttribute("videos", videos);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryMap", allCategoryMap);
        model.addAttribute("searchInfo", new SearchInfo());

        return "home";
    }

    @PostMapping("/results")
    public String postSearch(
            @Valid @ModelAttribute("searchInfo") SearchInfo searchInfo, BindingResult result) {

        if (result.hasErrors())
            return "results";

        return "redirect:/results?search_query=" + searchInfo.getQuery();
    }

    @GetMapping("/results")
    public String getSearchResults(
            @RequestParam(name = "search_query", required = true) String queryTerm,
            HttpSession session,
            Model model) {
        YouTube youtube = authService.getService();
        youtubeService.searchVideos(youtube, queryTerm);

        List<VideoInfo> videos = videoInfoService.getByQueryTerm(queryTerm);

        String userPicture = youtubeService.getUserPictureFromUserinfo(Auth.getUserinfoFromSession(session));

        model.addAttribute("userPicture", userPicture);
        model.addAttribute("videos", videos);
        model.addAttribute("searchInfo", new SearchInfo());

        return "results";
    }

    @GetMapping("/watch")
    public String watchVideo(
            @RequestParam(name = "v", required = true) String videoId,
            HttpSession session,
            Model model) {

        YouTube youtube = authService.getService();

        if (null == videoInfoService.getByVideoId(videoId)) {
            youtubeService.saveVideo(youtube, youtubeService.getVideoFromVideoId(youtube, videoId), videoId);
        }
        VideoInfo v = videoInfoService.getByVideoId(videoId);

        String queryTerm = v.getTitle().replaceAll("[^\\x00-\\x7F]", "");
        youtubeService.searchVideos(youtube, queryTerm);
        List<VideoInfo> videos = videoInfoService.getByQueryTerm(queryTerm);

        String userPicture = youtubeService.getUserPictureFromUserinfo(Auth.getUserinfoFromSession(session));

        List<TopLevelCommentThread> topLevelCommentThreadList = new ArrayList<>();
        try {
            topLevelCommentThreadList = youtubeService.getCommentThreadsFromVideoId(youtube, videoId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        model.addAttribute("video", v);
        model.addAttribute("videos", videos);
        model.addAttribute("userPicture", userPicture);
        model.addAttribute("commentThreads", topLevelCommentThreadList);
        model.addAttribute("searchInfo", new SearchInfo());
        model.addAttribute("commentInfo", new CommentInfo());

        return "watch";
    }

    @PostMapping("/comment")
    public String postComment(
            @Valid @ModelAttribute("commentInfo") CommentInfo commentInfo, BindingResult result, HttpSession session)
            throws IOException {

        if (result.hasErrors())
            return "watch";

        Credential credentials = Auth.getCredentialsFromSession(session);

        if (null == credentials)
            return "redirect:/authorise";

        YouTube youtube = authService.getService(credentials);

        CommentThread commentThread = new CommentThread();
        CommentThreadSnippet snippet = new CommentThreadSnippet();
        Comment topLevelComment = new Comment();
        CommentSnippet commentSnippet = new CommentSnippet();
        commentSnippet.setTextOriginal(commentInfo.getComment());
        topLevelComment.setSnippet(commentSnippet);
        snippet.setTopLevelComment(topLevelComment);
        snippet.setVideoId(commentInfo.getVideoId());
        commentThread.setSnippet(snippet);

        YouTube.CommentThreads.Insert request = youtube.commentThreads()
                .insert("snippet", commentThread);
        try {
            CommentThread response = request.execute();
            System.out.println("Comment posted: " + response);
        } catch (Exception e) {
            System.err.println("Comment not posted");
        }

        return "redirect:/watch?v=" + commentInfo.getVideoId();
    }

    @GetMapping("/subscribe/{channelId}")
    public String subscribeToChannel(@PathVariable String channelId, HttpSession session) throws IOException {
        Credential credentials = Auth.getCredentialsFromSession(session);

        if (null == credentials)
            return "redirect:/authorise";

        YouTube youtube = authService.getService(credentials);

        Subscription subscription = new Subscription();

        SubscriptionSnippet snippet = new SubscriptionSnippet();
        ResourceId resourceId = new ResourceId();
        resourceId.setChannelId(channelId);
        resourceId.setKind("youtube#channel");
        snippet.setResourceId(resourceId);
        subscription.setSnippet(snippet);

        YouTube.Subscriptions.Insert request = youtube.subscriptions()
                .insert("snippet", subscription);
        Subscription response = request.execute();

        System.out.println(response);

        return "redirect:/";
    }

}
