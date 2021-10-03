package com.codingandshare.tracking.repositories;

import com.codingandshare.tracking.domains.Version;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository relate to version table
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public interface VersionRepository extends JpaRepository<Version, Integer> {
}
