package com.hive.postservice.Service;

import com.hive.postservice.DTO.CommentDTO;
import com.hive.postservice.DTO.LikeDTO;
import com.hive.postservice.DTO.PostDTO;
import com.hive.postservice.DTO.PostRequestDTO;
import com.hive.postservice.Entity.Comment;
import com.hive.postservice.Entity.Like;
import com.hive.postservice.Entity.Post;
import com.hive.postservice.FeignClientConfig.UserInterface;
import com.hive.postservice.Repository.CommentDAO;
import com.hive.postservice.Repository.LikeDAO;
import com.hive.postservice.Repository.PostDAO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    @Value("${FOLDER.PATH}")
    private String FOLDER_PATH;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final LikeDAO likeDAO;
    private final UserInterface userInterface;


    @Override
    @Transactional
    public PostDTO createPost(MultipartFile file, PostRequestDTO postRequestDTO) {
        if ( !isValidUserId(postRequestDTO.getUserId()) ) {
            throw new RuntimeException("Invalid user id" + postRequestDTO.getUserId());
        }
        try {
            //? Saving File to File System
            String filePath = FOLDER_PATH + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            //? Saving Post Details Into DB
            Post post = Post.builder()
                    .description(postRequestDTO.getDescription())
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .filePath(filePath)
                    .createdOn(Timestamp.from(Instant.now()))
                    .userId(postRequestDTO.getUserId())
                    .postType(postRequestDTO.getPostType())
                    .build();
            return entityToDTO(postDAO.save(post));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PostDTO getPost(Long postId) {
        return entityToDTO( getPostEntity(postId));
    }

    @Override
    public byte[] getPostFile(Long postId) throws IOException {
        Post post = getPostEntity(postId);
        String filePath = post.getFilePath();
        return Files.readAllBytes(new File(filePath).toPath());
    }

    @Override
    public List<PostDTO> getPostsForUser(Long userId) {
        if( !isValidUserId(userId) )
            throw new RuntimeException("Invalid user id" + userId);

        return List.of(); //! INCOMPLETE
    }

    @Override
    public List<PostDTO> getRandomPosts(Integer pageNumber, Integer pageSize) {
        return postDAO
                .findRandomPosts(PageRequest.of(pageNumber, pageSize))
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        if( postDAO.existsById(postId) ) {
            postDAO.deleteById(postId);
        }
        else {
            throw new RuntimeException("Post not found with id: " + postId);
        }
    }

    //POST METHODS ENDED
    //COMMENT METHODS STARTED

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        if ( !isValidUserId( commentDTO.getUserId()) )
            throw new RuntimeException("Invalid user id" + commentDTO.getUserId());

        if ( postDAO.existsById( commentDTO.getPostId()) )
            throw new RuntimeException("Invalid post id" + commentDTO.getPostId());

        return entityToDTO( commentDAO.save( dtoToEntity(commentDTO)));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        if( commentDAO.existsById(commentId) ) {
            commentDAO.deleteById(commentId);
        }
        else {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }
    }

    @Override
    public List<CommentDTO> getCommentsForPost(Long postId) {
        return commentDAO
                .findByPost( getPostEntity(postId))
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public CommentDTO getComment(Long commentId) {
        return commentDAO
                .findById(commentId)
                .map(this::entityToDTO)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
    }

    @Override
    public Long commentCount(Long postId) {
        return commentDAO.countAllByPost( getPostEntity(postId));
    }

    //COMMENT METHODS ENDED
    //LIKE METHODS STARTED

    @Override
    @Transactional
    public LikeDTO createLike(LikeDTO likeDTO) {
        if ( !isValidUserId(likeDTO.getUserId()) )
            throw new RuntimeException("Invalid user id" + likeDTO.getUserId());

        Optional<Post> post = postDAO.findById(likeDTO.getPostId());
        if ( post.isEmpty() )
            throw new RuntimeException("Invalid post id" + likeDTO.getPostId());

        if ( likeDAO.existsByPostAndUserId(post.get(), likeDTO.getUserId()) )
            return null;

        return entityToDTO( likeDAO.save( dtoToEntity(likeDTO)));
    }

    @Override
    public LikeDTO getLike(Long likeId) {
        return likeDAO
                .findById(likeId)
                .map(this::entityToDTO)
                .orElseThrow(() -> new RuntimeException("Invalid likeId: " + likeId));
    }

    @Override
    public List<LikeDTO> getLikesForPost(Long postId) {
        return likeDAO
                .findByPost( getPostEntity(postId))
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public void deleteLike(Long likeId) {
        if ( likeDAO.existsById(likeId) ) {
            likeDAO.deleteById(likeId);
        }
        else {
            throw new RuntimeException("Like not found with id: " + likeId);
        }
    }

    @Override
    public Long likeCount(Long postId) {
        return likeDAO.countByPost( getPostEntity(postId));
    }

    //LIKE METHODS ENDED
    //HELPER METHODS STARTED

    public Post getPostEntity(Long postId) {
        return postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

    private Boolean isValidUserId(Long userId) {
        return userInterface.isUserExists(userId).getBody();
    }

    private PostDTO entityToDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .description(post.getDescription())
                .fileName(post.getFileName())
                .fileType(post.getFileType())
                .filePath(post.getFilePath())
                .createdOn(post.getCreatedOn())
                .userId(post.getUserId())
                .postType(post.getPostType())
                .build();
    }

    private Post dtoToEntity(PostDTO dto) {
        return Post.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .fileName(dto.getFileName())
                .fileType(dto.getFileType())
                .filePath(dto.getFilePath())
                .createdOn(dto.getCreatedOn())
                .userId(dto.getUserId())
                .postType(dto.getPostType())
                .build();
    }

    private CommentDTO entityToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .commentedDate(comment.getCommentedDate())
                .userId(comment.getUserId())
                .postId(comment.getPost().getId())
                .build();
    }

    private Comment dtoToEntity(CommentDTO dto) {
        return Comment.builder()
                .id(dto.getId())
                .comment(dto.getComment())
                .commentedDate(dto.getCommentedDate())
                .userId(dto.getUserId())
                .post( getPostEntity(dto.getPostId()))
                .build();
    }

    private LikeDTO entityToDTO(Like like) {
        return LikeDTO.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .likedDate(like.getLikedDate())
                .postId(like.getPost().getId())
                .build();
    }

    private Like dtoToEntity(LikeDTO dto) {
        return Like.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .likedDate(dto.getLikedDate())
                .post( getPostEntity(dto.getPostId()))
                .build();
    }
}