package com.codingandshare.tracking.repositories;

import com.codingandshare.tracking.domains.IssueMetaData;
import com.codingandshare.tracking.domains.MetadataType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The repository relate to issue_metadata table in db
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
public interface IssueMetaDataRepository extends JpaRepository<IssueMetaData, Integer> {

  public IssueMetaData findByTypeAndValue(MetadataType type, String value);
}
