-- ----------------------------
-- Records of ff_wordtemplatebind
-- ----------------------------
DROP TRIGGER IF EXISTS `updateffDeclareinfoTrigger`;
DELIMITER ;;
CREATE TRIGGER `updateffDeclareinfoTrigger` AFTER UPDATE ON `ff_sp_declareinfo` FOR EACH ROW begin update `y9_risecloud`.`rs_risecloud_applylist` set STATUS = new.STATUS,fileBytes = new.fileBytes,fileName = new.fileName,acceptanceNotice = new.acceptanceNotice,finishNotice = new.finishNotice
   where applyId=new.applyId;
 end
;;
DELIMITER ;
