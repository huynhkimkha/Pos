package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ImportingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IImportingTransactionRepository extends JpaRepository<ImportingTransaction, String> {
    @Query("select t from ImportingTransaction t where t.importingMaterial.id = ?1")
    List<ImportingTransaction> getByImportingMaterialId(String id);
}
