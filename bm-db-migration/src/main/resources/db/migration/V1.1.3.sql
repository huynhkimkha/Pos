INSERT INTO settings(id, label, `key`, `value`)
VALUES(4, 'Giới hạn giới thiệu của cộng tác viên', 'COLLABORATOR_REF_LIMIT', '5'),
(5, 'Giới hạn giới thiệu của nhân viên', 'EMPLOYEE_REF_LIMIT', '5'),
(2, 'Hoa hồng giới thiệu nhân viên', 'EMPLOYEE_REF_BONUS', '20000'),
(3, 'Hoa hồng giới thiệu cộng tác viên', 'COLLABORATOR_REF_BONUS', '30000');

ALTER TABLE `invoice_commission`
ADD `apply_object` VARCHAR(225) NULL DEFAULT NULL AFTER `name`;
