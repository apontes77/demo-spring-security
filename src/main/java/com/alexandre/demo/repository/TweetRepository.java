package com.alexandre.demo.repository;

import com.alexandre.demo.entities.Role;
import com.alexandre.demo.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
}
