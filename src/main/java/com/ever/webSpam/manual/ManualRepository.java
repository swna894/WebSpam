package com.ever.webSpam.manual;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManualRepository extends JpaRepository<Manual, Long> {

	List<Manual> findByDocContaining(String search);
  
}
