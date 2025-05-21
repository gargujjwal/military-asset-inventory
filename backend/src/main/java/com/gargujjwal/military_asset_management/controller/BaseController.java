package com.gargujjwal.military_asset_management.controller;

import com.gargujjwal.military_asset_management.dto.BaseDto;
import com.gargujjwal.military_asset_management.service.BaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/bases",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BaseController {
  private final BaseService baseService;

  @GetMapping(consumes = MediaType.ALL_VALUE)
  List<BaseDto> getAllBases() {
    return baseService.getAllBases();
  }

  @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
  BaseDto getBaseById(@Valid @NotBlank @PathVariable String id) {
    return baseService.getBaseById(id);
  }

  @PostMapping
  void createBase(@Valid @RequestBody BaseDto newBase) {
    baseService.createBase(newBase);
  }

  @DeleteMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
  void deleteBase(@Valid @NotBlank @PathVariable String id) {
    baseService.deleteBase(id);
  }

  @PatchMapping("/{baseId}/assign/{username}")
  void assignUserToBase(
      @Valid @NotBlank @PathVariable String baseId,
      @Valid @NotBlank @PathVariable String username) {
    baseService.assignUserToBase(baseId, username);
  }
}
