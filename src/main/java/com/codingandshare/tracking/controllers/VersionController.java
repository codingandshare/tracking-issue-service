package com.codingandshare.tracking.controllers;

import com.codingandshare.tracking.domains.Version;
import com.codingandshare.tracking.services.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Define all endpoints relate to version
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@RestController
@RequestMapping("/tracking/version")
public class VersionController {

  @Autowired
  private VersionService versionService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void createVersion(@RequestBody @Valid Version version) {
    this.versionService.createVersion(version);
  }

  @PutMapping(
      path = "{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public void updateVersion(
      @PathVariable(name = "id") Integer id,
      @RequestBody @Valid Version version
  ) {
    this.versionService.updateVersion(id, version);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Version> getVersions(){
    return this.versionService.getVersions();
  }
}
