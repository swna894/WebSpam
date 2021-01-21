package com.ever.webSpam.spam;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpamRepository extends JpaRepository<Spam, Long> {

  
}
