package br.com.fiap.application.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

public class VideoSteps {

    @Autowired
    private MockMvc mockMvc;

    private String currentUserId;
    private MvcResult lastResult;

    @Given("the user {string} is authenticated")
    public void theUserIsAuthenticated(String userId) {
        this.currentUserId = userId;
    }

    @When("the user uploads a video file {string} with size {long} bytes and type {string}")
    public void theUserUploadsAVideoFile(String filename, long size, String mimeType) throws Exception {
        MockMultipartFile file = new MockMultipartFile("video", filename, mimeType, "mock-video-content".getBytes());
        lastResult = mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .header("X-User-Id", currentUserId))
                .andReturn();
    }

    @When("the user uploads a file {string} with size {long} bytes and type {string}")
    public void theUserUploadsAFile(String filename, long size, String mimeType) throws Exception {
        MockMultipartFile file = new MockMultipartFile("video", filename, mimeType, "mock-content".getBytes());
        lastResult = mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .header("X-User-Id", currentUserId))
                .andReturn();
    }

    @Given("the user {string} has previously uploaded videos")
    public void theUserHasPreviouslyUploadedVideos(String userId) throws Exception {
        this.currentUserId = userId;
        MockMultipartFile file = new MockMultipartFile("video", "test.mp4", "video/mp4", "mock-video-content".getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(file)
                        .header("X-User-Id", userId))
                .andReturn();
    }

    @When("the user requests their video list")
    public void theUserRequestsTheirVideoList() throws Exception {
        lastResult = mockMvc.perform(get("/api/videos")
                        .header("X-User-Id", currentUserId))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(status);
    }

    @Then("the response should contain a videoId")
    public void theResponseShouldContainAVideoId() throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).contains("videoId");
    }

    @Then("the video status should be {string}")
    public void theVideoStatusShouldBe(String status) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).contains(status);
    }

    @Then("the response should contain a list of videos")
    public void theResponseShouldContainAListOfVideos() throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).startsWith("[");
    }
}

