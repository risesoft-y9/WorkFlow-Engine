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
  `id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u4e3b\u952e',
  `createTime` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '\u6284\u9001\u65f6\u95f4',
  `processInstanceId` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u6284\u9001\u7684\u6d41\u7a0b\u5b9e\u4f8b',
  `readTime` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '\u9605\u8bfb\u65f6\u95f4',
  `senderId` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '\u64cd\u4f5c\u4ebaId',
  `senderName` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '\u64cd\u4f5c\u4eba\u540d\u79f0',
  `status` int(11) DEFAULT NULL COMMENT '\u4f20\u9605\u72b6\u6001',
  `taskId` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u6284\u9001\u8282\u70b9\u7684\u4efb\u52a1Id',
  `tenantId` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u79df\u6237Id',
  `title` longtext COLLATE utf8_bin NOT NULL COMMENT '\u6284\u9001\u7684\u6807\u9898',
  `userId` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '\u6284\u9001\u76ee\u6807\u4eba\u5458Id',
  `userName` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u6284\u9001\u76ee\u6807\u4eba\u5458\u540d\u79f0',
  `itemId` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u4e8b\u9879\u552f\u4e00\u6807\u793a',
  `systemName` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '\u7cfb\u7edf\u82f1\u6587\u540d\u79f0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='\u6284\u9001\u4fe1\u606f\u8868';

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