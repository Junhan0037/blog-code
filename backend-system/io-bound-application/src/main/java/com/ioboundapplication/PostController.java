package com.ioboundapplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private static Integer PAGE_SIZE = 20;

    private final PostRepository postRepository;
    private final Producer producer;
    private final ObjectMapper objectMapper;

    @PostMapping("/post")
    public Post createPost(@RequestBody Post post) throws JsonProcessingException {
        String jsonPost = objectMapper.writeValueAsString(post);
        producer.sendTo(jsonPost);
        return post;
    }

    @GetMapping("/posts")
    public Page<Post> getPostList(@RequestParam(defaultValue = "1") Integer page) {
        return postRepository.findAll(
                PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").descending())
        );
    }

    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable("id") Long id) {
        return postRepository.findById(id).get();
    }

    @GetMapping("/search")
    public List<Post> findPostsByContent(@RequestParam String content) {
        return postRepository.findByContentContains(content);
    }

}
