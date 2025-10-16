CREATE TABLE act_hi_actinst_Year4Table
(
    ID_                varchar(64)  NOT NULL,
    REV_               int          DEFAULT 1,
    PROC_DEF_ID_       varchar(64)  NOT NULL,
    PROC_INST_ID_      varchar(64)  NOT NULL,
    EXECUTION_ID_      varchar(64)  NOT NULL,
    ACT_ID_            varchar(255) NOT NULL,
    TASK_ID_           varchar(64),
    CALL_PROC_INST_ID_ varchar(64),
    ACT_NAME_          varchar(255),
    ACT_TYPE_          varchar(255) NOT NULL,
    ASSIGNEE_          varchar(255),
    START_TIME_        datetime     NOT NULL,
    END_TIME_          datetime,
    TRANSACTION_ORDER_ int,
    DURATION_          bigint,
    DELETE_REASON_     varchar(4000),
    TENANT_ID_         varchar(255) DEFAULT '',
    PRIMARY KEY (ID_)
);


CREATE TABLE act_hi_identitylink_Year4Table
(
    ID_                  varchar(64) NOT NULL,
    GROUP_ID_            varchar(255),
    TYPE_                varchar(255),
    USER_ID_             varchar(255),
    TASK_ID_             varchar(64),
    CREATE_TIME_         datetime,
    PROC_INST_ID_        varchar(64),
    SCOPE_ID_            varchar(255),
    SUB_SCOPE_ID_        varchar(255),
    SCOPE_TYPE_          varchar(255),
    SCOPE_DEFINITION_ID_ varchar(255),
    PRIMARY KEY (ID_)
);

CREATE TABLE act_hi_procinst_Year4Table
(
    ID_                        varchar(64) NOT NULL PRIMARY KEY,
    REV_                       int          DEFAULT 1,
    PROC_INST_ID_              varchar(64) NOT NULL unique,
    BUSINESS_KEY_              varchar(255),
    PROC_DEF_ID_               varchar(64) NOT NULL,
    START_TIME_                datetime    NOT NULL,
    END_TIME_                  datetime,
    DURATION_                  bigint,
    START_USER_ID_             varchar(255),
    START_ACT_ID_              varchar(255),
    END_ACT_ID_                varchar(255),
    SUPER_PROCESS_INSTANCE_ID_ varchar(64),
    DELETE_REASON_             varchar(4000),
    TENANT_ID_                 varchar(255) DEFAULT '',
    NAME_                      varchar(255),
    CALLBACK_ID_               varchar(255),
    CALLBACK_TYPE_             varchar(255),
    REFERENCE_ID_              varchar(255),
    REFERENCE_TYPE_            varchar(255),
    PROPAGATED_STAGE_INST_ID_  varchar(255),
    BUSINESS_STATUS_           varchar(255)
);


CREATE TABLE act_hi_taskinst_Year4Table
(
    ID_                       varchar(64) NOT NULL PRIMARY KEY,
    REV_                      int          DEFAULT 1,
    PROC_DEF_ID_              varchar(64),
    TASK_DEF_ID_              varchar(64),
    TASK_DEF_KEY_             varchar(255),
    PROC_INST_ID_             varchar(64),
    EXECUTION_ID_             varchar(64),
    SCOPE_ID_                 varchar(255),
    SUB_SCOPE_ID_             varchar(255),
    SCOPE_TYPE_               varchar(255),
    SCOPE_DEFINITION_ID_      varchar(255),
    PROPAGATED_STAGE_INST_ID_ varchar(255),
    NAME_                     varchar(255),
    PARENT_TASK_ID_           varchar(64),
    DESCRIPTION_              varchar(4000),
    OWNER_                    varchar(255),
    ASSIGNEE_                 varchar(255),
    START_TIME_               datetime    NOT NULL,
    CLAIM_TIME_               datetime,
    END_TIME_                 datetime,
    DURATION_                 bigint,
    DELETE_REASON_            varchar(4000),
    PRIORITY_                 int,
    DUE_DATE_                 datetime,
    FORM_KEY_                 varchar(255),
    CATEGORY_                 varchar(255),
    TENANT_ID_                varchar(255) DEFAULT '',
    LAST_UPDATED_TIME_        datetime
);


CREATE TABLE act_hi_varinst_Year4Table
(
    ID_                varchar(64)  NOT NULL PRIMARY KEY,
    REV_               int DEFAULT 1,
    PROC_INST_ID_      varchar(64),
    EXECUTION_ID_      varchar(64),
    TASK_ID_           varchar(64),
    NAME_              varchar(255) NOT NULL,
    VAR_TYPE_          varchar(100),
    SCOPE_ID_          varchar(255),
    SUB_SCOPE_ID_      varchar(255),
    SCOPE_TYPE_        varchar(255),
    BYTEARRAY_ID_      varchar(64),
    DOUBLE_ double,
    LONG_              bigint,
    TEXT_              varchar(4000),
    TEXT2_             varchar(4000),
    CREATE_TIME_       datetime,
    LAST_UPDATED_TIME_ datetime
);


CREATE TABLE act_ge_bytearray_Year4Table
(
    ID_            varchar(64) NOT NULL PRIMARY KEY,
    REV_           int,
    NAME_          varchar(255),
    DEPLOYMENT_ID_ varchar(64),
    BYTES_         clob,
    GENERATED_     tinyint
);


CREATE TABLE ff_act_ru_execution_Year4Table
(
    ID_                        varchar(64) NOT NULL PRIMARY KEY,
    REV_                       int,
    PROC_INST_ID_              varchar(64),
    BUSINESS_KEY_              varchar(255),
    PARENT_ID_                 varchar(64),
    PROC_DEF_ID_               varchar(64),
    SUPER_EXEC_                varchar(64),
    ROOT_PROC_INST_ID_         varchar(64),
    ACT_ID_                    varchar(255),
    IS_ACTIVE_                 tinyint,
    IS_CONCURRENT_             tinyint,
    IS_SCOPE_                  tinyint,
    IS_EVENT_SCOPE_            tinyint,
    IS_MI_ROOT_                tinyint,
    SUSPENSION_STATE_          int,
    CACHED_ENT_STATE_          int,
    TENANT_ID_                 varchar(255) DEFAULT '',
    NAME_                      varchar(255),
    START_ACT_ID_              varchar(255),
    START_TIME_                datetime,
    START_USER_ID_             varchar(255),
    LOCK_TIME_                 timestamp NULL,
    LOCK_OWNER_                varchar(255),
    IS_COUNT_ENABLED_          tinyint,
    EVT_SUBSCR_COUNT_          int,
    TASK_COUNT_                int,
    JOB_COUNT_                 int,
    TIMER_JOB_COUNT_           int,
    SUSP_JOB_COUNT_            int,
    DEADLETTER_JOB_COUNT_      int,
    EXTERNAL_WORKER_JOB_COUNT_ int,
    VAR_COUNT_                 int,
    ID_LINK_COUNT_             int,
    CALLBACK_ID_               varchar(255),
    CALLBACK_TYPE_             varchar(255),
    REFERENCE_ID_              varchar(255),
    REFERENCE_TYPE_            varchar(255),
    PROPAGATED_STAGE_INST_ID_  varchar(255),
    BUSINESS_STATUS_           varchar(255)
);