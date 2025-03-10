// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of image objects
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
