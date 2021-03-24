package com.antdigital.agency.services;

public interface IValidationService {
    boolean checkExistCustomerTransaction(String customerId);
    boolean checkExistCollaboratorOrEmployee(String collaboratorId, String employeeId);
    boolean checkExistCollaboratorPhone(String phone);
    boolean checkExistCollaboratorEmail(String email);
    boolean checkExistMerchandise(String merchandiseId);
}
