package com.example.smishingdetectionapp.data.repository

import com.example.smishingdetectionapp.community.CommunityComment
import com.example.smishingdetectionapp.community.CommunityPost
import com.example.smishingdetectionapp.community.CommunityReportedNumber
import com.example.smishingdetectionapp.community.CommunityQueries

// SQLDelight implementation of CommunityRepository interface
class CommunityRepositoryDelight(
    private val queries: CommunityQueries,
    private val currentDateProvider: () -> String
) : CommunityRepository {

    // Save new community post and return inserted row ID
    override suspend fun insertPost(post: CommunityPost): Long {
        queries.insertPost(
            post.username,
            post.date,
            post.posttitle,
            post.postdescription,
            post.likes.toLong(),
            post.comments.toLong()
        )
        return queries.getLastInsertRowId().executeAsOne()
    }

    //Get all community posts from SQLDelight
    override suspend fun getAllPosts(): List<CommunityPost> {
        return queries.getAllPosts().executeAsList().map {
            CommunityPost(
                id = it.id.toInt(),
                username = it.username,
                date = it.date,
                posttitle = it.title,
                postdescription = it.description,
                likes = it.likes.toInt(),
                comments = it.comments.toInt()
            )
        }
    }

    // Update the comment count for post
    override suspend fun updatePostComments(postId: Int, newCount: Int) {
        queries.updatePostComments(newCount.toLong(), postId.toLong())
    }

    // Update the like count for post
    override suspend fun updatePostLikes(postId: Int, newLikes: Int) {
        queries.updatePostLikes(newLikes.toLong(), postId.toLong())
    }

    // Delete post and its related comments
    override suspend fun deletePost(postId: Int) {
        queries.deleteCommentsByPostId(postId.toLong())
        queries.deletePost(postId.toLong())
    }

    // Check whether post's table is empty
    override suspend fun isPostsEmpty(): Boolean {
        return queries.isPostsEmpty().executeAsOne() == 0L
    }

    //Get post or posts with the highest like count
    override suspend fun getTopLikedPosts(): List<CommunityPost> {
        return queries.getTopLikedPosts().executeAsList().map {
            CommunityPost(
                id = it.id.toInt(),
                username = it.username,
                date = it.date,
                posttitle = it.title,
                postdescription = it.description,
                likes = it.likes.toInt(),
                comments = it.comments.toInt()
            )
        }
    }

    // Save a new community comment
    override suspend fun insertComment(comment: CommunityComment) {
        queries.insertComment(
            comment.postId.toLong(),
            comment.user,
            comment.date,
            comment.commentText
        )
    }

    // Save new community comment using individual values
    override suspend fun insertComment(
        postId: Int,
        username: String,
        date: String,
        text: String
    ) {
        queries.insertComment(postId.toLong(), username, date, text)
    }

    // Get comments linked to a post
    override suspend fun getCommentsByPostId(postId: Int): List<CommunityComment> {
        return queries.getCommentsByPostId(postId.toLong()).executeAsList().map {
            CommunityComment(
                commentId = it.id.toInt(),
                postId = it.post_id.toInt(),
                user = it.comment_user,
                date = it.comment_date,
                commentText = it.comment_text
            )
        }
    }

    //Delete all comments linked to a post
    override suspend fun deleteCommentsByPostId(postId: Int) {
        queries.deleteCommentsByPostId(postId.toLong())
    }

    // Delete a single comment by ID
    override suspend fun deleteSingleComment(commentId: Int) {
        queries.deleteSingleComment(commentId.toLong())
    }

    // Check whether reports table is empty
    override suspend fun isReportsEmpty(): Boolean {
        return queries.isReportsEmpty().executeAsOne() == 0L
    }

    // Insert new reported number/update the existing report count
    override suspend fun insertOrUpdateReport(number: String, message: String) {
        val existingReport = queries.getReportByNumber(number).executeAsOneOrNull()
        val currentDate = currentDateProvider()

        if (existingReport == null) {
            queries.insertReport(number, message, 1L, currentDate)
        } else {
            queries.updateReport(
                existingReport.report_count + 1,
                currentDate,
                number
            )
        }
    }

    // Get most reported numbers
    override suspend fun getTopReportedDetails(limit: Int): List<CommunityReportedNumber> {
        return queries.getTopReportedDetails(limit.toLong()).executeAsList().map {
            CommunityReportedNumber(
                number = it.reported_number,
                count = it.report_count.toInt(),
                lastReportedDate = it.last_reported_date
            )
        }
    }
}