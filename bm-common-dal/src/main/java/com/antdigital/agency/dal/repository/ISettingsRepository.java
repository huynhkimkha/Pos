package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISettingsRepository extends JpaRepository<Settings, String> {
}
