Feature: Video Upload

  Scenario: Successfully upload a valid video
    Given the user "user-123" is authenticated
    When the user uploads a video file "test.mp4" with size 1024 bytes and type "video/mp4"
    Then the response status should be 202
    And the response should contain a videoId
    And the video status should be "PENDING"

  Scenario: Reject upload with unsupported format
    Given the user "user-123" is authenticated
    When the user uploads a file "document.pdf" with size 1024 bytes and type "application/pdf"
    Then the response status should be 422

  Scenario: List videos for a user
    Given the user "user-123" has previously uploaded videos
    When the user requests their video list
    Then the response status should be 200
    And the response should contain a list of videos
