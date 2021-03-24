package com.antdigital.agency.core.models.warehouse.request;

import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.models.warehouse.MerchandiseGroupModel;
import com.antdigital.agency.core.models.warehouse.ProductGroupModel;
import lombok.Data;

@Data
public class MerchandiseSearchModel {
    private String code;
    private String companyId;
    private MerchandiseGroupModel merchandiseGroup1;
    private MerchandiseGroupModel merchandiseGroup2;
    private CustomerModel merchandiseGroup3;
    private ProductGroupModel productGroup;

    public MerchandiseSearchModel() {
        merchandiseGroup1 = new MerchandiseGroupModel();
        merchandiseGroup2 = new MerchandiseGroupModel();
        merchandiseGroup3 = new CustomerModel();
        productGroup = new ProductGroupModel();
    }
}
