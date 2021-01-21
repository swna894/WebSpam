package com.ever.webSpam.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpamCategoryRepository extends JpaRepository<SpamCategory, Long> {

	SpamCategory findByUri(String uri);

	List<SpamCategory> findAllByOrderByUriAsc();

  
}
