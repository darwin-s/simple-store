package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of image objects
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
