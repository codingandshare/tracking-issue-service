package com.codingandshare.tracking.domains;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Mapping to role table
 *
 * @author Nhan Dinh
 * @since 9/17/21
 **/
@Data
@Entity
@Table(name = "role")
@EqualsAndHashCode
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String roleName;

  @Column(nullable = false)
  private String roleDescription;
}
