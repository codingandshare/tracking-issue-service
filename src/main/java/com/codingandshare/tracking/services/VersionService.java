package com.codingandshare.tracking.services;

import com.codingandshare.tracking.annotations.IsRoleAdmin;
import com.codingandshare.tracking.domains.User;
import com.codingandshare.tracking.domains.Version;
import com.codingandshare.tracking.exceptions.NotFoundException;
import com.codingandshare.tracking.repositories.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Handle business logic relate to version table
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Service
public class VersionService {

  @Autowired
  private VersionRepository versionRepository;

  @Autowired
  private AuthenUserService authenUserService;

  @IsRoleAdmin
  public void createVersion(Version version) {
    User currentUser = this.authenUserService.getUserCurrentLogin();
    version.setCreatedBy(currentUser.getId());
    version.setModifiedBy(currentUser.getId());
    this.versionRepository.save(version);
  }

  @IsRoleAdmin
  public void updateVersion(Integer versionId, Version version) {
    Optional<Version> versionOptional = this.versionRepository.findById(versionId);
    Version versionUpdating = versionOptional.orElseThrow(() -> {
      throw new NotFoundException("Version id not found");
    });
    versionUpdating.setVersion(version.getVersion());
    versionUpdating.setDescription(version.getDescription());
    User currentUser = this.authenUserService.getUserCurrentLogin();
    version.setModifiedBy(currentUser.getId());
    this.versionRepository.save(versionUpdating);
  }

  public List<Version> getVersions() {
    return this.versionRepository.findAll();
  }
}
