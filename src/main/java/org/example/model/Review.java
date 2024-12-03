package org.example.model;

import java.time.LocalDateTime;

/**
 * Represents a review made by a user for a specific document.
 * Contains details such as rating, comment, and timestamp of the review.
 */
public class Review {

    private int id;
    private String documentId;
    private String userId;
    private String userName; // New field
    private int rating;
    private String comment;
    private LocalDateTime timestamp;

    /**
     * Gets the unique identifier of the review.
     *
     * @return the review ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the review.
     *
     * @param id the review ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the document being reviewed.
     *
     * @return the document ID.
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the ID of the document being reviewed.
     *
     * @param documentId the document ID.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Gets the ID of the user who submitted the review.
     *
     * @return the user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who submitted the review.
     *
     * @param userId the user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the name of the user who submitted the review.
     *
     * @return the user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the user who submitted the review.
     *
     * @param userName the user name.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the rating given by the user in the review.
     *
     * @return the rating value.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating given by the user in the review.
     *
     * @param rating the rating value.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Gets the comment left by the user in the review.
     *
     * @return the review comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment left by the user in the review.
     *
     * @param comment the review comment.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the timestamp when the review was submitted.
     *
     * @return the timestamp of the review.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the review was submitted.
     *
     * @param timestamp the timestamp of the review.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
