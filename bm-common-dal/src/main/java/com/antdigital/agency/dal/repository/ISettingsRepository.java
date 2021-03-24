package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISettingsRepository extends JpaRepository<Settings, String> {
    @Query("select t from Settings t where t.agency.id = ?1")
    List<Settings> findAll(String agencyId);
    @Query("select t from Settings t where t.key=?1")
    Settings getByKey(String key);
}
