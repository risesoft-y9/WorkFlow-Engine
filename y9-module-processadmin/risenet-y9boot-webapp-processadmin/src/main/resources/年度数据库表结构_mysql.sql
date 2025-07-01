-- --------------------------------------
-- MYSQL


-- 将“_2020”替换成“_2021”,即为2021年年度表


-- --------------------------------------

SET
FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for act_ge_bytearray
-- ----------------------------

CREATE TABLE `act_ge_bytearray_2020`
(
    `ID_`            varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`           int(11) DEFAULT NULL,
    `NAME_`          varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `BYTES_`         longblob,
    `GENERATED_`     tinyint(4) DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY              `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for act_hi_actinst
-- ----------------------------

CREATE TABLE `act_hi_actinst_2020`
(
    `ID_`                varchar(64) COLLATE utf8_bin  NOT NULL,
    `REV_`               int(11) DEFAULT '1',
    `PROC_DEF_ID_`       varchar(64) COLLATE utf8_bin  NOT NULL,
    `PROC_INST_ID_`      varchar(64) COLLATE utf8_bin  NOT NULL,
    `EXECUTION_ID_`      varchar(64) COLLATE utf8_bin  NOT NULL,
    `ACT_ID_`            varchar(255) COLLATE utf8_bin NOT NULL,
    `TASK_ID_`           varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `ACT_NAME_`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ACT_TYPE_`          varchar(255) COLLATE utf8_bin NOT NULL,
    `ASSIGNEE_`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `START_TIME_`        datetime(3) NOT NULL,
    `END_TIME_`          datetime(3) DEFAULT NULL,
    `DURATION_`          bigint(20) DEFAULT NULL,
    `DELETE_REASON_`     varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TENANT_ID_`         varchar(255) COLLATE utf8_bin  DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY                  `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`) USING BTREE,
    KEY                  `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`) USING BTREE,
    KEY                  `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`) USING BTREE,
    KEY                  `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for act_hi_identitylink
-- ----------------------------

CREATE TABLE `act_hi_identitylink_2020`
(
    `ID_`                  varchar(64) COLLATE utf8_bin NOT NULL,
    `GROUP_ID_`            varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TYPE_`                varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `USER_ID_`             varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TASK_ID_`             varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `CREATE_TIME_`         datetime(3) DEFAULT NULL,
    `PROC_INST_ID_`        varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `SCOPE_ID_`            varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `SCOPE_TYPE_`          varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`) USING BTREE,
    KEY                    `ACT_IDX_HI_IDENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                    `ACT_IDX_HI_IDENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                    `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`) USING BTREE,
    KEY                    `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for act_hi_procinst
-- ----------------------------

CREATE TABLE `act_hi_procinst_2020`
(
    `ID_`                        varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`                       int(11) DEFAULT '1',
    `PROC_INST_ID_`              varchar(64) COLLATE utf8_bin NOT NULL,
    `BUSINESS_KEY_`              varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_DEF_ID_`               varchar(64) COLLATE utf8_bin NOT NULL,
    `START_TIME_`                datetime(3) NOT NULL,
    `END_TIME_`                  datetime(3) DEFAULT NULL,
    `DURATION_`                  bigint(20) DEFAULT NULL,
    `START_USER_ID_`             varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `START_ACT_ID_`              varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `END_ACT_ID_`                varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DELETE_REASON_`             varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TENANT_ID_`                 varchar(255) COLLATE utf8_bin  DEFAULT '',
    `NAME_`                      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `CALLBACK_ID_`               varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `CALLBACK_TYPE_`             varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`) USING BTREE,
    KEY                          `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`) USING BTREE,
    KEY                          `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for act_hi_taskinst
-- ----------------------------

CREATE TABLE `act_hi_taskinst_2020`
(
    `ID_`                  varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`                 int(11) DEFAULT '1',
    `PROC_DEF_ID_`         varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_DEF_ID_`         varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_DEF_KEY_`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_INST_ID_`        varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `EXECUTION_ID_`        varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `SCOPE_ID_`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SUB_SCOPE_ID_`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SCOPE_TYPE_`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `NAME_`                varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PARENT_TASK_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DESCRIPTION_`         varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `OWNER_`               varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ASSIGNEE_`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `START_TIME_`          datetime(3) NOT NULL,
    `CLAIM_TIME_`          datetime(3) DEFAULT NULL,
    `END_TIME_`            datetime(3) DEFAULT NULL,
    `DURATION_`            bigint(20) DEFAULT NULL,
    `DELETE_REASON_`       varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `PRIORITY_`            int(11) DEFAULT NULL,
    `DUE_DATE_`            datetime(3) DEFAULT NULL,
    `FORM_KEY_`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `CATEGORY_`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TENANT_ID_`           varchar(255) COLLATE utf8_bin  DEFAULT '',
    `LAST_UPDATED_TIME_`   datetime(3) DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                    `ACT_IDX_HI_TASK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                    `ACT_IDX_HI_TASK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                    `ACT_IDX_HI_TASK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                    `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for act_hi_varinst
-- ----------------------------

CREATE TABLE `act_hi_varinst_2020`
(
    `ID_`                varchar(64) COLLATE utf8_bin  NOT NULL,
    `REV_`               int(11) DEFAULT '1',
    `PROC_INST_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `EXECUTION_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_ID_`           varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `NAME_`              varchar(255) COLLATE utf8_bin NOT NULL,
    `VAR_TYPE_`          varchar(100) COLLATE utf8_bin  DEFAULT NULL,
    `SCOPE_ID_`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SUB_SCOPE_ID_`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SCOPE_TYPE_`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `BYTEARRAY_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DOUBLE_` double DEFAULT NULL,
    `LONG_`              bigint(20) DEFAULT NULL,
    `TEXT_`              varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TEXT2_`             varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `CREATE_TIME_`       datetime(3) DEFAULT NULL,
    `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                  `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`) USING BTREE,
    KEY                  `ACT_IDX_HI_VAR_SCOPE_ID_TYPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                  `ACT_IDX_HI_VAR_SUB_ID_TYPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
    KEY                  `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`) USING BTREE,
    KEY                  `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`) USING BTREE,
    KEY                  `ACT_IDX_HI_PROCVAR_EXE` (`EXECUTION_ID_`) USING BTREE,
    KEY                  `TASK_ID_NAME_` (`TASK_ID_`,`NAME_`) USING BTREE,
    KEY                  `PROC_INST_ID_NAME_` (`PROC_INST_ID_`,`NAME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ff_act_ru_execution_backup
-- ----------------------------

CREATE TABLE `ff_act_ru_execution_2020`
(
    `ID_`                   varchar(50) COLLATE utf8_bin NOT NULL,
    `ACT_ID_`               varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `IS_ACTIVE_`            bit(1)                        DEFAULT NULL,
    `BUSINESS_KEY_`         varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `CACHED_ENT_STATE_`     int(11) DEFAULT NULL,
    `CALLBACK_ID_`          varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `CALLBACK_TYPE_`        varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `IS_CONCURRENT_`        bit(1)                        DEFAULT NULL,
    `IS_COUNT_ENABLED_`     bit(1)                        DEFAULT NULL,
    `DEADLETTER_JOB_COUNT_` int(11) DEFAULT NULL,
    `IS_EVENT_SCOPE_`       bit(1)                        DEFAULT NULL,
    `EVT_SUBSCR_COUNT_`     int(11) DEFAULT NULL,
    `ID_LINK_COUNT_`        int(11) DEFAULT NULL,
    `JOB_COUNT_`            int(11) DEFAULT NULL,
    `LOCK_TIME_`            datetime(6) DEFAULT NULL,
    `IS_MI_ROOT_`           bit(1)                        DEFAULT NULL,
    `NAME_`                 varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `PARENT_ID_`            varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_DEF_ID_`          varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_INST_ID_`         varchar(50) COLLATE utf8_bin NOT NULL,
    `REV_`                  int(11) DEFAULT NULL,
    `ROOT_PROC_INST_ID_`    varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `IS_SCOPE_`             bit(1)                        DEFAULT NULL,
    `START_ACT_ID_`         varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `START_TIME_`           datetime(6) DEFAULT NULL,
    `START_USER_ID_`        varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `SUPER_EXEC_`           varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `SUSP_JOB_COUNT_`       int(11) DEFAULT NULL,
    `SUSPENSION_STATE_`     int(11) DEFAULT NULL,
    `TASK_COUNT_`           int(11) DEFAULT NULL,
    `TENANT_ID_`            varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TIMER_JOB_COUNT_`      int(11) DEFAULT NULL,
    `VAR_COUNT_`            int(11) DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                     `PROC_INST_ID_` (`PROC_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
