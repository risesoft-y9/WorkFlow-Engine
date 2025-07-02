/*
 *  将“_Year4Table”替换成“_2021”,即为2021年年度表
 */

CREATE TABLE `act_hi_actinst_Year4Table`
(
    `ID_`                varchar(64) COLLATE utf8mb3_bin  NOT NULL,
    `REV_`               int                               DEFAULT '1',
    `PROC_DEF_ID_`       varchar(64) COLLATE utf8mb3_bin  NOT NULL,
    `PROC_INST_ID_`      varchar(64) COLLATE utf8mb3_bin  NOT NULL,
    `EXECUTION_ID_`      varchar(64) COLLATE utf8mb3_bin  NOT NULL,
    `ACT_ID_`            varchar(255) COLLATE utf8mb3_bin NOT NULL,
    `TASK_ID_`           varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `ACT_NAME_`          varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `ACT_TYPE_`          varchar(255) COLLATE utf8mb3_bin NOT NULL,
    `ASSIGNEE_`          varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `START_TIME_`        datetime(3) NOT NULL,
    `END_TIME_`          datetime(3) DEFAULT NULL,
    `TRANSACTION_ORDER_` int                               DEFAULT NULL,
    `DURATION_`          bigint                            DEFAULT NULL,
    `DELETE_REASON_`     varchar(4000) COLLATE utf8mb3_bin DEFAULT NULL,
    `TENANT_ID_`         varchar(255) COLLATE utf8mb3_bin  DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY                  `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
    KEY                  `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
    KEY                  `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`),
    KEY                  `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;


CREATE TABLE `act_hi_identitylink_Year4Table`
(
    `ID_`                  varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `GROUP_ID_`            varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `TYPE_`                varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `USER_ID_`             varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `TASK_ID_`             varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `CREATE_TIME_`         datetime(3) DEFAULT NULL,
    `PROC_INST_ID_`        varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SCOPE_ID_`            varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `SUB_SCOPE_ID_`        varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `SCOPE_TYPE_`          varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
    KEY                    `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

CREATE TABLE `act_hi_procinst_Year4Table`
(
    `ID_`                        varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `REV_`                       int                               DEFAULT '1',
    `PROC_INST_ID_`              varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `BUSINESS_KEY_`              varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `PROC_DEF_ID_`               varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `START_TIME_`                datetime(3) NOT NULL,
    `END_TIME_`                  datetime(3) DEFAULT NULL,
    `DURATION_`                  bigint                            DEFAULT NULL,
    `START_USER_ID_`             varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `START_ACT_ID_`              varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `END_ACT_ID_`                varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `DELETE_REASON_`             varchar(4000) COLLATE utf8mb3_bin DEFAULT NULL,
    `TENANT_ID_`                 varchar(255) COLLATE utf8mb3_bin  DEFAULT '',
    `NAME_`                      varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `CALLBACK_ID_`               varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `CALLBACK_TYPE_`             varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `REFERENCE_ID_`              varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `REFERENCE_TYPE_`            varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `PROPAGATED_STAGE_INST_ID_`  varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `BUSINESS_STATUS_`           varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
    KEY                          `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
    KEY                          `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;


CREATE TABLE `act_hi_taskinst_Year4Table`
(
    `ID_`                       varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `REV_`                      int                               DEFAULT '1',
    `PROC_DEF_ID_`              varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `TASK_DEF_ID_`              varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `TASK_DEF_KEY_`             varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `PROC_INST_ID_`             varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `EXECUTION_ID_`             varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `SCOPE_ID_`                 varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SUB_SCOPE_ID_`             varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SCOPE_TYPE_`               varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SCOPE_DEFINITION_ID_`      varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `PROPAGATED_STAGE_INST_ID_` varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `NAME_`                     varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `PARENT_TASK_ID_`           varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `DESCRIPTION_`              varchar(4000) COLLATE utf8mb3_bin DEFAULT NULL,
    `OWNER_`                    varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `ASSIGNEE_`                 varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `START_TIME_`               datetime(3) NOT NULL,
    `CLAIM_TIME_`               datetime(3) DEFAULT NULL,
    `END_TIME_`                 datetime(3) DEFAULT NULL,
    `DURATION_`                 bigint                            DEFAULT NULL,
    `DELETE_REASON_`            varchar(4000) COLLATE utf8mb3_bin DEFAULT NULL,
    `PRIORITY_`                 int                               DEFAULT NULL,
    `DUE_DATE_`                 datetime(3) DEFAULT NULL,
    `FORM_KEY_`                 varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `CATEGORY_`                 varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `TENANT_ID_`                varchar(255) COLLATE utf8mb3_bin  DEFAULT '',
    `LAST_UPDATED_TIME_`        datetime(3) DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                         `ACT_IDX_HI_TASK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
    KEY                         `ACT_IDX_HI_TASK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
    KEY                         `ACT_IDX_HI_TASK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
    KEY                         `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;


CREATE TABLE `act_hi_varinst_Year4Table`
(
    `ID_`                varchar(64) COLLATE utf8mb3_bin  NOT NULL,
    `REV_`               int                               DEFAULT '1',
    `PROC_INST_ID_`      varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `EXECUTION_ID_`      varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `TASK_ID_`           varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `NAME_`              varchar(255) COLLATE utf8mb3_bin NOT NULL,
    `VAR_TYPE_`          varchar(100) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SCOPE_ID_`          varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SUB_SCOPE_ID_`      varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SCOPE_TYPE_`        varchar(255) COLLATE utf8mb3_bin  DEFAULT NULL,
    `BYTEARRAY_ID_`      varchar(64) COLLATE utf8mb3_bin   DEFAULT NULL,
    `DOUBLE_` double DEFAULT NULL,
    `LONG_`              bigint                            DEFAULT NULL,
    `TEXT_`              varchar(4000) COLLATE utf8mb3_bin DEFAULT NULL,
    `TEXT2_`             varchar(4000) COLLATE utf8mb3_bin DEFAULT NULL,
    `CREATE_TIME_`       datetime(3) DEFAULT NULL,
    `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                  `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`),
    KEY                  `ACT_IDX_HI_VAR_SCOPE_ID_TYPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
    KEY                  `ACT_IDX_HI_VAR_SUB_ID_TYPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
    KEY                  `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
    KEY                  `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`),
    KEY                  `ACT_IDX_HI_PROCVAR_EXE` (`EXECUTION_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;


CREATE TABLE `act_ge_bytearray_Year4Table`
(
    `ID_`            varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `REV_`           int                              DEFAULT NULL,
    `NAME_`          varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `DEPLOYMENT_ID_` varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `BYTES_`         longblob,
    `GENERATED_`     tinyint                          DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY              `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;


CREATE TABLE `ff_act_ru_execution_Year4Table`
(
    `ID_`                        varchar(64) COLLATE utf8mb3_bin NOT NULL,
    `REV_`                       int                              DEFAULT NULL,
    `PROC_INST_ID_`              varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `BUSINESS_KEY_`              varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `PARENT_ID_`                 varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `PROC_DEF_ID_`               varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `SUPER_EXEC_`                varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `ROOT_PROC_INST_ID_`         varchar(64) COLLATE utf8mb3_bin  DEFAULT NULL,
    `ACT_ID_`                    varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `IS_ACTIVE_`                 tinyint                          DEFAULT NULL,
    `IS_CONCURRENT_`             tinyint                          DEFAULT NULL,
    `IS_SCOPE_`                  tinyint                          DEFAULT NULL,
    `IS_EVENT_SCOPE_`            tinyint                          DEFAULT NULL,
    `IS_MI_ROOT_`                tinyint                          DEFAULT NULL,
    `SUSPENSION_STATE_`          int                              DEFAULT NULL,
    `CACHED_ENT_STATE_`          int                              DEFAULT NULL,
    `TENANT_ID_`                 varchar(255) COLLATE utf8mb3_bin DEFAULT '',
    `NAME_`                      varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `START_ACT_ID_`              varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `START_TIME_`                datetime(3) DEFAULT NULL,
    `START_USER_ID_`             varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `LOCK_TIME_`                 timestamp(3) NULL DEFAULT NULL,
    `LOCK_OWNER_`                varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `IS_COUNT_ENABLED_`          tinyint                          DEFAULT NULL,
    `EVT_SUBSCR_COUNT_`          int                              DEFAULT NULL,
    `TASK_COUNT_`                int                              DEFAULT NULL,
    `JOB_COUNT_`                 int                              DEFAULT NULL,
    `TIMER_JOB_COUNT_`           int                              DEFAULT NULL,
    `SUSP_JOB_COUNT_`            int                              DEFAULT NULL,
    `DEADLETTER_JOB_COUNT_`      int                              DEFAULT NULL,
    `EXTERNAL_WORKER_JOB_COUNT_` int                              DEFAULT NULL,
    `VAR_COUNT_`                 int                              DEFAULT NULL,
    `ID_LINK_COUNT_`             int                              DEFAULT NULL,
    `CALLBACK_ID_`               varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `CALLBACK_TYPE_`             varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `REFERENCE_ID_`              varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `REFERENCE_TYPE_`            varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `PROPAGATED_STAGE_INST_ID_`  varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    `BUSINESS_STATUS_`           varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY                          `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
    KEY                          `ACT_IDC_EXEC_ROOT` (`ROOT_PROC_INST_ID_`),
    KEY                          `ACT_IDX_EXEC_REF_ID_` (`REFERENCE_ID_`),
    KEY                          `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
    KEY                          `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
    KEY                          `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
    KEY                          `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;