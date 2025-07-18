/*
Navicat Oracle Data Transfer
Oracle Client Version : 11.2.0.3.0

Source Server         : 本地-y9_flowable
Source Server Version : 110200
Source Host           : 127.0.0.1:1521
Source Schema         : Y9_FLOWABLE

Target Server Type    : ORACLE
Target Server Version : 110200
File Encoding         : 65001

Date: 2019-05-21 18:31:07
*/


-- ----------------------------
-- Table structure for ACT_DE_MODEL
-- ----------------------------
CREATE TABLE "ACT_DE_MODEL"
(
    "ID"                VARCHAR2(255 BYTE) NOT NULL,
    "NAME"              VARCHAR2(400 BYTE) NOT NULL,
    "MODEL_KEY"         VARCHAR2(400 BYTE) NOT NULL,
    "DESCRIPTION"       VARCHAR2(4000 BYTE) NULL,
    "MODEL_COMMENT"     VARCHAR2(4000 BYTE) NULL,
    "CREATED"           TIMESTAMP(6) NULL,
    "CREATED_BY"        VARCHAR2(255 BYTE) NULL,
    "LAST_UPDATED"      TIMESTAMP(6) NULL,
    "LAST_UPDATED_BY"   VARCHAR2(255 BYTE) NULL,
    "VERSION"           NUMBER NULL,
    "MODEL_EDITOR_JSON" CLOB NULL,
    "THUMBNAIL"         BLOB NULL,
    "MODEL_TYPE"        NUMBER NULL,
    "TENANT_ID"         VARCHAR2(255 BYTE) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_DE_MODEL
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_DE_MODEL_HISTORY
-- ----------------------------
CREATE TABLE "ACT_DE_MODEL_HISTORY"
(
    "ID"                VARCHAR2(255 BYTE) NOT NULL,
    "NAME"              VARCHAR2(400 BYTE) NOT NULL,
    "MODEL_KEY"         VARCHAR2(400 BYTE) NOT NULL,
    "DESCRIPTION"       VARCHAR2(4000 BYTE) NULL,
    "MODEL_COMMENT"     VARCHAR2(4000 BYTE) NULL,
    "CREATED"           TIMESTAMP(6) NULL,
    "CREATED_BY"        VARCHAR2(255 BYTE) NULL,
    "LAST_UPDATED"      TIMESTAMP(6) NULL,
    "LAST_UPDATED_BY"   VARCHAR2(255 BYTE) NULL,
    "REMOVAL_DATE"      TIMESTAMP(6) NULL,
    "VERSION"           NUMBER NULL,
    "MODEL_EDITOR_JSON" CLOB NULL,
    "MODEL_ID"          VARCHAR2(255 BYTE) NOT NULL,
    "MODEL_TYPE"        NUMBER NULL,
    "TENANT_ID"         VARCHAR2(255 BYTE) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_DE_MODEL_HISTORY
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_DE_MODEL_RELATION
-- ----------------------------
CREATE TABLE "ACT_DE_MODEL_RELATION"
(
    "ID"              VARCHAR2(255 BYTE) NOT NULL,
    "PARENT_MODEL_ID" VARCHAR2(255 BYTE) NULL,
    "MODEL_ID"        VARCHAR2(255 BYTE) NULL,
    "RELATION_TYPE"   VARCHAR2(255 BYTE) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_DE_MODEL_RELATION
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_EVT_LOG
-- ----------------------------
CREATE TABLE "ACT_EVT_LOG"
(
    "LOG_NR_"       NUMBER(19) NOT NULL,
    "TYPE_"         NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"  NVARCHAR2(64) NULL,
    "PROC_INST_ID_" NVARCHAR2(64) NULL,
    "EXECUTION_ID_" NVARCHAR2(64) NULL,
    "TASK_ID_"      NVARCHAR2(64) NULL,
    "TIME_STAMP_"   TIMESTAMP(6) NOT NULL,
    "USER_ID_"      NVARCHAR2(255) NULL,
    "DATA_"         BLOB NULL,
    "LOCK_OWNER_"   NVARCHAR2(255) NULL,
    "LOCK_TIME_"    TIMESTAMP(6) NULL,
    "IS_PROCESSED_" NUMBER(3) DEFAULT 0  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_EVT_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_GE_BYTEARRAY
-- ----------------------------
CREATE TABLE "ACT_GE_BYTEARRAY"
(
    "ID_"            NVARCHAR2(64) NOT NULL,
    "REV_"           NUMBER NULL,
    "NAME_"          NVARCHAR2(255) NULL,
    "DEPLOYMENT_ID_" NVARCHAR2(64) NULL,
    "BYTES_"         BLOB NULL,
    "GENERATED_"     NUMBER(1) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_GE_BYTEARRAY
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_GE_PROPERTY
-- ----------------------------
CREATE TABLE "ACT_GE_PROPERTY"
(
    "NAME_"  NVARCHAR2(64) NOT NULL,
    "VALUE_" NVARCHAR2(300) NULL,
    "REV_"   NUMBER NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_GE_PROPERTY
-- ----------------------------
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('common.schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('next.dbid', '1', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('identitylink.schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('entitylink.schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('task.schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('variable.schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('job.schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('schema.version', '6.4.1.3', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('schema.history', 'create(6.4.1.3)', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('cfg.execution-related-entities-count', 'true', '1');
INSERT INTO "ACT_GE_PROPERTY"
VALUES ('cfg.task-related-entities-count', 'true', '1');

-- ----------------------------
-- Table structure for ACT_HI_ACTINST
-- ----------------------------
CREATE TABLE "ACT_HI_ACTINST"
(
    "ID_"                NVARCHAR2(64) NOT NULL,
    "REV_"               NUMBER DEFAULT 1 NULL,
    "PROC_DEF_ID_"       NVARCHAR2(64) NOT NULL,
    "PROC_INST_ID_"      NVARCHAR2(64) NOT NULL,
    "EXECUTION_ID_"      NVARCHAR2(64) NOT NULL,
    "ACT_ID_"            NVARCHAR2(255) NOT NULL,
    "TASK_ID_"           NVARCHAR2(64) NULL,
    "CALL_PROC_INST_ID_" NVARCHAR2(64) NULL,
    "ACT_NAME_"          NVARCHAR2(255) NULL,
    "ACT_TYPE_"          NVARCHAR2(255) NOT NULL,
    "ASSIGNEE_"          NVARCHAR2(255) NULL,
    "START_TIME_"        TIMESTAMP(6) NOT NULL,
    "END_TIME_"          TIMESTAMP(6) NULL,
    "DURATION_"          NUMBER(19) NULL,
    "DELETE_REASON_"     NVARCHAR2(2000) NULL,
    "TENANT_ID_"         NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_ACTINST
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_ATTACHMENT
-- ----------------------------
CREATE TABLE "ACT_HI_ATTACHMENT"
(
    "ID_"           NVARCHAR2(64) NOT NULL,
    "REV_"          NUMBER NULL,
    "USER_ID_"      NVARCHAR2(255) NULL,
    "NAME_"         NVARCHAR2(255) NULL,
    "DESCRIPTION_"  NVARCHAR2(2000) NULL,
    "TYPE_"         NVARCHAR2(255) NULL,
    "TASK_ID_"      NVARCHAR2(64) NULL,
    "PROC_INST_ID_" NVARCHAR2(64) NULL,
    "URL_"          NVARCHAR2(2000) NULL,
    "CONTENT_ID_"   NVARCHAR2(64) NULL,
    "TIME_"         TIMESTAMP(6) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_ATTACHMENT
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_COMMENT
-- ----------------------------
CREATE TABLE "ACT_HI_COMMENT"
(
    "ID_"           NVARCHAR2(64) NOT NULL,
    "TYPE_"         NVARCHAR2(255) NULL,
    "TIME_"         TIMESTAMP(6) NOT NULL,
    "USER_ID_"      NVARCHAR2(255) NULL,
    "TASK_ID_"      NVARCHAR2(64) NULL,
    "PROC_INST_ID_" NVARCHAR2(64) NULL,
    "ACTION_"       NVARCHAR2(255) NULL,
    "MESSAGE_"      NVARCHAR2(2000) NULL,
    "FULL_MSG_"     BLOB NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_COMMENT
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_DETAIL
-- ----------------------------
CREATE TABLE "ACT_HI_DETAIL"
(
    "ID_"           NVARCHAR2(64) NOT NULL,
    "TYPE_"         NVARCHAR2(255) NOT NULL,
    "PROC_INST_ID_" NVARCHAR2(64) NULL,
    "EXECUTION_ID_" NVARCHAR2(64) NULL,
    "TASK_ID_"      NVARCHAR2(64) NULL,
    "ACT_INST_ID_"  NVARCHAR2(64) NULL,
    "NAME_"         NVARCHAR2(255) NOT NULL,
    "VAR_TYPE_"     NVARCHAR2(64) NULL,
    "REV_"          NUMBER NULL,
    "TIME_"         TIMESTAMP(6) NOT NULL,
    "BYTEARRAY_ID_" NVARCHAR2(64) NULL,
    "DOUBLE_"       NUMBER NULL,
    "LONG_"         NUMBER(19) NULL,
    "TEXT_"         NVARCHAR2(2000) NULL,
    "TEXT2_"        NVARCHAR2(2000) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_DETAIL
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_ENTITYLINK
-- ----------------------------
CREATE TABLE "ACT_HI_ENTITYLINK"
(
    "ID_"                      NVARCHAR2(64) NOT NULL,
    "LINK_TYPE_"               NVARCHAR2(255) NULL,
    "CREATE_TIME_"             TIMESTAMP(6) NULL,
    "SCOPE_ID_"                NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"              NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_"     NVARCHAR2(255) NULL,
    "REF_SCOPE_ID_"            NVARCHAR2(255) NULL,
    "REF_SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "REF_SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "HIERARCHY_TYPE_"          NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_ENTITYLINK
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_IDENTITYLINK
-- ----------------------------
CREATE TABLE "ACT_HI_IDENTITYLINK"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "GROUP_ID_"            NVARCHAR2(255) NULL,
    "TYPE_"                NVARCHAR2(255) NULL,
    "USER_ID_"             NVARCHAR2(255) NULL,
    "TASK_ID_"             NVARCHAR2(64) NULL,
    "CREATE_TIME_"         TIMESTAMP(6) NULL,
    "PROC_INST_ID_"        NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_IDENTITYLINK
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_PROCINST
-- ----------------------------
CREATE TABLE "ACT_HI_PROCINST"
(
    "ID_"                        NVARCHAR2(64) NOT NULL,
    "REV_"                       NUMBER DEFAULT 1 NULL,
    "PROC_INST_ID_"              NVARCHAR2(64) NOT NULL,
    "BUSINESS_KEY_"              NVARCHAR2(255) NULL,
    "PROC_DEF_ID_"               NVARCHAR2(64) NOT NULL,
    "START_TIME_"                TIMESTAMP(6) NOT NULL,
    "END_TIME_"                  TIMESTAMP(6) NULL,
    "DURATION_"                  NUMBER(19) NULL,
    "START_USER_ID_"             NVARCHAR2(255) NULL,
    "START_ACT_ID_"              NVARCHAR2(255) NULL,
    "END_ACT_ID_"                NVARCHAR2(255) NULL,
    "SUPER_PROCESS_INSTANCE_ID_" NVARCHAR2(64) NULL,
    "DELETE_REASON_"             NVARCHAR2(2000) NULL,
    "TENANT_ID_"                 NVARCHAR2(255) DEFAULT ''  NULL,
    "NAME_"                      NVARCHAR2(255) NULL,
    "CALLBACK_ID_"               NVARCHAR2(255) NULL,
    "CALLBACK_TYPE_"             NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_PROCINST
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_TASKINST
-- ----------------------------
CREATE TABLE "ACT_HI_TASKINST"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER DEFAULT 1 NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "TASK_DEF_ID_"         NVARCHAR2(64) NULL,
    "TASK_DEF_KEY_"        NVARCHAR2(255) NULL,
    "PROC_INST_ID_"        NVARCHAR2(64) NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "PARENT_TASK_ID_"      NVARCHAR2(64) NULL,
    "NAME_"                NVARCHAR2(255) NULL,
    "DESCRIPTION_"         NVARCHAR2(2000) NULL,
    "OWNER_"               NVARCHAR2(255) NULL,
    "ASSIGNEE_"            NVARCHAR2(255) NULL,
    "START_TIME_"          TIMESTAMP(6) NOT NULL,
    "CLAIM_TIME_"          TIMESTAMP(6) NULL,
    "END_TIME_"            TIMESTAMP(6) NULL,
    "DURATION_"            NUMBER(19) NULL,
    "DELETE_REASON_"       NVARCHAR2(2000) NULL,
    "PRIORITY_"            NUMBER NULL,
    "DUE_DATE_"            TIMESTAMP(6) NULL,
    "FORM_KEY_"            NVARCHAR2(255) NULL,
    "CATEGORY_"            NVARCHAR2(255) NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL,
    "LAST_UPDATED_TIME_"   TIMESTAMP(6) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_TASKINST
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_TSK_LOG
-- ----------------------------
CREATE TABLE "ACT_HI_TSK_LOG"
(
    "ID_"                  NUMBER(19) NOT NULL,
    "TYPE_"                NVARCHAR2(64) NULL,
    "TASK_ID_"             NVARCHAR2(64) NOT NULL,
    "TIME_STAMP_"          TIMESTAMP(6) NOT NULL,
    "USER_ID_"             NVARCHAR2(255) NULL,
    "DATA_"                NVARCHAR2(2000) NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "PROC_INST_ID_"        NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_TSK_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_HI_VARINST
-- ----------------------------
CREATE TABLE "ACT_HI_VARINST"
(
    "ID_"                NVARCHAR2(64) NOT NULL,
    "REV_"               NUMBER DEFAULT 1 NULL,
    "PROC_INST_ID_"      NVARCHAR2(64) NULL,
    "EXECUTION_ID_"      NVARCHAR2(64) NULL,
    "TASK_ID_"           NVARCHAR2(64) NULL,
    "NAME_"              NVARCHAR2(255) NOT NULL,
    "VAR_TYPE_"          NVARCHAR2(100) NULL,
    "SCOPE_ID_"          NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"      NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"        NVARCHAR2(255) NULL,
    "BYTEARRAY_ID_"      NVARCHAR2(64) NULL,
    "DOUBLE_"            NUMBER NULL,
    "LONG_"              NUMBER(19) NULL,
    "TEXT_"              NVARCHAR2(2000) NULL,
    "TEXT2_"             NVARCHAR2(2000) NULL,
    "CREATE_TIME_"       TIMESTAMP(6) NULL,
    "LAST_UPDATED_TIME_" TIMESTAMP(6) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_HI_VARINST
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_PROCDEF_INFO
-- ----------------------------
CREATE TABLE "ACT_PROCDEF_INFO"
(
    "ID_"           NVARCHAR2(64) NOT NULL,
    "PROC_DEF_ID_"  NVARCHAR2(64) NOT NULL,
    "REV_"          NUMBER NULL,
    "INFO_JSON_ID_" NVARCHAR2(64) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_PROCDEF_INFO
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RE_DEPLOYMENT
-- ----------------------------
CREATE TABLE "ACT_RE_DEPLOYMENT"
(
    "ID_"                   NVARCHAR2(64) NOT NULL,
    "NAME_"                 NVARCHAR2(255) NULL,
    "CATEGORY_"             NVARCHAR2(255) NULL,
    "KEY_"                  NVARCHAR2(255) NULL,
    "TENANT_ID_"            NVARCHAR2(255) DEFAULT ''  NULL,
    "DEPLOY_TIME_"          TIMESTAMP(6) NULL,
    "DERIVED_FROM_"         NVARCHAR2(64) NULL,
    "DERIVED_FROM_ROOT_"    NVARCHAR2(64) NULL,
    "PARENT_DEPLOYMENT_ID_" NVARCHAR2(255) NULL,
    "ENGINE_VERSION_"       NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RE_DEPLOYMENT
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RE_MODEL
-- ----------------------------
CREATE TABLE "ACT_RE_MODEL"
(
    "ID_"                           NVARCHAR2(64) NOT NULL,
    "REV_"                          NUMBER NULL,
    "NAME_"                         NVARCHAR2(255) NULL,
    "KEY_"                          NVARCHAR2(255) NULL,
    "CATEGORY_"                     NVARCHAR2(255) NULL,
    "CREATE_TIME_"                  TIMESTAMP(6) NULL,
    "LAST_UPDATE_TIME_"             TIMESTAMP(6) NULL,
    "VERSION_"                      NUMBER NULL,
    "META_INFO_"                    NVARCHAR2(2000) NULL,
    "DEPLOYMENT_ID_"                NVARCHAR2(64) NULL,
    "EDITOR_SOURCE_VALUE_ID_"       NVARCHAR2(64) NULL,
    "EDITOR_SOURCE_EXTRA_VALUE_ID_" NVARCHAR2(64) NULL,
    "TENANT_ID_"                    NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RE_MODEL
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RE_PROCDEF
-- ----------------------------
CREATE TABLE "ACT_RE_PROCDEF"
(
    "ID_"                     NVARCHAR2(64) NOT NULL,
    "REV_"                    NUMBER NULL,
    "CATEGORY_"               NVARCHAR2(255) NULL,
    "NAME_"                   NVARCHAR2(255) NULL,
    "KEY_"                    NVARCHAR2(255) NOT NULL,
    "VERSION_"                NUMBER           NOT NULL,
    "DEPLOYMENT_ID_"          NVARCHAR2(64) NULL,
    "RESOURCE_NAME_"          NVARCHAR2(2000) NULL,
    "DGRM_RESOURCE_NAME_"     VARCHAR2(4000 BYTE) NULL,
    "DESCRIPTION_"            NVARCHAR2(2000) NULL,
    "HAS_START_FORM_KEY_"     NUMBER(1) NULL,
    "HAS_GRAPHICAL_NOTATION_" NUMBER(1) NULL,
    "SUSPENSION_STATE_"       NUMBER NULL,
    "TENANT_ID_"              NVARCHAR2(255) DEFAULT ''  NULL,
    "DERIVED_FROM_"           NVARCHAR2(64) NULL,
    "DERIVED_FROM_ROOT_"      NVARCHAR2(64) NULL,
    "DERIVED_VERSION_"        NUMBER DEFAULT 0 NOT NULL,
    "ENGINE_VERSION_"         NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RE_PROCDEF
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_ACTINST
-- ----------------------------
CREATE TABLE "ACT_RU_ACTINST"
(
    "ID_"                NVARCHAR2(64) NOT NULL,
    "REV_"               NUMBER DEFAULT 1 NULL,
    "PROC_DEF_ID_"       NVARCHAR2(64) NOT NULL,
    "PROC_INST_ID_"      NVARCHAR2(64) NOT NULL,
    "EXECUTION_ID_"      NVARCHAR2(64) NOT NULL,
    "ACT_ID_"            NVARCHAR2(255) NOT NULL,
    "TASK_ID_"           NVARCHAR2(64) NULL,
    "CALL_PROC_INST_ID_" NVARCHAR2(64) NULL,
    "ACT_NAME_"          NVARCHAR2(255) NULL,
    "ACT_TYPE_"          NVARCHAR2(255) NOT NULL,
    "ASSIGNEE_"          NVARCHAR2(255) NULL,
    "START_TIME_"        TIMESTAMP(6) NOT NULL,
    "END_TIME_"          TIMESTAMP(6) NULL,
    "DURATION_"          NUMBER(19) NULL,
    "DELETE_REASON_"     NVARCHAR2(2000) NULL,
    "TENANT_ID_"         NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_ACTINST
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_DEADLETTER_JOB
-- ----------------------------
CREATE TABLE "ACT_RU_DEADLETTER_JOB"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER NULL,
    "TYPE_"                NVARCHAR2(255) NOT NULL,
    "EXCLUSIVE_"           NUMBER(1) NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "PROCESS_INSTANCE_ID_" NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "EXCEPTION_STACK_ID_"  NVARCHAR2(64) NULL,
    "EXCEPTION_MSG_"       NVARCHAR2(2000) NULL,
    "DUEDATE_"             TIMESTAMP(6) NULL,
    "REPEAT_"              NVARCHAR2(255) NULL,
    "HANDLER_TYPE_"        NVARCHAR2(255) NULL,
    "HANDLER_CFG_"         NVARCHAR2(2000) NULL,
    "CUSTOM_VALUES_ID_"    NVARCHAR2(64) NULL,
    "CREATE_TIME_"         TIMESTAMP(6) NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_DEADLETTER_JOB
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_ENTITYLINK
-- ----------------------------
CREATE TABLE "ACT_RU_ENTITYLINK"
(
    "ID_"                      NVARCHAR2(64) NOT NULL,
    "REV_"                     NUMBER NULL,
    "CREATE_TIME_"             TIMESTAMP(6) NULL,
    "LINK_TYPE_"               NVARCHAR2(255) NULL,
    "SCOPE_ID_"                NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"              NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_"     NVARCHAR2(255) NULL,
    "REF_SCOPE_ID_"            NVARCHAR2(255) NULL,
    "REF_SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "REF_SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "HIERARCHY_TYPE_"          NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_ENTITYLINK
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_EVENT_SUBSCR
-- ----------------------------
CREATE TABLE "ACT_RU_EVENT_SUBSCR"
(
    "ID_"            NVARCHAR2(64) NOT NULL,
    "REV_"           NUMBER NULL,
    "EVENT_TYPE_"    NVARCHAR2(255) NOT NULL,
    "EVENT_NAME_"    NVARCHAR2(255) NULL,
    "EXECUTION_ID_"  NVARCHAR2(64) NULL,
    "PROC_INST_ID_"  NVARCHAR2(64) NULL,
    "ACTIVITY_ID_"   NVARCHAR2(64) NULL,
    "CONFIGURATION_" NVARCHAR2(255) NULL,
    "CREATED_"       TIMESTAMP(6) NOT NULL,
    "PROC_DEF_ID_"   NVARCHAR2(64) NULL,
    "TENANT_ID_"     NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_EVENT_SUBSCR
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_EXECUTION
-- ----------------------------
CREATE TABLE "ACT_RU_EXECUTION"
(
    "ID_"                   NVARCHAR2(64) NOT NULL,
    "REV_"                  NUMBER NULL,
    "PROC_INST_ID_"         NVARCHAR2(64) NULL,
    "BUSINESS_KEY_"         NVARCHAR2(255) NULL,
    "PARENT_ID_"            NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"          NVARCHAR2(64) NULL,
    "SUPER_EXEC_"           NVARCHAR2(64) NULL,
    "ROOT_PROC_INST_ID_"    NVARCHAR2(64) NULL,
    "ACT_ID_"               NVARCHAR2(255) NULL,
    "IS_ACTIVE_"            NUMBER(1) NULL,
    "IS_CONCURRENT_"        NUMBER(1) NULL,
    "IS_SCOPE_"             NUMBER(1) NULL,
    "IS_EVENT_SCOPE_"       NUMBER(1) NULL,
    "IS_MI_ROOT_"           NUMBER(1) NULL,
    "SUSPENSION_STATE_"     NUMBER NULL,
    "CACHED_ENT_STATE_"     NUMBER NULL,
    "TENANT_ID_"            NVARCHAR2(255) DEFAULT ''  NULL,
    "NAME_"                 NVARCHAR2(255) NULL,
    "START_ACT_ID_"         NVARCHAR2(255) NULL,
    "START_TIME_"           TIMESTAMP(6) NULL,
    "START_USER_ID_"        NVARCHAR2(255) NULL,
    "LOCK_TIME_"            TIMESTAMP(6) NULL,
    "IS_COUNT_ENABLED_"     NUMBER(1) NULL,
    "EVT_SUBSCR_COUNT_"     NUMBER NULL,
    "TASK_COUNT_"           NUMBER NULL,
    "JOB_COUNT_"            NUMBER NULL,
    "TIMER_JOB_COUNT_"      NUMBER NULL,
    "SUSP_JOB_COUNT_"       NUMBER NULL,
    "DEADLETTER_JOB_COUNT_" NUMBER NULL,
    "VAR_COUNT_"            NUMBER NULL,
    "ID_LINK_COUNT_"        NUMBER NULL,
    "CALLBACK_ID_"          NVARCHAR2(255) NULL,
    "CALLBACK_TYPE_"        NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_EXECUTION
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_HISTORY_JOB
-- ----------------------------
CREATE TABLE "ACT_RU_HISTORY_JOB"
(
    "ID_"                 NVARCHAR2(64) NOT NULL,
    "REV_"                NUMBER NULL,
    "LOCK_EXP_TIME_"      TIMESTAMP(6) NULL,
    "LOCK_OWNER_"         NVARCHAR2(255) NULL,
    "RETRIES_"            NUMBER NULL,
    "EXCEPTION_STACK_ID_" NVARCHAR2(64) NULL,
    "EXCEPTION_MSG_"      NVARCHAR2(2000) NULL,
    "HANDLER_TYPE_"       NVARCHAR2(255) NULL,
    "HANDLER_CFG_"        NVARCHAR2(2000) NULL,
    "CUSTOM_VALUES_ID_"   NVARCHAR2(64) NULL,
    "ADV_HANDLER_CFG_ID_" NVARCHAR2(64) NULL,
    "CREATE_TIME_"        TIMESTAMP(6) NULL,
    "SCOPE_TYPE_"         NVARCHAR2(255) NULL,
    "TENANT_ID_"          NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_HISTORY_JOB
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_IDENTITYLINK
-- ----------------------------
CREATE TABLE "ACT_RU_IDENTITYLINK"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER NULL,
    "GROUP_ID_"            NVARCHAR2(255) NULL,
    "TYPE_"                NVARCHAR2(255) NULL,
    "USER_ID_"             NVARCHAR2(255) NULL,
    "TASK_ID_"             NVARCHAR2(64) NULL,
    "PROC_INST_ID_"        NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_IDENTITYLINK
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_JOB
-- ----------------------------
CREATE TABLE "ACT_RU_JOB"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER NULL,
    "TYPE_"                NVARCHAR2(255) NOT NULL,
    "LOCK_EXP_TIME_"       TIMESTAMP(6) NULL,
    "LOCK_OWNER_"          NVARCHAR2(255) NULL,
    "EXCLUSIVE_"           NUMBER(1) NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "PROCESS_INSTANCE_ID_" NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "RETRIES_"             NUMBER NULL,
    "EXCEPTION_STACK_ID_"  NVARCHAR2(64) NULL,
    "EXCEPTION_MSG_"       NVARCHAR2(2000) NULL,
    "DUEDATE_"             TIMESTAMP(6) NULL,
    "REPEAT_"              NVARCHAR2(255) NULL,
    "HANDLER_TYPE_"        NVARCHAR2(255) NULL,
    "HANDLER_CFG_"         NVARCHAR2(2000) NULL,
    "CUSTOM_VALUES_ID_"    NVARCHAR2(64) NULL,
    "CREATE_TIME_"         TIMESTAMP(6) NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_JOB
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_SUSPENDED_JOB
-- ----------------------------
CREATE TABLE "ACT_RU_SUSPENDED_JOB"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER NULL,
    "TYPE_"                NVARCHAR2(255) NOT NULL,
    "EXCLUSIVE_"           NUMBER(1) NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "PROCESS_INSTANCE_ID_" NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "RETRIES_"             NUMBER NULL,
    "EXCEPTION_STACK_ID_"  NVARCHAR2(64) NULL,
    "EXCEPTION_MSG_"       NVARCHAR2(2000) NULL,
    "DUEDATE_"             TIMESTAMP(6) NULL,
    "REPEAT_"              NVARCHAR2(255) NULL,
    "HANDLER_TYPE_"        NVARCHAR2(255) NULL,
    "HANDLER_CFG_"         NVARCHAR2(2000) NULL,
    "CUSTOM_VALUES_ID_"    NVARCHAR2(64) NULL,
    "CREATE_TIME_"         TIMESTAMP(6) NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_SUSPENDED_JOB
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_TASK
-- ----------------------------
CREATE TABLE "ACT_RU_TASK"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "PROC_INST_ID_"        NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "TASK_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "NAME_"                NVARCHAR2(255) NULL,
    "PARENT_TASK_ID_"      NVARCHAR2(64) NULL,
    "DESCRIPTION_"         NVARCHAR2(2000) NULL,
    "TASK_DEF_KEY_"        NVARCHAR2(255) NULL,
    "OWNER_"               NVARCHAR2(255) NULL,
    "ASSIGNEE_"            NVARCHAR2(255) NULL,
    "DELEGATION_"          NVARCHAR2(64) NULL,
    "PRIORITY_"            NUMBER NULL,
    "CREATE_TIME_"         TIMESTAMP(6) NULL,
    "DUE_DATE_"            TIMESTAMP(6) NULL,
    "CATEGORY_"            NVARCHAR2(255) NULL,
    "SUSPENSION_STATE_"    NUMBER NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL,
    "FORM_KEY_"            NVARCHAR2(255) NULL,
    "CLAIM_TIME_"          TIMESTAMP(6) NULL,
    "IS_COUNT_ENABLED_"    NUMBER(1) NULL,
    "VAR_COUNT_"           NUMBER NULL,
    "ID_LINK_COUNT_"       NUMBER NULL,
    "SUB_TASK_COUNT_"      NUMBER NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_TASK
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_TIMER_JOB
-- ----------------------------
CREATE TABLE "ACT_RU_TIMER_JOB"
(
    "ID_"                  NVARCHAR2(64) NOT NULL,
    "REV_"                 NUMBER NULL,
    "TYPE_"                NVARCHAR2(255) NOT NULL,
    "LOCK_EXP_TIME_"       TIMESTAMP(6) NULL,
    "LOCK_OWNER_"          NVARCHAR2(255) NULL,
    "EXCLUSIVE_"           NUMBER(1) NULL,
    "EXECUTION_ID_"        NVARCHAR2(64) NULL,
    "PROCESS_INSTANCE_ID_" NVARCHAR2(64) NULL,
    "PROC_DEF_ID_"         NVARCHAR2(64) NULL,
    "SCOPE_ID_"            NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_"        NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"          NVARCHAR2(255) NULL,
    "SCOPE_DEFINITION_ID_" NVARCHAR2(255) NULL,
    "RETRIES_"             NUMBER NULL,
    "EXCEPTION_STACK_ID_"  NVARCHAR2(64) NULL,
    "EXCEPTION_MSG_"       NVARCHAR2(2000) NULL,
    "DUEDATE_"             TIMESTAMP(6) NULL,
    "REPEAT_"              NVARCHAR2(255) NULL,
    "HANDLER_TYPE_"        NVARCHAR2(255) NULL,
    "HANDLER_CFG_"         NVARCHAR2(2000) NULL,
    "CUSTOM_VALUES_ID_"    NVARCHAR2(64) NULL,
    "CREATE_TIME_"         TIMESTAMP(6) NULL,
    "TENANT_ID_"           NVARCHAR2(255) DEFAULT ''  NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_TIMER_JOB
-- ----------------------------

-- ----------------------------
-- Table structure for ACT_RU_VARIABLE
-- ----------------------------
CREATE TABLE "ACT_RU_VARIABLE"
(
    "ID_"           NVARCHAR2(64) NOT NULL,
    "REV_"          NUMBER NULL,
    "TYPE_"         NVARCHAR2(255) NOT NULL,
    "NAME_"         NVARCHAR2(255) NOT NULL,
    "EXECUTION_ID_" NVARCHAR2(64) NULL,
    "PROC_INST_ID_" NVARCHAR2(64) NULL,
    "TASK_ID_"      NVARCHAR2(64) NULL,
    "SCOPE_ID_"     NVARCHAR2(255) NULL,
    "SUB_SCOPE_ID_" NVARCHAR2(255) NULL,
    "SCOPE_TYPE_"   NVARCHAR2(255) NULL,
    "BYTEARRAY_ID_" NVARCHAR2(64) NULL,
    "DOUBLE_"       NUMBER NULL,
    "LONG_"         NUMBER(19) NULL,
    "TEXT_"         NVARCHAR2(2000) NULL,
    "TEXT2_"        NVARCHAR2(2000) NULL
) LOGGING
NOCOMPRESS
NOCACHE
;

-- ----------------------------
-- Records of ACT_RU_VARIABLE
-- ----------------------------

-- ----------------------------
-- Indexes structure for table ACT_DE_MODEL
-- ----------------------------
CREATE INDEX "IDX_PROC_MOD_CREATED"
    ON "ACT_DE_MODEL" ("CREATED_BY" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_DE_MODEL
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL"
    ADD CHECK ("ID" IS NOT NULL);
ALTER TABLE "ACT_DE_MODEL"
    ADD CHECK ("NAME" IS NOT NULL);
ALTER TABLE "ACT_DE_MODEL"
    ADD CHECK ("MODEL_KEY" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_DE_MODEL
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL"
    ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table ACT_DE_MODEL_HISTORY
-- ----------------------------
CREATE INDEX "IDX_PROC_MOD_HISTORY_PROC"
    ON "ACT_DE_MODEL_HISTORY" ("MODEL_ID" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_DE_MODEL_HISTORY
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL_HISTORY"
    ADD CHECK ("ID" IS NOT NULL);
ALTER TABLE "ACT_DE_MODEL_HISTORY"
    ADD CHECK ("NAME" IS NOT NULL);
ALTER TABLE "ACT_DE_MODEL_HISTORY"
    ADD CHECK ("MODEL_KEY" IS NOT NULL);
ALTER TABLE "ACT_DE_MODEL_HISTORY"
    ADD CHECK ("MODEL_ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_DE_MODEL_HISTORY
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL_HISTORY"
    ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table ACT_DE_MODEL_RELATION
-- ----------------------------

-- ----------------------------
-- Checks structure for table ACT_DE_MODEL_RELATION
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL_RELATION"
    ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_DE_MODEL_RELATION
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL_RELATION"
    ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table ACT_EVT_LOG
-- ----------------------------

-- ----------------------------
-- Checks structure for table ACT_EVT_LOG
-- ----------------------------
ALTER TABLE "ACT_EVT_LOG"
    ADD CHECK ("TIME_STAMP_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_EVT_LOG
-- ----------------------------
ALTER TABLE "ACT_EVT_LOG"
    ADD PRIMARY KEY ("LOG_NR_");

-- ----------------------------
-- Indexes structure for table ACT_GE_BYTEARRAY
-- ----------------------------
CREATE INDEX "ACT_IDX_BYTEAR_DEPL"
    ON "ACT_GE_BYTEARRAY" ("DEPLOYMENT_ID_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_GE_BYTEARRAY
-- ----------------------------
ALTER TABLE "ACT_GE_BYTEARRAY"
    ADD CHECK (GENERATED_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_GE_BYTEARRAY
-- ----------------------------
ALTER TABLE "ACT_GE_BYTEARRAY"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_GE_PROPERTY
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table ACT_GE_PROPERTY
-- ----------------------------
ALTER TABLE "ACT_GE_PROPERTY"
    ADD PRIMARY KEY ("NAME_");

-- ----------------------------
-- Indexes structure for table ACT_HI_ACTINST
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_ACT_INST_END"
    ON "ACT_HI_ACTINST" ("END_TIME_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_ACT_INST_EXEC"
    ON "ACT_HI_ACTINST" ("EXECUTION_ID_" ASC, "ACT_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_ACT_INST_PROCINST"
    ON "ACT_HI_ACTINST" ("PROC_INST_ID_" ASC, "ACT_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_ACT_INST_START"
    ON "ACT_HI_ACTINST" ("START_TIME_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_HI_ACTINST
-- ----------------------------
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("PROC_DEF_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("PROC_INST_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("EXECUTION_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("ACT_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("ACT_TYPE_" IS NOT NULL);
ALTER TABLE "ACT_HI_ACTINST"
    ADD CHECK ("START_TIME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_ACTINST
-- ----------------------------
ALTER TABLE "ACT_HI_ACTINST"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_ATTACHMENT
-- ----------------------------

-- ----------------------------
-- Checks structure for table ACT_HI_ATTACHMENT
-- ----------------------------
ALTER TABLE "ACT_HI_ATTACHMENT"
    ADD CHECK ("ID_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_ATTACHMENT
-- ----------------------------
ALTER TABLE "ACT_HI_ATTACHMENT"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_COMMENT
-- ----------------------------

-- ----------------------------
-- Checks structure for table ACT_HI_COMMENT
-- ----------------------------
ALTER TABLE "ACT_HI_COMMENT"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_COMMENT"
    ADD CHECK ("TIME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_COMMENT
-- ----------------------------
ALTER TABLE "ACT_HI_COMMENT"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_DETAIL
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_DETAIL_ACT_INST"
    ON "ACT_HI_DETAIL" ("ACT_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_DETAIL_NAME"
    ON "ACT_HI_DETAIL" ("NAME_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_DETAIL_PROC_INST"
    ON "ACT_HI_DETAIL" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_DETAIL_TASK_ID"
    ON "ACT_HI_DETAIL" ("TASK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_DETAIL_TIME"
    ON "ACT_HI_DETAIL" ("TIME_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_HI_DETAIL
-- ----------------------------
ALTER TABLE "ACT_HI_DETAIL"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_DETAIL"
    ADD CHECK ("TYPE_" IS NOT NULL);
ALTER TABLE "ACT_HI_DETAIL"
    ADD CHECK ("NAME_" IS NOT NULL);
ALTER TABLE "ACT_HI_DETAIL"
    ADD CHECK ("TIME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_DETAIL
-- ----------------------------
ALTER TABLE "ACT_HI_DETAIL"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_ENTITYLINK
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_ENT_LNK_SCOPE"
    ON "ACT_HI_ENTITYLINK" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC, "LINK_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_ENT_LNK_SCOPE_DEF"
    ON "ACT_HI_ENTITYLINK" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC, "LINK_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Primary Key structure for table ACT_HI_ENTITYLINK
-- ----------------------------
ALTER TABLE "ACT_HI_ENTITYLINK"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_IDENTITYLINK
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_IDENT_LNK_PROCINST"
    ON "ACT_HI_IDENTITYLINK" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_IDENT_LNK_SCOPE"
    ON "ACT_HI_IDENTITYLINK" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_IDENT_LNK_SCOPE_DEF"
    ON "ACT_HI_IDENTITYLINK" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_IDENT_LNK_TASK"
    ON "ACT_HI_IDENTITYLINK" ("TASK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_IDENT_LNK_USER"
    ON "ACT_HI_IDENTITYLINK" ("USER_ID_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Primary Key structure for table ACT_HI_IDENTITYLINK
-- ----------------------------
ALTER TABLE "ACT_HI_IDENTITYLINK"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_PROCINST
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_PRO_INST_END"
    ON "ACT_HI_PROCINST" ("END_TIME_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_PRO_I_BUSKEY"
    ON "ACT_HI_PROCINST" ("BUSINESS_KEY_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Uniques structure for table ACT_HI_PROCINST
-- ----------------------------
ALTER TABLE "ACT_HI_PROCINST"
    ADD UNIQUE ("PROC_INST_ID_");

-- ----------------------------
-- Checks structure for table ACT_HI_PROCINST
-- ----------------------------
ALTER TABLE "ACT_HI_PROCINST"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_PROCINST"
    ADD CHECK ("PROC_INST_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_PROCINST"
    ADD CHECK ("PROC_DEF_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_PROCINST"
    ADD CHECK ("START_TIME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_PROCINST
-- ----------------------------
ALTER TABLE "ACT_HI_PROCINST"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_TASKINST
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_TASK_INST_PROCINST"
    ON "ACT_HI_TASKINST" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_TASK_SCOPE"
    ON "ACT_HI_TASKINST" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_TASK_SCOPE_DEF"
    ON "ACT_HI_TASKINST" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_TASK_SUB_SCOPE"
    ON "ACT_HI_TASKINST" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_HI_TASKINST
-- ----------------------------
ALTER TABLE "ACT_HI_TASKINST"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_TASKINST"
    ADD CHECK ("START_TIME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_TASKINST
-- ----------------------------
ALTER TABLE "ACT_HI_TASKINST"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_TSK_LOG
-- ----------------------------

-- ----------------------------
-- Checks structure for table ACT_HI_TSK_LOG
-- ----------------------------
ALTER TABLE "ACT_HI_TSK_LOG"
    ADD CHECK ("TASK_ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_TSK_LOG"
    ADD CHECK ("TIME_STAMP_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_TSK_LOG
-- ----------------------------
ALTER TABLE "ACT_HI_TSK_LOG"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_HI_VARINST
-- ----------------------------
CREATE INDEX "ACT_IDX_HI_PROCVAR_EXE"
    ON "ACT_HI_VARINST" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_PROCVAR_NAME_TYPE"
    ON "ACT_HI_VARINST" ("NAME_" ASC, "VAR_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_PROCVAR_PROC_INST"
    ON "ACT_HI_VARINST" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_PROCVAR_TASK_ID"
    ON "ACT_HI_VARINST" ("TASK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_VAR_SCOPE_ID_TYPE"
    ON "ACT_HI_VARINST" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_HI_VAR_SUB_ID_TYPE"
    ON "ACT_HI_VARINST" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_HI_VARINST
-- ----------------------------
ALTER TABLE "ACT_HI_VARINST"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_HI_VARINST"
    ADD CHECK ("NAME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_HI_VARINST
-- ----------------------------
ALTER TABLE "ACT_HI_VARINST"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_PROCDEF_INFO
-- ----------------------------
CREATE INDEX "ACT_IDX_PROCDEF_INFO_JSON"
    ON "ACT_PROCDEF_INFO" ("INFO_JSON_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_PROCDEF_INFO_PROC"
    ON "ACT_PROCDEF_INFO" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Uniques structure for table ACT_PROCDEF_INFO
-- ----------------------------
ALTER TABLE "ACT_PROCDEF_INFO"
    ADD UNIQUE ("PROC_DEF_ID_");

-- ----------------------------
-- Checks structure for table ACT_PROCDEF_INFO
-- ----------------------------
ALTER TABLE "ACT_PROCDEF_INFO"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_PROCDEF_INFO"
    ADD CHECK ("PROC_DEF_ID_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_PROCDEF_INFO
-- ----------------------------
ALTER TABLE "ACT_PROCDEF_INFO"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RE_DEPLOYMENT
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table ACT_RE_DEPLOYMENT
-- ----------------------------
ALTER TABLE "ACT_RE_DEPLOYMENT"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RE_MODEL
-- ----------------------------
CREATE INDEX "ACT_IDX_MODEL_DEPLOYMENT"
    ON "ACT_RE_MODEL" ("DEPLOYMENT_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_MODEL_SOURCE"
    ON "ACT_RE_MODEL" ("EDITOR_SOURCE_VALUE_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_MODEL_SOURCE_EXTRA"
    ON "ACT_RE_MODEL" ("EDITOR_SOURCE_EXTRA_VALUE_ID_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RE_MODEL
-- ----------------------------
ALTER TABLE "ACT_RE_MODEL"
    ADD CHECK ("ID_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_RE_MODEL
-- ----------------------------
ALTER TABLE "ACT_RE_MODEL"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RE_PROCDEF
-- ----------------------------

-- ----------------------------
-- Uniques structure for table ACT_RE_PROCDEF
-- ----------------------------
ALTER TABLE "ACT_RE_PROCDEF"
    ADD UNIQUE ("KEY_", "VERSION_", "DERIVED_VERSION_", "TENANT_ID_");

-- ----------------------------
-- Checks structure for table ACT_RE_PROCDEF
-- ----------------------------
ALTER TABLE "ACT_RE_PROCDEF"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RE_PROCDEF"
    ADD CHECK ("KEY_" IS NOT NULL);
ALTER TABLE "ACT_RE_PROCDEF"
    ADD CHECK ("VERSION_" IS NOT NULL);
ALTER TABLE "ACT_RE_PROCDEF"
    ADD CHECK ("DERIVED_VERSION_" IS NOT NULL);
ALTER TABLE "ACT_RE_PROCDEF"
    ADD CHECK (HAS_START_FORM_KEY_ IN (1, 0));
ALTER TABLE "ACT_RE_PROCDEF"
    ADD CHECK (HAS_GRAPHICAL_NOTATION_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RE_PROCDEF
-- ----------------------------
ALTER TABLE "ACT_RE_PROCDEF"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_ACTINST
-- ----------------------------
CREATE INDEX "ACT_IDX_RU_ACTI_END"
    ON "ACT_RU_ACTINST" ("END_TIME_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_RU_ACTI_EXEC"
    ON "ACT_RU_ACTINST" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_RU_ACTI_EXEC_ACT"
    ON "ACT_RU_ACTINST" ("EXECUTION_ID_" ASC, "ACT_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_RU_ACTI_PROC"
    ON "ACT_RU_ACTINST" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_RU_ACTI_PROC_ACT"
    ON "ACT_RU_ACTINST" ("PROC_INST_ID_" ASC, "ACT_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_RU_ACTI_START"
    ON "ACT_RU_ACTINST" ("START_TIME_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_ACTINST
-- ----------------------------
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("PROC_DEF_ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("PROC_INST_ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("EXECUTION_ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("ACT_ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("ACT_TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_ACTINST"
    ADD CHECK ("START_TIME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_RU_ACTINST
-- ----------------------------
ALTER TABLE "ACT_RU_ACTINST"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_DEADLETTER_JOB
-- ----------------------------
CREATE INDEX "ACT_IDX_DJOB_CUSTOM_VAL_ID"
    ON "ACT_RU_DEADLETTER_JOB" ("CUSTOM_VALUES_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_EXCEPTION"
    ON "ACT_RU_DEADLETTER_JOB" ("EXCEPTION_STACK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_EXECUTION_ID"
    ON "ACT_RU_DEADLETTER_JOB" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_PROC_DEF_ID"
    ON "ACT_RU_DEADLETTER_JOB" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_PROC_INST_ID"
    ON "ACT_RU_DEADLETTER_JOB" ("PROCESS_INSTANCE_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_SCOPE"
    ON "ACT_RU_DEADLETTER_JOB" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_SCOPE_DEF"
    ON "ACT_RU_DEADLETTER_JOB" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_DJOB_SUB_SCOPE"
    ON "ACT_RU_DEADLETTER_JOB" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_DEADLETTER_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD CHECK ("TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD CHECK (EXCLUSIVE_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RU_DEADLETTER_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_ENTITYLINK
-- ----------------------------
CREATE INDEX "ACT_IDX_ENT_LNK_SCOPE"
    ON "ACT_RU_ENTITYLINK" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC, "LINK_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_ENT_LNK_SCOPE_DEF"
    ON "ACT_RU_ENTITYLINK" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC, "LINK_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Primary Key structure for table ACT_RU_ENTITYLINK
-- ----------------------------
ALTER TABLE "ACT_RU_ENTITYLINK"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_EVENT_SUBSCR
-- ----------------------------
CREATE INDEX "ACT_IDX_EVENT_SUBSCR"
    ON "ACT_RU_EVENT_SUBSCR" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_EVENT_SUBSCR_CONFIG_"
    ON "ACT_RU_EVENT_SUBSCR" ("CONFIGURATION_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_EVENT_SUBSCR
-- ----------------------------
ALTER TABLE "ACT_RU_EVENT_SUBSCR"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_EVENT_SUBSCR"
    ADD CHECK ("EVENT_TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_EVENT_SUBSCR"
    ADD CHECK ("CREATED_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_RU_EVENT_SUBSCR
-- ----------------------------
ALTER TABLE "ACT_RU_EVENT_SUBSCR"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_EXECUTION
-- ----------------------------
CREATE INDEX "ACT_IDX_EXEC_BUSKEY"
    ON "ACT_RU_EXECUTION" ("BUSINESS_KEY_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_EXEC_ROOT"
    ON "ACT_RU_EXECUTION" ("ROOT_PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_EXE_PARENT"
    ON "ACT_RU_EXECUTION" ("PARENT_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_EXE_PROCDEF"
    ON "ACT_RU_EXECUTION" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_EXE_PROCINST"
    ON "ACT_RU_EXECUTION" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_EXE_SUPER"
    ON "ACT_RU_EXECUTION" ("SUPER_EXEC_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_EXECUTION
-- ----------------------------
ALTER TABLE "ACT_RU_EXECUTION"
    ADD CHECK (IS_ACTIVE_ IN (1, 0));
ALTER TABLE "ACT_RU_EXECUTION"
    ADD CHECK (IS_CONCURRENT_ IN (1, 0));
ALTER TABLE "ACT_RU_EXECUTION"
    ADD CHECK (IS_SCOPE_ IN (1, 0));
ALTER TABLE "ACT_RU_EXECUTION"
    ADD CHECK (IS_EVENT_SCOPE_ IN (1, 0));
ALTER TABLE "ACT_RU_EXECUTION"
    ADD CHECK (IS_MI_ROOT_ IN (1, 0));
ALTER TABLE "ACT_RU_EXECUTION"
    ADD CHECK (IS_COUNT_ENABLED_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RU_EXECUTION
-- ----------------------------
ALTER TABLE "ACT_RU_EXECUTION"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_HISTORY_JOB
-- ----------------------------

-- ----------------------------
-- Checks structure for table ACT_RU_HISTORY_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_HISTORY_JOB"
    ADD CHECK ("ID_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_RU_HISTORY_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_HISTORY_JOB"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_IDENTITYLINK
-- ----------------------------
CREATE INDEX "ACT_IDX_ATHRZ_PROCEDEF"
    ON "ACT_RU_IDENTITYLINK" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_IDENT_LNK_GROUP"
    ON "ACT_RU_IDENTITYLINK" ("GROUP_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_IDENT_LNK_SCOPE"
    ON "ACT_RU_IDENTITYLINK" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_IDENT_LNK_SCOPE_DEF"
    ON "ACT_RU_IDENTITYLINK" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_IDENT_LNK_USER"
    ON "ACT_RU_IDENTITYLINK" ("USER_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_IDL_PROCINST"
    ON "ACT_RU_IDENTITYLINK" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TSKASS_TASK"
    ON "ACT_RU_IDENTITYLINK" ("TASK_ID_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Primary Key structure for table ACT_RU_IDENTITYLINK
-- ----------------------------
ALTER TABLE "ACT_RU_IDENTITYLINK"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_JOB
-- ----------------------------
CREATE INDEX "ACT_IDX_JOB_CUSTOM_VAL_ID"
    ON "ACT_RU_JOB" ("CUSTOM_VALUES_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_EXCEPTION"
    ON "ACT_RU_JOB" ("EXCEPTION_STACK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_EXECUTION_ID"
    ON "ACT_RU_JOB" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_PROC_DEF_ID"
    ON "ACT_RU_JOB" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_PROC_INST_ID"
    ON "ACT_RU_JOB" ("PROCESS_INSTANCE_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_SCOPE"
    ON "ACT_RU_JOB" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_SCOPE_DEF"
    ON "ACT_RU_JOB" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_JOB_SUB_SCOPE"
    ON "ACT_RU_JOB" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_JOB"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_JOB"
    ADD CHECK ("TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_JOB"
    ADD CHECK (EXCLUSIVE_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RU_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_JOB"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_SUSPENDED_JOB
-- ----------------------------
CREATE INDEX "ACT_IDX_SJOB_CUSTOM_VAL_ID"
    ON "ACT_RU_SUSPENDED_JOB" ("CUSTOM_VALUES_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_EXCEPTION"
    ON "ACT_RU_SUSPENDED_JOB" ("EXCEPTION_STACK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_EXECUTION_ID"
    ON "ACT_RU_SUSPENDED_JOB" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_PROC_DEF_ID"
    ON "ACT_RU_SUSPENDED_JOB" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_PROC_INST_ID"
    ON "ACT_RU_SUSPENDED_JOB" ("PROCESS_INSTANCE_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_SCOPE"
    ON "ACT_RU_SUSPENDED_JOB" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_SCOPE_DEF"
    ON "ACT_RU_SUSPENDED_JOB" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_SJOB_SUB_SCOPE"
    ON "ACT_RU_SUSPENDED_JOB" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_SUSPENDED_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD CHECK ("TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD CHECK (EXCLUSIVE_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RU_SUSPENDED_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_TASK
-- ----------------------------
CREATE INDEX "ACT_IDX_TASK_CREATE"
    ON "ACT_RU_TASK" ("CREATE_TIME_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TASK_EXEC"
    ON "ACT_RU_TASK" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TASK_PROCDEF"
    ON "ACT_RU_TASK" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TASK_PROCINST"
    ON "ACT_RU_TASK" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TASK_SCOPE"
    ON "ACT_RU_TASK" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TASK_SCOPE_DEF"
    ON "ACT_RU_TASK" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TASK_SUB_SCOPE"
    ON "ACT_RU_TASK" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_TASK
-- ----------------------------
ALTER TABLE "ACT_RU_TASK"
    ADD CHECK (IS_COUNT_ENABLED_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RU_TASK
-- ----------------------------
ALTER TABLE "ACT_RU_TASK"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_TIMER_JOB
-- ----------------------------
CREATE INDEX "ACT_IDX_TJOB_CUSTOM_VAL_ID"
    ON "ACT_RU_TIMER_JOB" ("CUSTOM_VALUES_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_EXCEPTION"
    ON "ACT_RU_TIMER_JOB" ("EXCEPTION_STACK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_EXECUTION_ID"
    ON "ACT_RU_TIMER_JOB" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_PROC_DEF_ID"
    ON "ACT_RU_TIMER_JOB" ("PROC_DEF_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_PROC_INST_ID"
    ON "ACT_RU_TIMER_JOB" ("PROCESS_INSTANCE_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_SCOPE"
    ON "ACT_RU_TIMER_JOB" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_SCOPE_DEF"
    ON "ACT_RU_TIMER_JOB" ("SCOPE_DEFINITION_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_TJOB_SUB_SCOPE"
    ON "ACT_RU_TIMER_JOB" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_TIMER_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD CHECK ("TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD CHECK (EXCLUSIVE_ IN (1, 0));

-- ----------------------------
-- Primary Key structure for table ACT_RU_TIMER_JOB
-- ----------------------------
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Indexes structure for table ACT_RU_VARIABLE
-- ----------------------------
CREATE INDEX "ACT_IDX_RU_VAR_SCOPE_ID_TYPE"
    ON "ACT_RU_VARIABLE" ("SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_RU_VAR_SUB_ID_TYPE"
    ON "ACT_RU_VARIABLE" ("SUB_SCOPE_ID_" ASC, "SCOPE_TYPE_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_VARIABLE_TASK_ID"
    ON "ACT_RU_VARIABLE" ("TASK_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_VAR_BYTEARRAY"
    ON "ACT_RU_VARIABLE" ("BYTEARRAY_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_VAR_EXE"
    ON "ACT_RU_VARIABLE" ("EXECUTION_ID_" ASC) LOGGING
VISIBLE;
CREATE INDEX "ACT_IDX_VAR_PROCINST"
    ON "ACT_RU_VARIABLE" ("PROC_INST_ID_" ASC) LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table ACT_RU_VARIABLE
-- ----------------------------
ALTER TABLE "ACT_RU_VARIABLE"
    ADD CHECK ("ID_" IS NOT NULL);
ALTER TABLE "ACT_RU_VARIABLE"
    ADD CHECK ("TYPE_" IS NOT NULL);
ALTER TABLE "ACT_RU_VARIABLE"
    ADD CHECK ("NAME_" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table ACT_RU_VARIABLE
-- ----------------------------
ALTER TABLE "ACT_RU_VARIABLE"
    ADD PRIMARY KEY ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_DE_MODEL_RELATION"
-- ----------------------------
ALTER TABLE "ACT_DE_MODEL_RELATION"
    ADD FOREIGN KEY ("MODEL_ID") REFERENCES "ACT_DE_MODEL" ("ID");
ALTER TABLE "ACT_DE_MODEL_RELATION"
    ADD FOREIGN KEY ("PARENT_MODEL_ID") REFERENCES "ACT_DE_MODEL" ("ID");

-- ----------------------------
-- Foreign Key structure for table "ACT_GE_BYTEARRAY"
-- ----------------------------
ALTER TABLE "ACT_GE_BYTEARRAY"
    ADD FOREIGN KEY ("DEPLOYMENT_ID_") REFERENCES "ACT_RE_DEPLOYMENT" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_PROCDEF_INFO"
-- ----------------------------
ALTER TABLE "ACT_PROCDEF_INFO"
    ADD FOREIGN KEY ("INFO_JSON_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_PROCDEF_INFO"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RE_MODEL"
-- ----------------------------
ALTER TABLE "ACT_RE_MODEL"
    ADD FOREIGN KEY ("DEPLOYMENT_ID_") REFERENCES "ACT_RE_DEPLOYMENT" ("ID_");
ALTER TABLE "ACT_RE_MODEL"
    ADD FOREIGN KEY ("EDITOR_SOURCE_VALUE_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RE_MODEL"
    ADD FOREIGN KEY ("EDITOR_SOURCE_EXTRA_VALUE_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_DEADLETTER_JOB"
-- ----------------------------
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD FOREIGN KEY ("CUSTOM_VALUES_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD FOREIGN KEY ("EXCEPTION_STACK_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD FOREIGN KEY ("PROCESS_INSTANCE_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_DEADLETTER_JOB"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_EVENT_SUBSCR"
-- ----------------------------
ALTER TABLE "ACT_RU_EVENT_SUBSCR"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_EXECUTION"
-- ----------------------------
ALTER TABLE "ACT_RU_EXECUTION"
    ADD FOREIGN KEY ("PARENT_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_EXECUTION"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");
ALTER TABLE "ACT_RU_EXECUTION"
    ADD FOREIGN KEY ("PROC_INST_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_EXECUTION"
    ADD FOREIGN KEY ("SUPER_EXEC_") REFERENCES "ACT_RU_EXECUTION" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_IDENTITYLINK"
-- ----------------------------
ALTER TABLE "ACT_RU_IDENTITYLINK"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");
ALTER TABLE "ACT_RU_IDENTITYLINK"
    ADD FOREIGN KEY ("PROC_INST_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_IDENTITYLINK"
    ADD FOREIGN KEY ("TASK_ID_") REFERENCES "ACT_RU_TASK" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_JOB"
-- ----------------------------
ALTER TABLE "ACT_RU_JOB"
    ADD FOREIGN KEY ("CUSTOM_VALUES_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_JOB"
    ADD FOREIGN KEY ("EXCEPTION_STACK_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_JOB"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_JOB"
    ADD FOREIGN KEY ("PROCESS_INSTANCE_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_JOB"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_SUSPENDED_JOB"
-- ----------------------------
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD FOREIGN KEY ("CUSTOM_VALUES_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD FOREIGN KEY ("EXCEPTION_STACK_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD FOREIGN KEY ("PROCESS_INSTANCE_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_SUSPENDED_JOB"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_TASK"
-- ----------------------------
ALTER TABLE "ACT_RU_TASK"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_TASK"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");
ALTER TABLE "ACT_RU_TASK"
    ADD FOREIGN KEY ("PROC_INST_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_TIMER_JOB"
-- ----------------------------
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD FOREIGN KEY ("CUSTOM_VALUES_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD FOREIGN KEY ("EXCEPTION_STACK_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD FOREIGN KEY ("PROCESS_INSTANCE_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_TIMER_JOB"
    ADD FOREIGN KEY ("PROC_DEF_ID_") REFERENCES "ACT_RE_PROCDEF" ("ID_");

-- ----------------------------
-- Foreign Key structure for table "ACT_RU_VARIABLE"
-- ----------------------------
ALTER TABLE "ACT_RU_VARIABLE"
    ADD FOREIGN KEY ("BYTEARRAY_ID_") REFERENCES "ACT_GE_BYTEARRAY" ("ID_");
ALTER TABLE "ACT_RU_VARIABLE"
    ADD FOREIGN KEY ("EXECUTION_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
ALTER TABLE "ACT_RU_VARIABLE"
    ADD FOREIGN KEY ("PROC_INST_ID_") REFERENCES "ACT_RU_EXECUTION" ("ID_");
