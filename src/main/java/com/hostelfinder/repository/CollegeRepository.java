package com.hostelfinder.repository;

import com.hostelfinder.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long> {
    List<College> findByCollegeNameContainingIgnoreCase(String name);
}
