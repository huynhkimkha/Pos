package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.Order;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.services.IValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationServiceImpl implements IValidationService {
    private static final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private IImportingWarehouseRepository importingWarehouseRepository;

    @Autowired
    private IExportingWarehouseRepository exportingWarehouseRepository;

    @Autowired
    private IReceiptRepository receiptRepository;

    @Autowired
    private IReceiptAdviceRepository receiptAdviceRepository;

    @Autowired
    private IPaymentAdviceRepository paymentAdviceRepository;

    @Autowired
    private IImportingReturnRepository importingReturnRepository;

    @Autowired
    private IExportingReturnRepository exportingReturnRepository;

    @Autowired
    private IDebtClearingDetailRepository debtClearingDetailRepository;

    @Autowired
    private ICollaboratorRepository collaboratorRepository;

    @Autowired
    private IEmployeesRepository employeesRepository;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IOrderTransactionRepository orderTransactionRepository;

    @Autowired
    private IImportingReturnTransactionRepository importingReturnTransactionRepository;

    @Autowired
    private IExportingReturnTransactionRepository exportingReturnTransactionRepository;

    @Override
    public boolean checkExistCustomerTransaction(String customerId) {
        try {
            int countOrderCustomer = orderRepository.countCustomerId(customerId);
            int countPaymentCustomer = paymentRepository.countCustomerId(customerId);
            int countImportCustomer = importingWarehouseRepository.countCustomerId(customerId);
            int countExportCustomer = exportingWarehouseRepository.countCustomerId(customerId);
            int countReceiptCustomer = receiptRepository.countCustomerId(customerId);
            int countReceiptAdviceCustomer = receiptAdviceRepository.countCustomerId(customerId);
            int countPaymentAdviceCustomer = paymentAdviceRepository.countCustomerId(customerId);
            int countImportReturnCustomer = importingReturnRepository.countCustomerId(customerId);
            int countExportReturnCustomer = exportingReturnRepository.countCustomerId(customerId);
            int countDebtClearingCustomer = debtClearingDetailRepository.countCustomerId(customerId);
            int countDebtClearingDebtCustomer = debtClearingDetailRepository.countDebtCustomerId(customerId);
            if (countOrderCustomer >= 1 || countImportCustomer >= 1 || countExportCustomer >= 1 || countReceiptCustomer >= 1
                    || countPaymentCustomer >= 1 || countReceiptAdviceCustomer >= 1 || countPaymentAdviceCustomer >= 1
                    || countExportReturnCustomer >= 1 || countImportReturnCustomer >= 1 || countDebtClearingCustomer >= 1 || countDebtClearingDebtCustomer >= 1) {
                return true;
            }
            return false;
        }catch (EntityNotFoundException ex) {
            return false;
        }
    }

    @Override
    public boolean checkExistCollaboratorOrEmployee(String collaboratorId, String employeeId) {
        try {
            int countCollaborator = collaboratorRepository.countCollaboratorId(collaboratorId);
            int countEmployee = employeesRepository.countEmployeeId(employeeId);
            if (countCollaborator > 0 || countEmployee > 0) {
                    if(countCollaborator > 0 && countEmployee > 0){
                        return false;
                    }
                return true;
            }
            return false;
        }catch (EntityNotFoundException ex) {
            return false;
        }
    }

    @Override
    public boolean checkExistCollaboratorPhone(String phone) {
        try {
            int countCollaboratorPhone = collaboratorRepository.countCollaboratorPhone(phone);
            if (countCollaboratorPhone > 0) {
                return true;
            }
            return false;
        }catch (EntityNotFoundException ex) {
            return false;
        }
    }

    @Override
    public boolean checkExistCollaboratorEmail(String email) {
        try {
            int countCollaboratorEmail = collaboratorRepository.countCollaboratorEmail(email);
            if (countCollaboratorEmail > 0) {
                return true;
            }
            return false;
        }catch (EntityNotFoundException ex) {
            return false;
        }
    }

    @Override
    public boolean checkExistMerchandise(String merchandiseId) {
        try {
            int countImportingTransaction = importingTransactionRepository.countMerchandise(merchandiseId);
            int countExportingTransaction = exportingTransactionRepository.countMerchandise(merchandiseId);
            int countOrderTransaction = orderTransactionRepository.countMerchandise(merchandiseId);
            int countImportingReturnTransaction = importingReturnTransactionRepository.countMerchandise(merchandiseId);
            int countExportingReturnTransaction = exportingReturnTransactionRepository.countMerchandise(merchandiseId);

            if (countImportingTransaction > 0 || countExportingTransaction > 0 || countOrderTransaction > 0
                    || countImportingReturnTransaction > 0 || countExportingReturnTransaction > 0){
                return true;
            }
            return false;
        }
        catch (EntityNotFoundException ex){
            return false;
        }
    }
}
