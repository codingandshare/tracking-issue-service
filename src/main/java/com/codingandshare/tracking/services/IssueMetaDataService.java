package com.codingandshare.tracking.services;

import com.codingandshare.tracking.domains.IssueMetaData;
import com.codingandshare.tracking.repositories.IssueMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handle business for issues meta data
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Service
public class IssueMetaDataService {

  @Autowired
  private IssueMetaDataRepository issueMetaDataRepository;

  public List<IssueMetaData> findAll() {
    return this.issueMetaDataRepository.findAll();
  }
}
