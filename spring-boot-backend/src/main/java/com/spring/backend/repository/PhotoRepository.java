package com.spring.backend.repository;

import com.spring.backend.models.Photo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Integer> {
    @Query("select p from Photo p where p.hotel_pic.id=?1")
    List<Photo> findPhotoByHotelId(int id_hotel);
}
