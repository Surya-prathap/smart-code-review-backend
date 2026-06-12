package com.smartreview.repository;

import com.smartreview.entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeReviewRepository extends JpaRepository<CodeReview,Long> {
}
