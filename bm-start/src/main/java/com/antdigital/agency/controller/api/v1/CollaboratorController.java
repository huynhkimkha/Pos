package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.BlockStatusEnum;
import com.antdigital.agency.common.utils.BCryptHelper;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.entity.Payment;
import com.antdigital.agency.dal.entity.PaymentAdvice;
import com.antdigital.agency.dal.entity.PaymentDetail;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ChangePasswordDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.ICollaboratorService;
import com.antdigital.agency.services.IPaymentAdviceService;
import com.antdigital.agency.services.IPaymentService;
import com.antdigital.agency.services.IReferralBonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/collaborator")
public class CollaboratorController extends BaseController {
    @Autowired
    private ICollaboratorService collaboratorService;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IReferralBonusService referralBonusService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IPaymentAdviceService paymentAdviceService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        String agencyId = getAgencyId();
        List<CollaboratorDto> collaboratorDtos = collaboratorService.findAll(agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách cộng tác viên"), HttpStatus.OK.value(), collaboratorDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<CollaboratorDto>> searchDto) {
        String agencyId = getAgencyId();
        BaseSearchDto<List<CollaboratorDto>> search = collaboratorService.findAll(searchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách cộng tác viên"), HttpStatus.OK.value(), search));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@Valid @RequestBody CollaboratorSearchDto collaboratorSearchDto) {
        String agencyId = getAgencyId();
        CollaboratorSearchDto search = collaboratorService.findAllAffiliate(collaboratorSearchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách cộng tác viên"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/like-name")
    public ResponseEntity<?> getLikeName(@RequestParam String name) {
        String agencyId = getAgencyId();
        List<CollaboratorDto> collaborators = collaboratorService.getLikeName(name, agencyId);
        ResponseEntity<?> res = collaborators != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Collaborator"), HttpStatus.OK.value(), collaborators))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Cộng tác viên không tồn tại"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @PostMapping("/list")
    public ResponseEntity<?> getCollaborators(@RequestBody List<String> ids) {
        List<CollaboratorTempDto> collaborators = collaboratorService.getCollaboratorsById(ids);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách nhân viên"), HttpStatus.OK.value(), collaborators));
    }

    @GetMapping("/getCollaboratorByEmail")
    public ResponseEntity<?> getCollaboratorByEmail(@RequestParam String email) {
        String companyId = getCompanyId();
        CollaboratorDto collaboratorEmail = collaboratorService.getByEmail(email, companyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("EmployeesEmail"), HttpStatus.OK.value(), collaboratorEmail));
    }

    @GetMapping("/{collaboratorId}")
    public ResponseEntity<?> getCollaboratorById(@PathVariable String collaboratorId) {
        CollaboratorDto collaborator = collaboratorService.getById(collaboratorId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Cộng tác viên"), HttpStatus.OK.value(), collaborator));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String collaboratorId) {
        CollaboratorDto collaborator = collaboratorService.getById(collaboratorId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Cộng tác viên"), HttpStatus.OK.value(), collaborator));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String collaboratorId) {
        String password = collaboratorService.resetPassword(collaboratorId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Cộng tác viên"), HttpStatus.OK.value(), password));
    }

    @GetMapping("/get-full/{collaboratorId}")
    public ResponseEntity<?> getCollaboratorFullById(HttpServletRequest request, @PathVariable String collaboratorId) {
        CollaboratorFullDto collaboratorFull = collaboratorService.getFullById(request, collaboratorId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Cộng tác viên"), HttpStatus.OK.value(), collaboratorFull));
    }

    @GetMapping("/get-collaborator-like-name")
    public ResponseEntity<?> getCollaboratorRefLikeNameOrEmail(@RequestParam String collaboratorName) {
        String agencyId = getAgencyId();
        List<CollaboratorDto> collaborators = collaboratorService.getCollaboratorRefLikeNameOrEmail(collaboratorName, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Cộng tác viên"), HttpStatus.OK.value(), collaborators));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CollaboratorFullDto collaboratorFullDto) {
        List<String> errMessages = validateRegister(collaboratorFullDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        CollaboratorFullDto collaborator = collaboratorService.register(collaboratorFullDto);

        ResponseEntity<?> res = collaborator != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin cộng tác viên thành công"), HttpStatus.OK.value(), collaborator))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin cộng tác viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody CollaboratorFullDto collaboratorFullDto) {
        List<String> errMessages = validateInserting(collaboratorFullDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(getAgencyId());
        collaboratorFullDto.setAgency(agencyDto);
        CollaboratorFullDto collaborator = collaboratorService.insert(collaboratorFullDto);

        ResponseEntity<?> res = collaborator != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin cộng tác viên thành công"), HttpStatus.OK.value(), collaborator))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin cộng tác viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("COLLABORATOR_MANAGEMENT")
    public ResponseEntity<?> update(@Valid @RequestBody CollaboratorFullDto collaboratorFullDto) {
        List<String> msg = validateUpdate(collaboratorFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        collaboratorFullDto.setAgency(new AgencyDto());
        collaboratorFullDto.getAgency().setId(getAgencyId());
        CollaboratorFullDto collaboratorFullDto1 = collaboratorService.update(collaboratorFullDto);

        ResponseEntity<?> res = collaboratorFullDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thành công"), HttpStatus.OK.value(), collaboratorFullDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin cộng tác viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        String companyId = getCompanyId();
        List<String> msg = changePassValidation(changePasswordDto);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(),""));
        }

        CollaboratorDto curCollaborator = getCurrentCollaborator();
        CollaboratorDto collaborator = collaboratorService.changePassword(curCollaborator.getEmail(), changePasswordDto, companyId);

        ResponseEntity<?> res = collaborator != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Đổi mật khẩu thành công"),HttpStatus.OK.value(),collaborator))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Đổi mật khẩu không thành công"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    @RolesAllowed("COLLABORATOR_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request ,@RequestParam String id) throws IOException, JAXBException {
        List<String> errMessages = validateDeleting(request ,id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        boolean result = collaboratorService.delete(request ,id);
        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Collaborator"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin cộng tác viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PostMapping("/getBonusByRangeDate/{collaboratorId}")
    public ResponseEntity<?> getBonusByRangeDate(@RequestBody RangeDateDto rangeDateDto, @PathVariable String collaboratorId) {
        Double bonus = collaboratorService.getBonusByRangeDate(rangeDateDto, collaboratorId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hoa hồng theo khoảng ngày"), HttpStatus.OK.value(), bonus));
    }

    private List<String> validateRegister(CollaboratorFullDto collaboratorFullDto) {
        List<String> result = new ArrayList<>();
        collaboratorFullDto.setPassword(BCryptHelper.encode(collaboratorFullDto.getPassword()));
        CollaboratorDto collaboratorDtoEmail = collaboratorService.getByEmail(collaboratorFullDto.getEmail(), collaboratorFullDto.getAgency().getCompanyId());
        CollaboratorDto collaboratorDtoPhone = collaboratorService.getByPhone(collaboratorFullDto.getPhone(), collaboratorFullDto.getAgency().getCompanyId());
        if (collaboratorFullDto.getReferee() != null
                && collaboratorFullDto.getReferee().getCollaboratorRef() != null
                && collaboratorFullDto.getReferee().getCollaboratorRef().getId() != null) {
            CollaboratorDto collaborator = collaboratorService.getById(collaboratorFullDto.getReferee().getCollaboratorRef().getId());
            if (collaborator.getBlockedStatus() == BlockStatusEnum.BLOCKED) {
                result.add("Tài khoản cộng tác viên giới thiệu không còn hoạt động");
            }
        }
        if(collaboratorDtoEmail != null){
            result.add("Email đã tồn tại");
        }
        if(collaboratorDtoPhone != null){
            result.add("Số điện thoại đã tồn tại");
        }
        return result;
    }

    private List<String> validateInserting(CollaboratorFullDto collaboratorFullDto) {
        List<String> result = new ArrayList<>();
        String companyId = getCompanyId();
        collaboratorFullDto.setPassword(BCryptHelper.encode(collaboratorFullDto.getPassword()));
        CollaboratorDto collaboratorDtoEmail = collaboratorService.getByEmail(collaboratorFullDto.getEmail(), companyId);
        CollaboratorDto collaboratorDtoPhone = collaboratorService.getByPhone(collaboratorFullDto.getPhone(), companyId);
        if (collaboratorFullDto.getReferee() != null
                && collaboratorFullDto.getReferee().getCollaboratorRef() != null
                && collaboratorFullDto.getReferee().getCollaboratorRef().getId() != null) {
            CollaboratorDto collaborator = collaboratorService.getById(collaboratorFullDto.getReferee().getCollaboratorRef().getId());
            if (collaborator.getBlockedStatus() == BlockStatusEnum.BLOCKED) {
                result.add("Tài khoản cộng tác viên giới thiệu không còn hoạt động");
            }
        }
        if(collaboratorDtoEmail != null){
            result.add("Email đã tồn tại");
        }
        if(collaboratorDtoPhone != null){
            result.add("Số điện thoại đã tồn tại");
        }
        return result;
    }

    private List<String> validateUpdate(CollaboratorFullDto collaboratorFullDto) {
        List<String> result = new ArrayList<>();
        String companyId = getCompanyId();
        CollaboratorDto collaboratorDtoEmail = collaboratorService.getByEmail(collaboratorFullDto.getEmail(), companyId);
        CollaboratorDto collaboratorDtoPhone = collaboratorService.getByPhone(collaboratorFullDto.getPhone(), companyId);
        if(collaboratorDtoEmail != null && !collaboratorFullDto.getId().equals(collaboratorDtoEmail.getId())){
            result.add("Email đã tồn tại");
        }
        if(collaboratorDtoPhone != null && !collaboratorFullDto.getId().equals(collaboratorDtoPhone.getId())){
            result.add("Số điện thoại đã tồn tại");
        }
        return result;
    }

    private List<String> validateDeleting(HttpServletRequest request ,String id) throws IOException, JAXBException {
        List<String> result = new ArrayList<>();
        if(id.isEmpty()) {
            result.add("Cộng tác viên không tồn tại");
            return result;
        }
        CollaboratorDto collaboratorDto = collaboratorService.getById(id);
        if (collaboratorDto == null) {
            result.add("Cộng tác viên không tồn tại");
            return result;
        }
        if (collaboratorDto.getBlockedStatus() == BlockStatusEnum.APPROVED){
            result.add("Không thể xóa cộng tác viên đang hoạt động");
            return result;
        }
        List<CustomerModel> customerList = warehouseRequestService.getCustomerListByCollaboratorId(request, id);
        if (customerList.size() > 0) {
            result.add("Không thể xóa cộng tác viên đã giới thiệu khách hàng");
            return result;
        }
        List<ReferralBonusDto> referralBonusDtos = referralBonusService.findAllByCollaboratorRefId(id);
        if (referralBonusDtos.size() > 0) {
            result.add("Không thể xóa cộng tác viên đã giới thiệu một cộng tác viên hay nhân viên khác");
            return result;
        }
        ReferralBonusDto referralBonusDto = referralBonusService.getByCollaboratorId(id);
        List<PaymentDetailDto> paymentDetailDtos = paymentService.getByRefferalBonusId(referralBonusDto.getId());
        List<PaymentAdviceDetailDto> paymentAdviceDetailDtos = paymentAdviceService.getByRefferalBonusId(referralBonusDto.getId());
        if (paymentDetailDtos.size() > 0 || paymentAdviceDetailDtos.size() > 0){
            result.add("Không thể xóa cộng tác viên khi đã tạo phiếu chi hoa hồng");
            return result;
        }
        return result;
    }

    private List<String> changePassValidation(ChangePasswordDto changePasswordDto) {
        List<String> result = new ArrayList<>();

        CollaboratorDto curCollaborator = getCurrentCollaborator();
        // crypt oldPassword and compare password
        if(!BCryptHelper.check(changePasswordDto.getOldPassword(), curCollaborator.getPassword())){
            result.add("Mật khẩu hiện tại không chính xác");
        }

        // valid input new password
        if(!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmation())){
            result.add("Mật khẩu mới không khớp nhau");
        }

        // duplicate password
        if(BCryptHelper.check(changePasswordDto.getNewPassword(), curCollaborator.getPassword())) {
            result.add("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        return result;
    }


}
