/*
Navicat MySQL Data Transfer

Source Server         : 新有生云
Source Server Version : 80018
Source Host           : 218.60.41.137:3306
Source Database       : t_0001_risesoft

Target Server Type    : MYSQL
Target Server Version : 80018
File Encoding         : 65001

Date: 2020-08-07 10:27:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ff_act_ru_execution_2020
-- ----------------------------
CREATE TABLE `ff_act_ru_execution_2020` (
  `ID_` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ACT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE_` bit(1) DEFAULT NULL,
  `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CACHED_ENT_STATE_` int(11) DEFAULT NULL,
  `CALLBACK_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `IS_CONCURRENT_` bit(1) DEFAULT NULL,
  `IS_COUNT_ENABLED_` bit(1) DEFAULT NULL,
  `DEADLETTER_JOB_COUNT_` int(11) DEFAULT NULL,
  `IS_EVENT_SCOPE_` bit(1) DEFAULT NULL,
  `EVT_SUBSCR_COUNT_` int(11) DEFAULT NULL,
  `ID_LINK_COUNT_` int(11) DEFAULT NULL,
  `JOB_COUNT_` int(11) DEFAULT NULL,
  `LOCK_TIME_` datetime(6) DEFAULT NULL,
  `IS_MI_ROOT_` bit(1) DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PARENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `IS_SCOPE_` bit(1) DEFAULT NULL,
  `START_ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(6) DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SUPER_EXEC_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SUSP_JOB_COUNT_` int(11) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TASK_COUNT_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TIMER_JOB_COUNT_` int(11) DEFAULT NULL,
  `VAR_COUNT_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `PROC_INST_ID_` (`PROC_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_approveitem
-- ----------------------------
CREATE TABLE `ff_approveitem` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ACCOUNTABILITY` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `APPURL` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `BASICFORMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `CREATERID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATERNAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTNAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `EXPIRED` int(11) DEFAULT NULL,
  `HANDELFORMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ICONDATA` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `ICONID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ISDOCKING` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ISONLINE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `LEGALLIMIT` int(11) DEFAULT NULL,
  `NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NATURE` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `REVISEDATE` datetime(6) DEFAULT NULL,
  `REVISERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `REVISERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STARTER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STARTERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SYSLEVEL` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SYSTEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TODOTASKURLPREFIX` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `WORKFLOWGUID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_associatedfile
-- ----------------------------
CREATE TABLE `ff_associatedfile` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ASSOCIATEDID` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `CREATETIME` datetime(6) DEFAULT NULL,
  `PROCESSINSTANCEID` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `PROCESSSERIALNUMBER` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_attachment
-- ----------------------------
CREATE TABLE `ff_attachment` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DELETETIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  `DEPTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DESCRIBES` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DETELEUSERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESOURCE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `serialNumber` int(11) DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPLOADTIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FULLPATH` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `REALFILENAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_autoformsequence
-- ----------------------------
CREATE TABLE `ff_autoformsequence` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CALENDARYEAR` int(11) DEFAULT NULL,
  `CHARACTERVALUE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `LABELNAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SEQUENCEVALUE` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_bookmarkbind
-- ----------------------------
CREATE TABLE `ff_bookmarkbind` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BOOKMARKNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BOOKMARKTYPE` int(11) NOT NULL,
  `COLUMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `WORDTEMPLATEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_calendarconfig
-- ----------------------------
CREATE TABLE `ff_calendarconfig` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `everyYearHoliday` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `weekend2WorkingDay` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `workingDay2Holiday` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `year` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_chaosong
-- ----------------------------
CREATE TABLE `ff_chaosong` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `READTIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SENDDEPTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SENDDEPTNAME` varchar(150) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SENDERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SENDERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `SYSTEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TITLE` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERDEPTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERDEPTNAME` varchar(150) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `opinionState` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `opinionContent` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `opinionGroup` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_commonbutton
-- ----------------------------
CREATE TABLE `ff_commonbutton` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CUSTOMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_m0fytub0rhit4v1cth2d423ie` (`CUSTOMID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_commonsentences
-- ----------------------------
CREATE TABLE `ff_commonsentences` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CONTENT` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_commonsentencesinit
-- ----------------------------
CREATE TABLE `ff_commonsentencesinit` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_documentnumberdetail
-- ----------------------------
CREATE TABLE `ff_documentnumberdetail` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CALENDARYEAR` int(11) NOT NULL,
  `NUMLENGTH` int(11) DEFAULT NULL,
  `SEQUENCEINITVALUE` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_documentword
-- ----------------------------
CREATE TABLE `ff_documentword` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DELETED` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ISTAOHONG` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SAVEDATE` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TITLE` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_documentword_his
-- ----------------------------
CREATE TABLE `ff_documentword_his` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DELETED` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ISTAOHONG` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SAVEDATE` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TITLE` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_draftentity
-- ----------------------------
CREATE TABLE `ff_draftentity` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATER` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DELFLAG` bit(1) DEFAULT NULL,
  `DOCNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DRAFTTIME` datetime(6) DEFAULT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONKEY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SERIALNUMBER` int(11) DEFAULT NULL,
  `SYSTEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TITLE` varchar(1500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TYPE` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `URGENCY` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NEIBU` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_dynamicrole
-- ----------------------------
CREATE TABLE `ff_dynamicrole` (
  `ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CLASSPATH` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USEPROCESSINSTANCEID` bit(1) DEFAULT b'0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_entrust
-- ----------------------------
CREATE TABLE `ff_entrust` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ASSIGNEEID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATTIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ENDTIME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `OWNERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `STARTTIME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_entrust_history
-- ----------------------------
CREATE TABLE `ff_entrust_history` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ASSIGNEEID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATTIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ENDTIME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `OWNERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `STARTTIME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `updateTime` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_entrustdetail
-- ----------------------------
CREATE TABLE `ff_entrustdetail` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `OWNERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TASKID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_extended_content
-- ----------------------------
CREATE TABLE `ff_extended_content` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CATEGORY` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CONTENT` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `DEPARTMENTID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPARTMENTNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `MODIFYDATE` datetime(6) DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_extraneteformitembind
-- ----------------------------
CREATE TABLE `ff_extraneteformitembind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMURL` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_button_role
-- ----------------------------
CREATE TABLE `ff_item_button_role` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMBUTTONID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ROLEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_buttonbind
-- ----------------------------
CREATE TABLE `ff_item_buttonbind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUTTONID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUTTONTYPE` int(11) NOT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKDEFKEY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_eformbind
-- ----------------------------
CREATE TABLE `ff_item_eformbind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMURL` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SHOWDOCUMENTTAB` bit(1) DEFAULT NULL,
  `SHOWFILETAB` bit(1) DEFAULT NULL,
  `SHOWHISTORYTAB` bit(1) DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKDEFKEY` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_opinionframe
-- ----------------------------
CREATE TABLE `ff_item_opinionframe` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `MODIFYDATE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `OPINIONFRAMEMARK` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `OPINIONFRAMENAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TASKDEFKEY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_opinionframe_role
-- ----------------------------
CREATE TABLE `ff_item_opinionframe_role` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMOPINIONFRAMEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ROLEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_organword
-- ----------------------------
CREATE TABLE `ff_item_organword` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `MODIFYDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ORGANWORDCUSTOM` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TASKDEFKEY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_organword_role
-- ----------------------------
CREATE TABLE `ff_item_organword_role` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMORGANWORDBINDID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ROLEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_permission
-- ----------------------------
CREATE TABLE `ff_item_permission` (
  `ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ROLEID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ROLETYPE` int(11) DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKDEFKEY` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_printform_bind
-- ----------------------------
CREATE TABLE `ff_item_printform_bind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMURL` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_printtemplate_bind
-- ----------------------------
CREATE TABLE `ff_item_printtemplate_bind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TEMPLATEID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TEMPLATENAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TEMPLATEURL` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TEMPLATETYPE` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_tabbind
-- ----------------------------
CREATE TABLE `ff_item_tabbind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_taskconf
-- ----------------------------
CREATE TABLE `ff_item_taskconf` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SIGNOPINION` bit(1) DEFAULT b'0',
  `SPONSOR` bit(1) NOT NULL DEFAULT b'0',
  `TASKDEFKEY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_viewconf
-- ----------------------------
CREATE TABLE `ff_item_viewconf` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `COLUMNNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DISPLAYALIGN` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DISPLAYNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DISPLAYWIDTH` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TABLENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `VIEWTYPE` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_wordtemplate_bind
-- ----------------------------
CREATE TABLE `ff_item_wordtemplate_bind` (
  `ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TEMPLATEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_item_y9formbind
-- ----------------------------
CREATE TABLE `ff_item_y9formbind` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `FORMNAME` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSDEFINITIONID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SHOWDOCUMENTTAB` bit(1) DEFAULT NULL,
  `SHOWFILETAB` bit(1) DEFAULT NULL,
  `SHOWHISTORYTAB` bit(1) DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TASKDEFKEY` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_office_follow
-- ----------------------------
CREATE TABLE `ff_office_follow` (
  `GUID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUREAUID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `BUREAUNAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DOCUMENTTITLE` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILETYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `handleTerm` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `JINJICHENGDU` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NUMBERS` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSSERIALNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `sendDept` varchar(150) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STARTTIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`GUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_opinion
-- ----------------------------
CREATE TABLE `ff_opinion` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `AGENTUSERDEPTID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `AGENTUSERDEPTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `AGENTUSERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `AGENTUSERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CONTENT` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPTID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ISAGENT` int(11) DEFAULT NULL,
  `MODIFYDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `OPINIONFRAMEMARK` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSINSTANCEID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TASKID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `POSITIONID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_opinionframe
-- ----------------------------
CREATE TABLE `ff_opinionframe` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DELETED` int(11) DEFAULT NULL,
  `MARK` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `MODIFYDATE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_9xdkasvwyppxcgwpjp179f7lr` (`MARK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_organword
-- ----------------------------
CREATE TABLE `ff_organword` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CUSTOM` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_2iva5hcg6g8q6f92h3cda8rqi` (`CUSTOM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_organword_detail
-- ----------------------------
CREATE TABLE `ff_organword_detail` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CHARACTERVALUE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CUSTOM` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CURRENTNUMBER` int(11) NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `YEARS` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_organword_propty
-- ----------------------------
CREATE TABLE `ff_organword_propty` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `INITNUMBER` int(11) NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ORGANWORDID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_organword_usehistory
-- ----------------------------
CREATE TABLE `ff_organword_usehistory` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CUSTOM` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NUMBERSTRING` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSSERIALNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_printtemplate
-- ----------------------------
CREATE TABLE `ff_printtemplate` (
  `ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `content` longblob,
  `DESCRIBES` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILEPATH` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPLOADTIME` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_process_param
-- ----------------------------
CREATE TABLE `ff_process_param` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUREAUIDS` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `COMPLETER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CUSTOMLEVEL` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CUSTOMNUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEPTIDS` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `isSendSms` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `isShuMing` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ITEMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ITEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSSERIALNUMBER` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SEARCHTERM` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `smsContent` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `smsPersonId` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `SYSTEMCNNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SYSTEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TITLE` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TODOTASKURLPREFIX` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SENDED` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `sponsorGuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STARTOR` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STARTORNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `index_001_processInstanceId` (`PROCESSINSTANCEID`),
  KEY `index_002_processSerialNumber` (`PROCESSSERIALNUMBER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_processtrack
-- ----------------------------
CREATE TABLE `ff_processtrack` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DESCRIBED` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ENDTIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `RECEIVERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SENDERNAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `STARTTIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TASKDEFNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_procinstancerelationship
-- ----------------------------
CREATE TABLE `ff_procinstancerelationship` (
  `PROCINSTANCEID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PARENTPROCINSTANCEID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCDEFINITIONKEY` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`PROCINSTANCEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_receivedepartment
-- ----------------------------
CREATE TABLE `ff_receivedepartment` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUREAUID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `DEPTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DEPTNAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PARENTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_receiveperson
-- ----------------------------
CREATE TABLE `ff_receiveperson` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` datetime(6) DEFAULT NULL,
  `DEPTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DEPTNAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PERSONDEPTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `RECEIVE` bit(1) DEFAULT NULL,
  `SEND` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_rejectreason
-- ----------------------------
CREATE TABLE `ff_rejectreason` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ACTION` int(11) DEFAULT NULL,
  `REASON` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TASKID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERMOBILE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_reminder
-- ----------------------------
CREATE TABLE `ff_reminder` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` datetime(6) NOT NULL,
  `MODIFYTIME` datetime(6) NOT NULL,
  `MSGCONTENT` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PROCINSTID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `READTIME` datetime(6) DEFAULT NULL,
  `REMINDER_MAKE_TYPE` int(11) NOT NULL,
  `REMINDER_SEND_TYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SENDERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `SENDERNAME` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TASKID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_sendbutton
-- ----------------------------
CREATE TABLE `ff_sendbutton` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CUSTOMID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_ghm5hk7xutjs5nelxswadg0qr` (`CUSTOMID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_signaturepicture
-- ----------------------------
CREATE TABLE `ff_signaturepicture` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATEDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESTOREID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `MODIFYDATE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_sye8p7ll26kixfo1lolrd91f7` (`USERID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- Table structure for ff_speakinfo
-- ----------------------------
CREATE TABLE `ff_speakinfo` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CONTENT` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DELETED` bit(1) DEFAULT NULL,
  `PROCESSINSTANCEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `READUSERID` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_tabentity
-- ----------------------------
CREATE TABLE `ff_tabentity` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `URL` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_taohongtemplate
-- ----------------------------
CREATE TABLE `ff_taohongtemplate` (
  `TEMPLATE_GUID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUREAU_GUID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `BUREAU_NAME` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TEMPLATE_CONTENT` longblob,
  `TEMPLATE_FILENAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TEMPLATE_TYPE` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `uploadTime` datetime(6) NOT NULL,
  `USERID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`TEMPLATE_GUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_taohongtemplatetype
-- ----------------------------
CREATE TABLE `ff_taohongtemplatetype` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUREAUID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABINDEX` int(11) DEFAULT NULL,
  `TYPENAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_wordtemplate
-- ----------------------------
CREATE TABLE `ff_wordtemplate` (
  `ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `BUREAUID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DESCRIBES` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILEPATH` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILESIZE` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PERSONNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPLOADTIME` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9_form_baoxiao
-- ----------------------------
CREATE TABLE `y9_form_baoxiao` (
  `guid` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `processInstanceId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `title` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '标题',
  `baoxiaoren` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '报销人',
  `baoxiaodanhao` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '报销单号',
  `bumenmingcheng` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '部门名称',
  `riqi` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '报销日期',
  `baoxiaozhaiyao` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '报销摘要',
  `baoxiaoleibie` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '报销类别',
  `danjuzhangshu` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '单据张数',
  `baoxiaozonge` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '报销总额',
  `daxiejine` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '大写金额',
  `beizhu` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `biaodantouName` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '报销头',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9_form_gongdanjilubiao
-- ----------------------------
CREATE TABLE `y9_form_gongdanjilubiao` (
  `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `processInstanceId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `workOrderType` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '工单类型',
  `title` text COLLATE utf8_bin COMMENT '标题',
  `description` text COLLATE utf8_bin COMMENT '工单描述',
  `workLevel` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '优先级',
  `status` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'STATUS',
  `workOrderGrade` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '严重程度',
  `productModel` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '产品模块',
  `number` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '工单编号',
  `creator` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `creatime` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建时间',
  `mobile` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '联系方式',
  `company` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `remarks` text COLLATE utf8_bin,
  `deptName` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '部门名称',
  `remark` text COLLATE utf8_bin,
  `systemId` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '系统id',
  `isvName` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '开发商名称',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9_form_workorder
-- ----------------------------
CREATE TABLE `y9_form_workorder` (
  `GUID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `QQ` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `createDate` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `createTime` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `handleType` varchar(5) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `handlerMobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `htmlDescription` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `mobile` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `number` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `processInstanceId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `realProcessInstanceId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `suggest` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tenantId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tenantName` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `title` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `urgency` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `userId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `userName` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `workOrderType` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `resultFeedback` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`GUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9_form_ziyoubanjian
-- ----------------------------
CREATE TABLE `y9_form_ziyoubanjian` (
  `guid` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `processInstanceId` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `number` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '文件编号',
  `type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '类型',
  `wordSize` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '字号',
  `title` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '标题',
  `department` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '创建部门',
  `creater` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `createDate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '创建时间',
  `contact` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '联系方式',
  `motive` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '主题词',
  `send` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '发送对象',
  `level` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '紧急程度',
  `signAndIssue` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '签发人',
  `dateOfIssue` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '签发日期',
  `remarks` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `outline` varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '文件概要',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_define
-- ----------------------------
CREATE TABLE `y9form_define` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CSSURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FILECONTENT` longblob,
  `FILENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMTYPE` int(11) DEFAULT NULL,
  `JSURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ORIGINALCONTENT` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `PERSONID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `RESULTCONTENT` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `SYSTEMCNNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SYSTEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TEMPLATETYPE` int(11) DEFAULT NULL,
  `TENANTID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPDATETIME` datetime(6) DEFAULT NULL,
  `INITDATAURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_element
-- ----------------------------
CREATE TABLE `y9form_element` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `COMPONENTPROPERTY` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `COMPONENTTYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ELEMENTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ELEMENTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ELEMENTTYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FIELDCNNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FIELDNAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STATUS` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLENAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_element_perm
-- ----------------------------
CREATE TABLE `y9form_element_perm` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ELEMENTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ELEMENTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ELEMENTTYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FORMID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `PROCESSDEFINITIONID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `READROLEID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `READROLENAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TASKDEFKEY` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `WRITEROLEID` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `WRITEROLENAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_optionclass
-- ----------------------------
CREATE TABLE `y9form_optionclass` (
  `TYPE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_optionvalue
-- ----------------------------
CREATE TABLE `y9form_optionvalue` (
  `ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CODE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TABINDEX` int(11) NOT NULL,
  `TYPE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `UPDATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `DEFAULTSELECTED` int(11) DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_table
-- ----------------------------
CREATE TABLE `y9form_table` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `OLDTABLENAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SYSTEMCNNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `SYSTEMNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLEALIAS` varchar(4) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLECNNAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLEMEMO` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLENAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLETYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_table_field
-- ----------------------------
CREATE TABLE `y9form_table_field` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `DISPLAYORDER` int(11) DEFAULT NULL,
  `FIELDCNNAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FIELDLENGTH` int(11) DEFAULT NULL,
  `FIELDNAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `FIELDTYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ISMAYNULL` int(11) DEFAULT NULL,
  `ISSYSTEMFIELD` int(11) DEFAULT NULL,
  `OLDFIELDNAME` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `STATE` int(11) DEFAULT NULL,
  `TABLEID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TABLENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for y9form_validtype
-- ----------------------------
CREATE TABLE `y9form_validtype` (
  `ID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `PERSONID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `TENANTID` varchar(38) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `UPDATETIME` datetime(6) DEFAULT NULL,
  `VALIDCNNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `VALIDCONTENT` longtext CHARACTER SET utf8 COLLATE utf8_bin,
  `VALIDNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `VALIDTYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `ff_remind_instance` (
  `ID` varchar(38) COLLATE utf8_bin NOT NULL,
  `CREATETIME` varchar(255) COLLATE utf8_bin NOT NULL,
  `PROCESSINSTANCEID` varchar(64) COLLATE utf8_bin NOT NULL,
  `REMIND_TYPE` varchar(50) COLLATE utf8_bin NOT NULL,
  `TASKID` longtext COLLATE utf8_bin,
  `TENANTID` varchar(50) COLLATE utf8_bin NOT NULL,
  `USERID` varchar(38) COLLATE utf8_bin NOT NULL,
  `arriveTaskKey` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `completeTaskKey` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
