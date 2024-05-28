package com.hive.postservice.Service;

import com.hive.postservice.DTO.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
            throw new RuntimeException("[createPost] Invalid user id " + postRequestDTO.getUserId());
        }
        try {
            //? Saving File to File System
            String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(Timestamp.from(Instant.now()));
            String filePath = FOLDER_PATH + timestamp + "-" + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            //? Saving Post Details Into DB
            Post post = Post.builder()
                    .description(postRequestDTO.getDescription())
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .filePath(filePath)
                    .createdOn(Timestamp.from(Instant.now()))
                    .userId(postRequestDTO.getUserId())
                    .isBlocked(false)
                    .postType(postRequestDTO.getPostType())
                    .build();
            return entityToDTO(postDAO.save(post));
        }
        catch (IOException e) {
            throw new RuntimeException("[createPost IO] " + e);
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
            throw new RuntimeException("[getPostsForUser] Invalid user id " + userId);

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
            throw new RuntimeException("[deletePost] Post not found with id: " + postId);
        }
    }

    @Override
    public PostDTO blockPost(Long postId) {
        Post post = getPostEntity(postId);
        post.setIsBlocked(true);
        return entityToDTO(postDAO.save(post));
    }

    @Override
    public PostDTO unBlockPost(Long postId) {
        Post post = getPostEntity(postId);
        post.setIsBlocked(false);
        return entityToDTO(postDAO.save(post));
    }

    @Override
    public Long postCount() {
        return postDAO.count();
    }

    @Override
    public PaginationInfo getAllPosts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Post> page = postDAO.findAll(pageable);
        List<PostDTO> contents = page.getContent().stream().map(this::entityToDTO).toList();

        return PaginationInfo.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .isLast(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public List<PostDTO> getUserPosts(Long userId) {
        if( !isValidUserId(userId) )
            throw new RuntimeException("[getUserPosts] Invalid user id " + userId);
        return postDAO.findByUserId(userId).stream().map(this::entityToDTO).toList();
    }

    //POST METHODS ENDED
    //COMMENT METHODS STARTED

    @Override
    public CommentDTO createComment(CommentRequestDTO commentRequest) {
        if ( !isValidUserId( commentRequest.getUserId()) )
            throw new RuntimeException("[createComment] Invalid user id " + commentRequest.getUserId());

        if ( !postDAO.existsById( commentRequest.getPostId()) )
            throw new RuntimeException("[createComment] Invalid post id " + commentRequest.getPostId());

        Comment comment = Comment.builder()
                .comment(commentRequest.getComment())
                .commentedDate(Timestamp.from(Instant.now()))
                .userId(commentRequest.getUserId())
                .isBlocked(false)
                .post( getPostEntity(commentRequest.getPostId()))
                .build();
        return entityToDTO( commentDAO.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        if( commentDAO.existsById(commentId) ) {
            commentDAO.deleteById(commentId);
        }
        else {
            throw new RuntimeException("[deleteComment] Comment not found with id: " + commentId);
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
                .orElseThrow(() -> new RuntimeException("[getComment] Comment not found with id: " + commentId));
    }

    @Override
    public Long commentCount(Long postId) {
        return commentDAO.countAllByPost( getPostEntity(postId));
    }

    @Override
    public CommentDTO blockComment(Long commentId) {
        Optional<Comment> commentOp = commentDAO.findById(commentId);
        if ( commentOp.isPresent() ) {
            Comment comment = commentOp.get();
            comment.setIsBlocked(true);
            return entityToDTO(commentDAO.save(comment));
        }
        return null;
    }

    @Override
    public CommentDTO unBlockComment(Long commentId) {
        Optional<Comment> commentOp = commentDAO.findById(commentId);
        if ( commentOp.isPresent() ) {
            Comment comment = commentOp.get();
            comment.setIsBlocked(false);
            return entityToDTO(commentDAO.save(comment));
        }
        return null;
    }

    //COMMENT METHODS ENDED
    //LIKE METHODS STARTED

    @Override
    @Transactional
    public LikeDTO createLike(LikeRequestDTO likeRequest) {
        if ( !isValidUserId(likeRequest.getUserId()) )
            throw new RuntimeException("[createLike] Invalid user id " + likeRequest.getUserId());

        Optional<Post> post = postDAO.findById(likeRequest.getPostId());
        if ( post.isEmpty() )
            throw new RuntimeException("[createLike] Invalid post id " + likeRequest.getPostId());

        if ( likeDAO.existsByPostAndUserId(post.get(), likeRequest.getUserId()) )
            return null;

        Like like = Like.builder()
                .userId(likeRequest.getUserId())
                .likedDate(Timestamp.from(Instant.now()))
                .post( getPostEntity(likeRequest.getPostId()))
                .build();
        return entityToDTO( likeDAO.save(like));
    }

    @Override
    public LikeDTO getLike(Long likeId) {
        return likeDAO
                .findById(likeId)
                .map(this::entityToDTO)
                .orElseThrow(() -> new RuntimeException("[getLike] Invalid likeId: " + likeId));
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
            throw new RuntimeException("[deleteLike] Like not found with id: " + likeId);
        }
    }

    @Override
    public Long likeCount(Long postId) {
        return likeDAO.countByPost( getPostEntity(postId));
    }

    @Override
    public Boolean isUserLiked(LikeRequestDTO likeRequest) {
        if ( !isValidUserId(likeRequest.getUserId()) )
            throw new RuntimeException("[createLike] Invalid user id " + likeRequest.getUserId());

        Optional<Post> post = postDAO.findById(likeRequest.getPostId());
        if ( post.isEmpty() )
            throw new RuntimeException("[createLike] Invalid post id " + likeRequest.getPostId());

        return likeDAO.existsByPostAndUserId(post.get(), likeRequest.getUserId());
    }

    //LIKE METHODS ENDED
    //HELPER METHODS STARTED

    public Post getPostEntity(Long postId) {
        return postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("[getPostEntity] Post not found with id: " + postId));
    }

    private Boolean isValidUserId(Long userId) {
        return userInterface.isUserExists(userId).getBody();
    }

    private PostDTO entityToDTO(Post post) {
        System.out.println(post);
        return PostDTO.builder()
                .id(post.getId())
                .description(post.getDescription())
                .fileName(post.getFileName())
                .fileType(post.getFileType())
                .filePath(post.getFilePath())
                .createdOn(post.getCreatedOn())
                .userId(post.getUserId())
                .isBlocked(post.getIsBlocked())
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
                .isBlocked(dto.getIsBlocked())
                .postType(dto.getPostType())
                .build();
    }

    private CommentDTO entityToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .commentedDate(comment.getCommentedDate())
                .userId(comment.getUserId())
                .isBlocked(comment.getIsBlocked())
                .postId(comment.getPost().getId())
                .build();
    }

    private Comment dtoToEntity(CommentDTO dto) {
        return Comment.builder()
                .id(dto.getId())
                .comment(dto.getComment())
                .commentedDate(dto.getCommentedDate())
                .userId(dto.getUserId())
                .isBlocked(dto.getIsBlocked())
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