package com.example.smishingdetectionapp.data.repository

import com.example.smishingdetectionapp.community.CommunityComment
import com.example.smishingdetectionapp.community.CommunityPost
import com.example.smishingdetectionapp.community.CommunityReportedNumber

// Repository interface for community post, comment and report operations
interface CommunityRepository {

    // Save new community post
    suspend fun insertPost(post: CommunityPost): Long

    // Get all community posts
    suspend fun getAllPosts(): List<CommunityPost>

    // Update the comment count for a post
    suspend fun updatePostComments(postId: Int, newCount: Int)

    //Update the like count for a post
    suspend fun updatePostLikes(postId: Int, newLikes: Int)

    // Delete post by ID
    suspend fun deletePost(postId: Int)

    // Check whether there are any community posts stored
    suspend fun isPostsEmpty(): Boolean

    // Get the post or posts with the highest like count
    suspend fun getTopLikedPosts(): List<CommunityPost>

    // Save new community comment
    suspend fun insertComment(comment: CommunityComment)

    //Save new comment using individual comment fields
    suspend fun insertComment(
        postId: Int,
        username: String,
        date: String,
        text: String
    )

    // Get all comments linked to post
    suspend fun getCommentsByPostId(postId: Int): List<CommunityComment>

    // Delete all comments linked to a post
    suspend fun deleteCommentsByPostId(postId: Int)

    // Delete single comment by ID
    suspend fun deleteSingleComment(commentId: Int)

    //Check whether there are any reported numbers stored
    suspend fun isReportsEmpty(): Boolean

    // Insert new report or update an existing report count
    suspend fun insertOrUpdateReport(number: String, message: String)

    // Get the most reported numbers
    suspend fun getTopReportedDetails(limit: Int): List<CommunityReportedNumber>
}