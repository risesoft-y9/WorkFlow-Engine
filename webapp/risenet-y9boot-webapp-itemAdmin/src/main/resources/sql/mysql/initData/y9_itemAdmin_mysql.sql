/*
Navicat MySQL Data Transfer

Source Server         : root
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : yousheng_new

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2018-12-07 11:26:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ff_approveitem
-- ----------------------------

CREATE TABLE `ff_approveitem` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `ACCOUNTABILITY` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `APPURL` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `BASICFORMID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `CREATERID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATERNAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTNAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXPIRED` int(11) DEFAULT NULL,
  `HANDELFORMID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ICONDATA` longtext COLLATE utf8_bin,
  `ICONID` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `ISDOCKING` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ISONLINE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `LEGALLIMIT` int(11) DEFAULT NULL,
  `NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `NATURE` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `REVISEDATE` datetime(6) DEFAULT NULL,
  `REVISERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `REVISERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `STARTER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `STARTERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `SYSLEVEL` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `SYSTEMNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `WORKFLOWGUID` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_approveitem
-- ----------------------------
INSERT INTO `ff_approveitem` VALUES ('1a578d2da8ad434794e40c88141ec182', '自由办件', 'http://www.youshengyun.com/flowableUI?itemId=1a578d2da8ad434794e40c88141ec182', '', '2018-03-23 15:09:28.000000', null, null, null, null, null, null, 0x2B4563626E65537548594C5163772F6A74576B354B4975326B697A4A37794F56746E5A6865532F47614D665A6739785773776B5170547061413045685931494A364B695A39314A55456B7072697746374F2F7531336E3958616E6A6152614979794D515A694954424D7863574E4F3179727545786A504D634D7455766469504E6938683346782F6A72774B6D616A3346744B546C63745A66372F417743334E503867766648754B6741414141424A52553545726B4A6767673D3D, null, '1', '0', null, '自由办件', null, null, null, null, '', null, '办件', 'flowableUI', '自由办件', 'ziyouliucheng');
INSERT INTO `ff_approveitem` VALUES ('2643cc601db044d792d58937f48d75c3', '我的工单', 'http://www.youshengyun.com/flowableUI?itemId=2643cc601db044d792d58937f48d75c3', null, '2018-05-13 23:59:25.000000', null, null, null, null, null, null, 0x2B4563626E65537548594C5163772F6A74576B354B4975326B697A4A37794F56746E5A6865532F47614D665A6739785773776B5170547061413045685931494A364B695A39314A55456B7072697746374F2F7531336E3958616E6A6152614979794D515A694954424D7863574E4F3179727545786A504D634D7455766469504E6938683346782F6A72774B6D616A3346744B546C63745A66372F417743334E503867766648754B6741414141424A52553545726B4A6767673D3D, null, '1', '0', null, '我的工单', null, null, null, null, null, null, '办件', 'flowableUI', '我的工单', 'gongdanguanli');

-- ----------------------------
-- Table structure for ff_associatedfile
-- ----------------------------

CREATE TABLE `ff_associatedfile` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `ASSOCIATEDID` longtext COLLATE utf8_bin,
  `CREATETIME` datetime(6) DEFAULT NULL,
  `PROCESSINSTANCEID` longtext COLLATE utf8_bin,
  `PROCESSSERIALNUMBER` longtext COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_associatedfile
-- ----------------------------

-- ----------------------------
-- Table structure for ff_attachment
-- ----------------------------

CREATE TABLE `ff_attachment` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `DELETETIME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  `DESCRIBES` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DETELEUSERID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `FILESOURCE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PERSONNAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `serialNumber` int(11) DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `UPLOADTIME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for ff_autoformsequence
-- ----------------------------

CREATE TABLE `ff_autoformsequence` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CALENDARYEAR` int(11) DEFAULT NULL,
  `CHARACTERVALUE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LABELNAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SEQUENCEVALUE` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_autoformsequence
-- ----------------------------

-- ----------------------------
-- Table structure for ff_buttonitembind
-- ----------------------------

CREATE TABLE `ff_buttonitembind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `BUTTONID` varchar(38) COLLATE utf8_bin NOT NULL,
  `BUTTONTYPE` int(11) NOT NULL,
  `CREATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(100) COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINEKEY` varchar(100) COLLATE utf8_bin NOT NULL,
  `ROLENAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKDEFKEY` varchar(100) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_buttonitembind
-- ----------------------------

-- ----------------------------
-- Table structure for ff_chaosong
-- ----------------------------

CREATE TABLE `ff_chaosong` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) COLLATE utf8_bin NOT NULL,
  `READTIME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `SENDERID` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `SENDERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `TASKID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TITLE` longtext COLLATE utf8_bin NOT NULL,
  `USERID` varchar(100) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_chaosong
-- ----------------------------

-- ----------------------------
-- Table structure for ff_commonbutton
-- ----------------------------

CREATE TABLE `ff_commonbutton` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CUSTOMID` varchar(50) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_m0fytub0rhit4v1cth2d423ie` (`CUSTOMID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_commonbutton
-- ----------------------------

-- ----------------------------
-- Table structure for ff_commonsentences
-- ----------------------------

CREATE TABLE `ff_commonsentences` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CONTENT` varchar(4000) COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_commonsentences
-- ----------------------------

-- ----------------------------
-- Table structure for ff_commonsentencesinit
-- ----------------------------

CREATE TABLE `ff_commonsentencesinit` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_commonsentencesinit
-- ----------------------------

-- ----------------------------
-- Table structure for ff_customgroup
-- ----------------------------

CREATE TABLE `ff_customgroup` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `createTime` datetime(6) NOT NULL,
  `groupName` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `itemId` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `modifyTime` datetime(6) DEFAULT NULL,
  `processDefinitionKey` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `shareId` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `shareName` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `tabIndex` int(11) DEFAULT NULL,
  `taskKey` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `tenantId` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `userId` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `userName` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_customgroup
-- ----------------------------

-- ----------------------------
-- Table structure for ff_custommember
-- ----------------------------

CREATE TABLE `ff_custommember` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `createTime` datetime(6) NOT NULL,
  `deptId` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `groupId` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `memberId` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `memberName` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `modifyTime` datetime(6) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `tabIndex` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_custommember
-- ----------------------------

-- ----------------------------
-- Table structure for ff_documentnumberdetail
-- ----------------------------

CREATE TABLE `ff_documentnumberdetail` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CALENDARYEAR` int(11) NOT NULL,
  `NUMLENGTH` int(11) DEFAULT NULL,
  `SEQUENCEINITVALUE` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_documentnumberdetail
-- ----------------------------

-- ----------------------------
-- Table structure for ff_documentword
-- ----------------------------

CREATE TABLE `ff_documentword` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `DELETED` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ISTAOHONG` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `SAVEDATE` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TITLE` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_documentword
-- ----------------------------

-- ----------------------------
-- Table structure for ff_documentword_his
-- ----------------------------

CREATE TABLE `ff_documentword_his` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `DELETED` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ISTAOHONG` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `SAVEDATE` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TITLE` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_documentword_his
-- ----------------------------

-- ----------------------------
-- Table structure for ff_draftentity
-- ----------------------------

CREATE TABLE `ff_draftentity` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATER` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `CREATERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DELFLAG` bit(1) DEFAULT NULL,
  `DOCNUMBER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DRAFTTIME` datetime(6) DEFAULT NULL,
  `ITEMID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONKEY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `SERIALNUMBER` int(11) DEFAULT NULL,
  `SYSTEMNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TITLE` varchar(1500) COLLATE utf8_bin DEFAULT NULL,
  `TYPE` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `URGENCY` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_draftentity
-- ----------------------------

-- ----------------------------
-- Table structure for ff_dynamicrole
-- ----------------------------

CREATE TABLE `ff_dynamicrole` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `CLASSPATH` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTWHERE` longtext COLLATE utf8_bin,
  `DESCRIPTION` longtext COLLATE utf8_bin,
  `GROUPWHERE` longtext COLLATE utf8_bin,
  `NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PERSONWHERE` longtext COLLATE utf8_bin,
  `POSITIONWHERE` longtext COLLATE utf8_bin,
  `SYSTEMNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TYPE` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_dynamicrole
-- ----------------------------
INSERT INTO `ff_dynamicrole` VALUES ('3c87335cd57640769f50c640f4e74819', 'net.risesoft.fileflow.service.dynamicRole.impl.AllDept4CurrentBureau', '', '', '', '当前委办局的所有科室和人员', '', '', 'itemAdmin', '0', '', '0');
INSERT INTO `ff_dynamicrole` VALUES ('50f161743c094cf2a32947cb0f987d90', 'net.risesoft.fileflow.service.dynamicRole.impl.CurrentDeptUsers', '', '', '', '当前部门人员', '', '', 'itemAdmin', '2', '', '0');
INSERT INTO `ff_dynamicrole` VALUES ('e727989785f14c2980e7b89b8f4f3ab0', 'net.risesoft.fileflow.service.dynamicRole.impl.CurrentOrg', '', '', '', '当前组织架构', '', '', 'itemAdmin', '1', '', '0');

-- ----------------------------
-- Table structure for ff_eformitembind
-- ----------------------------

CREATE TABLE `ff_eformitembind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FORMID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FORMNAME` varchar(100) COLLATE utf8_bin NOT NULL,
  `FORMURL` varchar(100) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(55) COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(255) COLLATE utf8_bin NOT NULL,
  `SHOWDOCUMENTTAB` bit(1) DEFAULT NULL,
  `SHOWFILETAB` bit(1) DEFAULT NULL,
  `SHOWHISTORYTAB` bit(1) DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKDEFKEY` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKbxncohi0tsrrvd7jdsnkxrljn` (`FORMID`,`ITEMID`,`PROCESSDEFINITIONID`,`TASKDEFKEY`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_eformitembind
-- ----------------------------
INSERT INTO `ff_eformitembind` VALUES ('0281809617bd4b188339f3654801de91', '2', '办件单(办件)', '/engine/TemplatePreview.pfm?temp_Id=2', '1a578d2da8ad434794e40c88141ec182', 'ziyouliucheng:1:ef3a9b92-2da1-11e8-981d-fa163e0a4cf1', '', '', '', '2', '', '');
INSERT INTO `ff_eformitembind` VALUES ('be7d1279b4ed48f28561162ce197dc65', '1', '基本信息(办件)', '/engine/TemplatePreview.pfm?temp_Id=1', '1a578d2da8ad434794e40c88141ec182', 'ziyouliucheng:1:ef3a9b92-2da1-11e8-981d-fa163e0a4cf1', '', '', '', '1', '', '');
INSERT INTO `ff_eformitembind` VALUES ('d210d0d3afd247e7815c111e540e2304', '15', '工单记录表', '/engine/TemplatePreview.pfm?temp_Id=15', '2643cc601db044d792d58937f48d75c3', 'gongdanguanli:3:72883868-6493-11e8-955c-fa163e77eb43', '\0', '', '\0', null, '', '');

-- ----------------------------
-- Table structure for ff_entrust
-- ----------------------------

CREATE TABLE `ff_entrust` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `ASSIGNEEID` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATTIME` varchar(255) COLLATE utf8_bin NOT NULL,
  `ENDTIME` varchar(30) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(255) COLLATE utf8_bin NOT NULL,
  `OWNERID` varchar(100) COLLATE utf8_bin NOT NULL,
  `STARTTIME` varchar(30) COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_entrust
-- ----------------------------

-- ----------------------------
-- Table structure for ff_entrust_history
-- ----------------------------

CREATE TABLE `ff_entrust_history` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `ASSIGNEEID` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATTIME` varchar(255) COLLATE utf8_bin NOT NULL,
  `ENDTIME` varchar(30) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(255) COLLATE utf8_bin NOT NULL,
  `OWNERID` varchar(100) COLLATE utf8_bin NOT NULL,
  `STARTTIME` varchar(30) COLLATE utf8_bin NOT NULL,
  `updateTime` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_entrust_history
-- ----------------------------

-- ----------------------------
-- Table structure for ff_extended_content
-- ----------------------------

CREATE TABLE `ff_extended_content` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CATEGORY` varchar(50) COLLATE utf8_bin NOT NULL,
  `CONTENT` varchar(4000) COLLATE utf8_bin NOT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `DEPARTMENTID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `MODIFYDATE` datetime(6) DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKID` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_extended_content
-- ----------------------------

-- ----------------------------
-- Table structure for ff_extraneteformitembind
-- ----------------------------

CREATE TABLE `ff_extraneteformitembind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FORMID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FORMNAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `FORMURL` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(55) COLLATE utf8_bin NOT NULL,
  `ITEMNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_extraneteformitembind
-- ----------------------------

-- ----------------------------
-- Table structure for ff_form_gongdanjilubiao
-- ----------------------------

CREATE TABLE `ff_form_gongdanjilubiao` (
  `guid` varchar(50) COLLATE utf8_bin NOT NULL,
  `processInstanceId` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `workOrderType` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `title` text COLLATE utf8_bin,
  `description` text COLLATE utf8_bin,
  `workLevel` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `workOrderGrade` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `productModel` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `number` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `creatime` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `mobile` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_form_gongdanjilubiao
-- ----------------------------

-- ----------------------------
-- Table structure for ff_form_ziyoubanjian
-- ----------------------------

CREATE TABLE `ff_form_ziyoubanjian` (
  `guid` varchar(38) COLLATE utf8_bin NOT NULL,
  `processInstanceId` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `number` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `wordSize` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `title` text COLLATE utf8_bin,
  `department` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `creater` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `createDate` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `contact` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `motive` text COLLATE utf8_bin,
  `send` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `level` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `signAndIssue` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `dateOfIssue` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `remarks` text COLLATE utf8_bin,
  `outline` text COLLATE utf8_bin,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_form_ziyoubanjian
-- ----------------------------

-- ----------------------------
-- Table structure for ff_itempermission
-- ----------------------------

CREATE TABLE `ff_itempermission` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `OBJECTGUID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OBJECTTYPE` int(11) DEFAULT NULL,
  `PRINCIPALGUID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PRINCIPALNAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PRINCIPALTYPE` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_itempermission
-- ----------------------------
INSERT INTO `ff_itempermission` VALUES ('34ef49a834a446d08ae56ec3d17e92cd', '2643cc601db044d792d58937f48d75c3:gongdanguanli:3:72883868-6493-11e8-955c-fa163e77eb43:bingxingbanli', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');
INSERT INTO `ff_itempermission` VALUES ('393b7310d9e545a18136b2fc4c8906e5', '2643cc601db044d792d58937f48d75c3:gongdanguanli:3:72883868-6493-11e8-955c-fa163e77eb43:zhipai', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');
INSERT INTO `ff_itempermission` VALUES ('3c15aa26e075470fb31ed3dbd785eb8e', '1a578d2da8ad434794e40c88141ec182:ziyouliucheng:1:ef3a9b92-2da1-11e8-981d-fa163e0a4cf1', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');
INSERT INTO `ff_itempermission` VALUES ('498d494d10b24752bec03b0930773d4a', '1a578d2da8ad434794e40c88141ec182:ziyouliucheng:1:ef3a9b92-2da1-11e8-981d-fa163e0a4cf1:freeFlowEndRole', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');
INSERT INTO `ff_itempermission` VALUES ('7345ca34f60b4ffeadf21158172bf0bd', '2643cc601db044d792d58937f48d75c3:gongdanguanli:3:72883868-6493-11e8-955c-fa163e77eb43:qicao', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');
INSERT INTO `ff_itempermission` VALUES ('86606264159944c6bd2229a5c656837f', '2643cc601db044d792d58937f48d75c3:gongdanguanli:3:72883868-6493-11e8-955c-fa163e77eb43:jiaohe', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');
INSERT INTO `ff_itempermission` VALUES ('876fe83df28b4e2d9cd50eee720f130e', '2643cc601db044d792d58937f48d75c3:gongdanguanli:3:72883868-6493-11e8-955c-fa163e77eb43:chuanxingbanli', '2', 'e727989785f14c2980e7b89b8f4f3ab0', '当前组织架构', '4', '');

-- ----------------------------
-- Table structure for ff_network_workorderinfo
-- ----------------------------

CREATE TABLE `ff_network_workorderinfo` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `ACCEPTANCENOTICE` longtext COLLATE utf8_bin,
  `ACCEPTANCETIME` datetime(6) DEFAULT NULL,
  `BUREAUGUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `BUREAUNAME` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `DECLARERGUID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DECLARERMOBILE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DECLARERPERSON` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` longtext COLLATE utf8_bin,
  `EMPLOYEEGUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `EMPLOYEEMOBILE` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `EMPLOYEENAME` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `ENDTIME` datetime(6) DEFAULT NULL,
  `FILENAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `FILEURL` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `FINISHNOTICE` longtext COLLATE utf8_bin,
  `ISINOROUT` int(11) DEFAULT NULL,
  `ITEMGUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `ITEMNAME` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONKEY` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PRODUCTMODEL` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `SUBMITTIME` datetime(6) DEFAULT NULL,
  `text` longtext COLLATE utf8_bin,
  `TITLE` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `WORKLEVEL` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `WORKORDERGRADE` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `WORKORDERNO` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `WORKORDERTYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_network_workorderinfo
-- ----------------------------

-- ----------------------------
-- Table structure for ff_objectpermission
-- ----------------------------

CREATE TABLE `ff_objectpermission` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `OBJECTGUID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OBJECTTYPE` int(11) DEFAULT NULL,
  `PRINCIPALGUID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PRINCIPALNAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PRINCIPALTYPE` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_objectpermission
-- ----------------------------

-- ----------------------------
-- Table structure for ff_opinion
-- ----------------------------

CREATE TABLE `ff_opinion` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CONTENT` longtext COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MODIFYDATE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OPINIONFRAMEMARK` varchar(50) COLLATE utf8_bin NOT NULL,
  `PROCESSINSTANCEID` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) COLLATE utf8_bin NOT NULL,
  `TASKID` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_opinion
-- ----------------------------

-- ----------------------------
-- Table structure for ff_opinionframe
-- ----------------------------

CREATE TABLE `ff_opinionframe` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DELETED` int(11) DEFAULT NULL,
  `MARK` varchar(50) COLLATE utf8_bin NOT NULL,
  `MODIFYDATE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_9xdkasvwyppxcgwpjp179f7lr` (`MARK`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_opinionframe
-- ----------------------------
INSERT INTO `ff_opinionframe` VALUES ('de3dfd1723bc47609783fc07adb47426', '2018-04-18 12:00:34', '0', 'personalComment', '2018-07-20 09:33:52', '个人意见', '', '', '');

-- ----------------------------
-- Table structure for ff_opinionframetaskbind
-- ----------------------------

CREATE TABLE `ff_opinionframetaskbind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(200) COLLATE utf8_bin NOT NULL,
  `MODIFYDATE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OPINIONFRAMEMARK` varchar(50) COLLATE utf8_bin NOT NULL,
  `OPINIONFRAMENAME` varchar(100) COLLATE utf8_bin NOT NULL,
  `ROLEID` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `ROLENAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `TASKDEFKEY` varchar(100) COLLATE utf8_bin NOT NULL,
  `TASKDEFNAME` varchar(100) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_opinionframetaskbind
-- ----------------------------
INSERT INTO `ff_opinionframetaskbind` VALUES ('052347fc5ef046baae3727010be6eeac', '2018-05-14 14:15:45', '2643cc601db044d792d58937f48d75c3', '2018-05-14 14:15:45', 'personalComment', '个人意见', '', '', 'bingxingbanli', '并行办理', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('0662e86c0b5043458c0f5fae36c01e9a', '2018-05-14 01:15:57', '2643cc601db044d792d58937f48d75c3', '2018-05-14 01:15:57', 'personalComment', '个人意见', '', '', 'qicao', '起草', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('3cb86ccaaf2841e09d1221c9cc49772d', '2018-04-18 12:01:27', '1a578d2da8ad434794e40c88141ec182', '2018-04-18 12:01:27', 'personalComment', '个人意见', '', '', 'chuanxingbanli', '串行办理', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('6679122f683547479b0d5e95e86941bc', '2018-05-14 14:15:15', '2643cc601db044d792d58937f48d75c3', '2018-05-14 14:15:15', 'personalComment', '个人意见', '', '', 'chuanxingbanli', '串行办理', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('b188ad03d8c24ab9be6fa1e91a49a2dc', '2018-05-14 14:16:30', '2643cc601db044d792d58937f48d75c3', '2018-05-14 14:16:30', 'personalComment', '个人意见', '', '', 'jiaohe', '校核', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('b63a4d7a986043aa94bb1f6d9f947e5c', '2018-04-18 12:01:46', '1a578d2da8ad434794e40c88141ec182', '2018-04-18 12:01:46', 'personalComment', '个人意见', '', '', 'bingxingbanli', '并行办理', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('c5d349a4ec6f4fae9a2e0c080a012f20', '2018-04-18 12:00:53', '1a578d2da8ad434794e40c88141ec182', '2018-04-18 12:00:53', 'personalComment', '个人意见', '', '', 'qicao', '起草', '', '', '');
INSERT INTO `ff_opinionframetaskbind` VALUES ('d1a9dfcb2c934028be825dcf96964826', '2018-05-14 14:16:09', '2643cc601db044d792d58937f48d75c3', '2018-05-14 14:16:09', 'personalComment', '个人意见', '', '', 'zhipai', '指派', '', '', '');

-- ----------------------------
-- Table structure for ff_organword
-- ----------------------------

CREATE TABLE `ff_organword` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CHARACTERVALUE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CUSTOM` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `INITNUMBER` int(11) NOT NULL,
  `TABINDEX` int(11) NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_organword
-- ----------------------------

-- ----------------------------
-- Table structure for ff_organword_usehistory
-- ----------------------------

CREATE TABLE `ff_organword_usehistory` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) COLLATE utf8_bin NOT NULL,
  `CUSTOM` varchar(50) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) COLLATE utf8_bin NOT NULL,
  `NUMBERSTRING` varchar(50) COLLATE utf8_bin NOT NULL,
  `PROCESSSERIALNUMBER` varchar(50) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_organword_usehistory
-- ----------------------------

-- ----------------------------
-- Table structure for ff_organworddetail
-- ----------------------------

CREATE TABLE `ff_organworddetail` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CHARACTERVALUE` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) COLLATE utf8_bin NOT NULL,
  `CUSTOM` varchar(50) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) COLLATE utf8_bin NOT NULL,
  `CURRENTNUMBER` int(11) NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `YEARS` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_organworddetail
-- ----------------------------

-- ----------------------------
-- Table structure for ff_organwordtaskbind
-- ----------------------------

CREATE TABLE `ff_organwordtaskbind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(50) COLLATE utf8_bin NOT NULL,
  `MODIFYDATE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ORGANWORDCUSTOM` varchar(50) COLLATE utf8_bin NOT NULL,
  `ORGANWORDNAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONKEY` varchar(200) COLLATE utf8_bin NOT NULL,
  `ROLEID` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `TASKDEFKEY` varchar(100) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_organwordtaskbind
-- ----------------------------

-- ----------------------------
-- Table structure for ff_printformitembind
-- ----------------------------

CREATE TABLE `ff_printformitembind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FORMID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FORMNAME` varchar(100) COLLATE utf8_bin NOT NULL,
  `FORMURL` varchar(100) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(55) COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_printformitembind
-- ----------------------------
INSERT INTO `ff_printformitembind` VALUES ('1f2871399fd445978802af5189339b01', '26', '基本信息(办件-打印)', '/engine/TemplatePreview.pfm?temp_Id=26', '1a578d2da8ad434794e40c88141ec182', '1', '');
INSERT INTO `ff_printformitembind` VALUES ('b9c2c41a765e48f19883af960ca255d6', '27', '办件单(办件-打印)', '/engine/TemplatePreview.pfm?temp_Id=27', '1a578d2da8ad434794e40c88141ec182', '2', '');

-- ----------------------------
-- Table structure for ff_processtrack
-- ----------------------------

CREATE TABLE `ff_processtrack` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `DESCRIBED` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `ENDTIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) COLLATE utf8_bin NOT NULL,
  `RECEIVERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `SENDERNAME` varchar(255) COLLATE utf8_bin NOT NULL,
  `STARTTIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TASKDEFNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_processtrack
-- ----------------------------

-- ----------------------------
-- Table structure for ff_procinstancerelationship
-- ----------------------------

CREATE TABLE `ff_procinstancerelationship` (
  `PROCINSTANCEID` varchar(255) COLLATE utf8_bin NOT NULL,
  `PARENTPROCINSTANCEID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROCDEFINITIONKEY` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`PROCINSTANCEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_procinstancerelationship
-- ----------------------------

-- ----------------------------
-- Table structure for ff_rankingno
-- ----------------------------

CREATE TABLE `ff_rankingno` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `NUMBERS` int(11) DEFAULT NULL,
  `PROCESSDEFINITIONKEY` varchar(50) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `YEARS` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_rankingno
-- ----------------------------

-- ----------------------------
-- Table structure for ff_rejectreason
-- ----------------------------

CREATE TABLE `ff_rejectreason` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `ACTION` int(11) DEFAULT NULL,
  `REASON` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `USERMOBILE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_rejectreason
-- ----------------------------

-- ----------------------------
-- Table structure for ff_reminder
-- ----------------------------

CREATE TABLE `ff_reminder` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATETIME` datetime(6) NOT NULL,
  `MODIFYTIME` datetime(6) NOT NULL,
  `MSGCONTENT` varchar(1000) COLLATE utf8_bin NOT NULL,
  `PROCINSTID` varchar(64) COLLATE utf8_bin NOT NULL,
  `READTIME` datetime(6) DEFAULT NULL,
  `REMINDER_MAKE_TYPE` int(11) NOT NULL,
  `REMINDER_SEND_TYPE` varchar(50) COLLATE utf8_bin NOT NULL,
  `SENDERID` varchar(38) COLLATE utf8_bin NOT NULL,
  `SENDERNAME` varchar(10) COLLATE utf8_bin NOT NULL,
  `TASKID` varchar(64) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_reminder
-- ----------------------------

-- ----------------------------
-- Table structure for ff_reminderdefine
-- ----------------------------

CREATE TABLE `ff_reminderdefine` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CUSTOMID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DAY_TYPE` int(11) NOT NULL,
  `MSGTEMPLATE` varchar(500) COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(64) COLLATE utf8_bin NOT NULL,
  `REMINDCOUNT` int(11) DEFAULT NULL,
  `REMINDINTERVAL` int(11) DEFAULT NULL,
  `REMINDSTART` int(11) DEFAULT NULL,
  `REMINDERDEFINE_AUTOMATIC` int(11) NOT NULL,
  `REMINDERDEFINE_TYPE` varchar(50) COLLATE utf8_bin NOT NULL,
  `TASKDEFKEY` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASKDURATION` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_reminderdefine
-- ----------------------------

-- ----------------------------
-- Table structure for ff_sendbutton
-- ----------------------------

CREATE TABLE `ff_sendbutton` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CUSTOMID` varchar(50) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_ghm5hk7xutjs5nelxswadg0qr` (`CUSTOMID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_sendbutton
-- ----------------------------

-- ----------------------------
-- Table structure for ff_sendreceive
-- ----------------------------

CREATE TABLE `ff_sendreceive` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `BUREAUID` varchar(50) COLLATE utf8_bin NOT NULL,
  `BUREAUNAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `DEPTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `GUIDPATH` varchar(800) COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_sendreceive
-- ----------------------------

-- ----------------------------
-- Table structure for ff_signaturepicture
-- ----------------------------

CREATE TABLE `ff_signaturepicture` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `MODIFYDATE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_sye8p7ll26kixfo1lolrd91f7` (`USERID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_signaturepicture
-- ----------------------------

-- ----------------------------
-- Table structure for ff_sp_attachment
-- ----------------------------

CREATE TABLE `ff_sp_attachment` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `FILEBYTES` longblob,
  `FILENAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `PARENTID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `UPLOADPERSON` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `UPLOADTIME` datetime(6) DEFAULT NULL,
  `UPLOADURL` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_sp_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for ff_sp_cooperationform
-- ----------------------------

CREATE TABLE `ff_sp_cooperationform` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CC_CITY` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `CC_CONTACTS` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `CC_CONTACTSEMAIL` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `CC_CONTACTSMOBILE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `CC_DECLARER` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CC_MAINBUSINESS` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `CC_PROVINCE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `CC_REMARK` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `CONTACTS` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CONTACTSEMAIL` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `CONTACTSMOBILE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` datetime(6) DEFAULT NULL,
  `DECLARER` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `FU_CONTACTS` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `FU_CONTACTSEMAIL` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `FU_CONTACTSMOBILE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `FU_DECLARER` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `FU_DESCRIPTION` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `SC_CONTACTS` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `SC_CONTACTSEMAIL` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `SC_CONTACTSMOBILE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `SC_DECLARER` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `SC_PRODUCTNAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `SC_SUCCESSCASE` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `TYPE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_sp_cooperationform
-- ----------------------------

-- ----------------------------
-- Table structure for ff_sp_declareinfo
-- ----------------------------

CREATE TABLE `ff_sp_declareinfo` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `ACCEPTANCENOTICE` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `ACCEPTANCETIME` datetime(6) DEFAULT NULL,
  `APPLYID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `AREA` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `BUREAUGUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `BUREAUNAME` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `DECLAREREMAIL` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DECLARERGUID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DECLARERMOBILE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DECLARERPERSON` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `EMPLOYEEGUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `EMPLOYEEMOBILE` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `EMPLOYEENAME` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `FILEBYTES` longblob,
  `FILENAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `FINISHNOTICE` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `ITEMGUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `ITEMNAME` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONKEY` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `SERIALNUMBER` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `SUBMITTIME` datetime(6) DEFAULT NULL,
  `TITLE` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_sp_declareinfo
-- ----------------------------

-- ----------------------------
-- Table structure for ff_sp_jobwantedform
-- ----------------------------

CREATE TABLE `ff_sp_jobwantedform` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `ADDRESS` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `APPLICANT` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `AREA` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `BIRTHDAY` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` datetime(6) DEFAULT NULL,
  `EDUCATION` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `EMAIL` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `ETHNIC` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `MARRIED` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `MOBILE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `POSITION` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `SALARY` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `SEX` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `SPECIALITY` varchar(2000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_sp_jobwantedform
-- ----------------------------

-- ----------------------------
-- Table structure for ff_speakinfo
-- ----------------------------

CREATE TABLE `ff_speakinfo` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CONTENT` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DELETED` bit(1) DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_speakinfo
-- ----------------------------

-- ----------------------------
-- Table structure for ff_tabentity
-- ----------------------------

CREATE TABLE `ff_tabentity` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `UPDATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `URL` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_tabentity
-- ----------------------------

-- ----------------------------
-- Table structure for ff_tabitembind
-- ----------------------------

CREATE TABLE `ff_tabitembind` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(100) COLLATE utf8_bin NOT NULL,
  `TABID` varchar(38) COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_tabitembind
-- ----------------------------

-- ----------------------------
-- Table structure for ff_taohongtemplate
-- ----------------------------

CREATE TABLE `ff_taohongtemplate` (
  `TEMPLATE_GUID` varchar(38) COLLATE utf8_bin NOT NULL,
  `BUREAU_GUID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `BUREAU_NAME` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TEMPLATE_CONTENT` longblob,
  `TEMPLATE_FILENAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `TEMPLATE_TYPE` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `uploadTime` datetime(6) NOT NULL,
  `USERID` varchar(38) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`TEMPLATE_GUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_taohongtemplate
-- ----------------------------

-- ----------------------------
-- Table structure for ff_taohongtemplatetype
-- ----------------------------

CREATE TABLE `ff_taohongtemplatetype` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TYPENAME` varchar(100) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_taohongtemplatetype
-- ----------------------------

-- ----------------------------
-- Table structure for ff_taskconf
-- ----------------------------

CREATE TABLE `ff_taskconf` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `OPERATION` varchar(400) COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONID` varchar(255) COLLATE utf8_bin NOT NULL,
  `ISSIGNOPINION` bit(1) DEFAULT NULL,
  `ISSPONSOR` bit(1) DEFAULT NULL,
  `TASKDEFKEY` varchar(255) COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_taskconf
-- ----------------------------

-- ----------------------------
-- Table structure for ff_wordtemplate
-- ----------------------------

CREATE TABLE `ff_wordtemplate` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `content` longblob,
  `DESCRIBES` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `FILEPATH` varchar(2000) COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PERSONNAME` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `UPLOADTIME` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_wordtemplate
-- ----------------------------

-- ----------------------------
-- Table structure for ff_wordtemplatebind
-- ----------------------------

CREATE TABLE `ff_wordtemplatebind` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TEMPLATEID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ff_wordtemplatebind
-- ----------------------------

