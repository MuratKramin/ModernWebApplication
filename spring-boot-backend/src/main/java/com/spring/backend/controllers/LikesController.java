package com.spring.backend.controllers;

import com.spring.backend.models.Likes;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.LikesRepository;
import com.spring.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/likes")
public class LikesController {

    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;

    // Получить все записи
    @GetMapping
    public ResponseEntity<List<Likes>> getAllLikes() {
        try {
            List<Likes> likes = likesRepository.findAll();
            if (likes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(likes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Получить одну запись по ID
    @GetMapping("/{id}")
    public ResponseEntity<Likes> getLikeById(@PathVariable("id") int id) {
        Optional<Likes> likeData = likesRepository.findById(id);

        return likeData.map(like -> new ResponseEntity<>(like, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Создать новую запись
    @PostMapping
    public ResponseEntity<Likes> createLike(@RequestBody Likes like) {
        try {
            Likes _like = likesRepository.save(new Likes(like.getId(), like.getUser(), like.getHotel()));
            return new ResponseEntity<>(_like, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/byId")
    public ResponseEntity<Likes> createLikeById(@RequestParam int user_id,@RequestParam int hotel_id) {
        try {
            Likes _like = likesRepository.save(new Likes(0, userRepository.getReferenceById((long) user_id), hotelRepository.getReferenceById(hotel_id)));
            return new ResponseEntity<>(_like, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Не удалось добавить лайк");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Обновить запись
    @PutMapping("/{id}")
    public ResponseEntity<Likes> updateLike(@PathVariable("id") int id, @RequestBody Likes like) {
        Optional<Likes> likeData = likesRepository.findById(id);

        if (likeData.isPresent()) {
            Likes _like = likeData.get();
            _like.setUser(like.getUser());
            _like.setHotel(like.getHotel());
            return new ResponseEntity<>(likesRepository.save(_like), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удалить запись
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteLike(@PathVariable("id") int id) {
        try {
            likesRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Удалить все записи
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllLikes() {
        try {
            likesRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
