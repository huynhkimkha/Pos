package com.antdigital.agency.core.models.warehouse.request;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerGroupModel;
import lombok.Data;

@Data
public class CustomerSearchModel {
    private String companyId;
    private String code;
    private CustomerGroupModel customerGroup1;
    private CustomerGroupModel customerGroup2;
    private CustomerGroupModel customerGroup3;
    private AccountingTableModel accountingTable;

    public CustomerSearchModel() {
        customerGroup1 = new CustomerGroupModel();
        customerGroup2 = new CustomerGroupModel();
        customerGroup3 = new CustomerGroupModel();
        accountingTable = new AccountingTableModel();
    }
}
