/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.flowable.common.engine.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.ArrayTypeHandler;
import org.apache.ibatis.type.BigDecimalTypeHandler;
import org.apache.ibatis.type.BlobInputStreamTypeHandler;
import org.apache.ibatis.type.BlobTypeHandler;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.ByteTypeHandler;
import org.apache.ibatis.type.ClobTypeHandler;
import org.apache.ibatis.type.DateOnlyTypeHandler;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.DoubleTypeHandler;
import org.apache.ibatis.type.FloatTypeHandler;
import org.apache.ibatis.type.IntegerTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LongTypeHandler;
import org.apache.ibatis.type.NClobTypeHandler;
import org.apache.ibatis.type.NStringTypeHandler;
import org.apache.ibatis.type.ShortTypeHandler;
import org.apache.ibatis.type.SqlxmlTypeHandler;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TimeOnlyTypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.engine.EngineLifecycleListener;
import org.flowable.common.engine.api.lock.LockManager;
import org.flowable.common.engine.impl.agenda.AgendaOperationExecutionListener;
import org.flowable.common.engine.impl.agenda.AgendaOperationRunner;
import org.flowable.common.engine.impl.cfg.CommandExecutorImpl;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.engine.impl.cfg.TransactionContextFactory;
import org.flowable.common.engine.impl.cfg.standalone.StandaloneMybatisTransactionContextFactory;
import org.flowable.common.engine.impl.db.CommonDbSchemaManager;
import org.flowable.common.engine.impl.db.DbSqlSessionFactory;
import org.flowable.common.engine.impl.db.FlowableStringTypeHandler;
import org.flowable.common.engine.impl.db.LogSqlExecutionTimePlugin;
import org.flowable.common.engine.impl.db.MybatisTypeAliasConfigurator;
import org.flowable.common.engine.impl.db.MybatisTypeHandlerConfigurator;
import org.flowable.common.engine.impl.db.SchemaManager;
import org.flowable.common.engine.impl.db.SchemaManagerDatabaseConfiguration;
import org.flowable.common.engine.impl.db.SchemaManagerDatabaseConfigurationSessionFactory;
import org.flowable.common.engine.impl.db.SchemaOperationsEngineBuild;
import org.flowable.common.engine.impl.event.EventDispatchAction;
import org.flowable.common.engine.impl.event.FlowableEventDispatcherImpl;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandConfig;
import org.flowable.common.engine.impl.interceptor.CommandContextFactory;
import org.flowable.common.engine.impl.interceptor.CommandContextInterceptor;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.common.engine.impl.interceptor.CommandInterceptor;
import org.flowable.common.engine.impl.interceptor.CrDbRetryInterceptor;
import org.flowable.common.engine.impl.interceptor.DefaultCommandInvoker;
import org.flowable.common.engine.impl.interceptor.LogInterceptor;
import org.flowable.common.engine.impl.interceptor.SessionFactory;
import org.flowable.common.engine.impl.interceptor.TransactionContextInterceptor;
import org.flowable.common.engine.impl.lock.LockManagerImpl;
import org.flowable.common.engine.impl.logging.LoggingListener;
import org.flowable.common.engine.impl.logging.LoggingSession;
import org.flowable.common.engine.impl.logging.LoggingSessionFactory;
import org.flowable.common.engine.impl.persistence.GenericManagerFactory;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.common.engine.impl.persistence.cache.EntityCache;
import org.flowable.common.engine.impl.persistence.cache.EntityCacheImpl;
import org.flowable.common.engine.impl.persistence.entity.ByteArrayEntityManager;
import org.flowable.common.engine.impl.persistence.entity.ByteArrayEntityManagerImpl;
import org.flowable.common.engine.impl.persistence.entity.Entity;
import org.flowable.common.engine.impl.persistence.entity.PropertyEntityManager;
import org.flowable.common.engine.impl.persistence.entity.PropertyEntityManagerImpl;
import org.flowable.common.engine.impl.persistence.entity.TableDataManager;
import org.flowable.common.engine.impl.persistence.entity.TableDataManagerImpl;
import org.flowable.common.engine.impl.persistence.entity.data.ByteArrayDataManager;
import org.flowable.common.engine.impl.persistence.entity.data.PropertyDataManager;
import org.flowable.common.engine.impl.persistence.entity.data.impl.MybatisByteArrayDataManager;
import org.flowable.common.engine.impl.persistence.entity.data.impl.MybatisPropertyDataManager;
import org.flowable.common.engine.impl.runtime.Clock;
import org.flowable.common.engine.impl.service.CommonEngineServiceImpl;
import org.flowable.common.engine.impl.util.DefaultClockImpl;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.common.engine.impl.util.ReflectUtil;
import org.flowable.eventregistry.api.EventRegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.risesoft.util.Y9DbUtil;

public abstract class AbstractEngineConfiguration {

    /** The tenant id indicating 'no tenant' */
    public static final String NO_TENANT_ID = "";

    /**
     * Checks the version of the DB schema against the library when the form engine is being created and throws an
     * exception if the versions don't match.
     */
    public static final String DB_SCHEMA_UPDATE_FALSE = "false";

    public static final String DB_SCHEMA_UPDATE_CREATE = "create";
    public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";
    /**
     * Creates the schema when the form engine is being created and drops the schema when the form engine is being
     * closed.
     */
    public static final String DB_SCHEMA_UPDATE_DROP_CREATE = "drop-create";

    /**
     * Upon building of the process engine, a check is performed and an update of the schema is performed if it is
     * necessary.
     */
    public static final String DB_SCHEMA_UPDATE_TRUE = "true";

    public static final String PRODUCT_NAME_POSTGRES = "PostgreSQL";

    public static final String PRODUCT_NAME_CRDB = "CockroachDB";

    public static final String DATABASE_TYPE_H2 = "h2";
    public static final String DATABASE_TYPE_HSQL = "hsql";
    public static final String DATABASE_TYPE_MYSQL = "mysql";
    public static final String DATABASE_TYPE_ORACLE = "oracle";
    public static final String DATABASE_TYPE_POSTGRES = "postgres";
    public static final String DATABASE_TYPE_MSSQL = "mssql";
    public static final String DATABASE_TYPE_DB2 = "db2";
    public static final String DATABASE_TYPE_COCKROACHDB = "cockroachdb";
    public static final int DEFAULT_GENERIC_MAX_LENGTH_STRING = 4000;
    public static final int DEFAULT_ORACLE_MAX_LENGTH_STRING = 2000;

    // y9 edit
    public static Properties getDefaultDatabaseTypeMappings() {
        return Y9DbUtil.getDefaultDatabaseTypeMappings();
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected boolean forceCloseMybatisConnectionPool = true;
    protected String databaseType;
    protected String jdbcDriver = "org.h2.Driver";
    protected String jdbcUrl = "jdbc:h2:tcp://localhost/~/flowable";
    protected String jdbcUsername = "sa";
    protected String jdbcPassword = "";
    protected String dataSourceJndiName;

    protected int jdbcMaxActiveConnections = 16;

    protected int jdbcMaxIdleConnections = 8;

    protected int jdbcMaxCheckoutTime;

    // COMMAND EXECUTORS ///////////////////////////////////////////////

    protected int jdbcMaxWaitTime;
    protected boolean jdbcPingEnabled;
    protected String jdbcPingQuery;
    protected int jdbcPingConnectionNotUsedFor;
    protected int jdbcDefaultTransactionIsolationLevel;
    protected DataSource dataSource;

    protected Map<String, SchemaManager> additionalSchemaManagers;
    protected SchemaManager commonSchemaManager;

    protected SchemaManager schemaManager;
    protected Command<Void> schemaManagementCmd;
    protected String databaseSchemaUpdate = DB_SCHEMA_UPDATE_FALSE;

    /**
     * Whether to use a lock when performing the database schema create or update operations.
     */
    protected boolean useLockForDatabaseSchemaUpdate = false;
    protected String xmlEncoding = "UTF-8";

    protected CommandExecutor commandExecutor;
    protected Collection<? extends CommandInterceptor> defaultCommandInterceptors;

    protected CommandConfig defaultCommandConfig;

    protected CommandConfig schemaCommandConfig;

    // MYBATIS SQL SESSION FACTORY /////////////////////////////////////

    protected CommandContextFactory commandContextFactory;
    protected CommandInterceptor commandInvoker;
    protected AgendaOperationRunner agendaOperationRunner = (commandContext, runnable) -> runnable.run();
    protected Collection<AgendaOperationExecutionListener> agendaOperationExecutionListeners;
    protected List<CommandInterceptor> customPreCommandInterceptors;

    protected List<CommandInterceptor> customPostCommandInterceptors;

    protected List<CommandInterceptor> commandInterceptors;

    protected Map<String, AbstractEngineConfiguration> engineConfigurations = new HashMap<>();

    protected Map<String, AbstractServiceConfiguration> serviceConfigurations = new HashMap<>();
    protected ClassLoader classLoader;
    /**
     * Either use Class.forName or ClassLoader.loadClass for class loading. See
     * http://forums.activiti.org/content/reflectutilloadclass-and-custom- classloader
     */
    protected boolean useClassForNameClassLoading = true;
    protected List<EngineLifecycleListener> engineLifecycleListeners;

    // Event Registry //////////////////////////////////////////////////
    protected Map<String, EventRegistryEventConsumer> eventRegistryEventConsumers = new HashMap<>();
    protected boolean isDbHistoryUsed = true;
    protected DbSqlSessionFactory dbSqlSessionFactory;

    protected SqlSessionFactory sqlSessionFactory;
    protected TransactionFactory transactionFactory;

    protected TransactionContextFactory transactionContextFactory;
    /**
     * If set to true, enables bulk insert (grouping sql inserts together). Default true. For some databases (eg
     * DB2+z/OS) needs to be set to false.
     */
    protected boolean isBulkInsertEnabled = true;
    /**
     * Some databases have a limit of how many parameters one sql insert can have (eg SQL Server, 2000 params (!= insert
     * statements) ). Tweak this parameter in case of exceptions indicating too much is being put into one bulk insert,
     * or make it higher if your database can cope with it and there are inserts with a huge amount of data.
     * <p>
     * By default: 100 (55 for mssql server as it has a hard limit of 2000 parameters in a statement)
     */
    protected int maxNrOfStatementsInBulkInsert = 100;
    public int DEFAULT_MAX_NR_OF_STATEMENTS_BULK_INSERT_SQL_SERVER = 55; // currently Execution has most params (35).
                                                                         // 2000 / 35 = 57.
    protected String mybatisMappingFile;

    protected Set<Class<?>> customMybatisMappers;

    protected Set<String> customMybatisXMLMappers;

    protected List<Interceptor> customMybatisInterceptors;

    protected Set<String> dependentEngineMyBatisXmlMappers;

    protected List<MybatisTypeAliasConfigurator> dependentEngineMybatisTypeAliasConfigs;

    protected List<MybatisTypeHandlerConfigurator> dependentEngineMybatisTypeHandlerConfigs;

    // SESSION FACTORIES ///////////////////////////////////////////////
    protected List<SessionFactory> customSessionFactories;

    protected Map<Class<?>, SessionFactory> sessionFactories;

    protected boolean enableEventDispatcher = true;

    protected FlowableEventDispatcher eventDispatcher;

    protected List<FlowableEventListener> eventListeners;

    protected Map<String, List<FlowableEventListener>> typedEventListeners;

    protected List<EventDispatchAction> additionalEventDispatchActions;

    protected LoggingListener loggingListener;

    protected boolean transactionsExternallyManaged;

    /**
     * Flag that can be set to configure or not a relational database is used. This is useful for custom implementations
     * that do not use relational databases at all.
     *
     * If true (default), the {@link AbstractEngineConfiguration#getDatabaseSchemaUpdate()} value will be used to
     * determine what needs to happen wrt the database schema.
     *
     * If false, no validation or schema creation will be done. That means that the database schema must have been
     * created 'manually' before but the engine does not validate whether the schema is correct. The
     * {@link AbstractEngineConfiguration#getDatabaseSchemaUpdate()} value will not be used.
     */
    protected boolean usingRelationalDatabase = true;

    // DATA MANAGERS //////////////////////////////////////////////////////////////////

    /**
     * Flag that can be set to configure whether or not a schema is used. This is useful for custom implementations that
     * do not use relational databases at all. Setting {@link #usingRelationalDatabase} to true will automatically imply
     * using a schema.
     */
    protected boolean usingSchemaMgmt = true;
    /**
     * Allows configuring a database table prefix which is used for all runtime operations of the process engine. For
     * example, if you specify a prefix named 'PRE1.', Flowable will query for executions in a table named
     * 'PRE1.ACT_RU_EXECUTION_'.
     *
     * <p>
     * <strong>NOTE: the prefix is not respected by automatic database schema management. If you use
     * {@link AbstractEngineConfiguration#DB_SCHEMA_UPDATE_CREATE_DROP} or
     * {@link AbstractEngineConfiguration#DB_SCHEMA_UPDATE_TRUE}, Flowable will create the database tables using the
     * default names, regardless of the prefix configured here.</strong>
     */
    protected String databaseTablePrefix = "";
    /**
     * Escape character for doing wildcard searches.
     *
     * This will be added at then end of queries that include for example a LIKE clause. For example: SELECT * FROM
     * table WHERE column LIKE '%\%%' ESCAPE '\';
     */
    protected String databaseWildcardEscapeCharacter;

    // ENTITY MANAGERS ////////////////////////////////////////////////////////////////

    /**
     * database catalog to use
     */
    protected String databaseCatalog = "";
    /**
     * In some situations you want to set the schema to use for table checks / generation if the database metadata
     * doesn't return that correctly, see https://jira.codehaus.org/browse/ACT-1220,
     * https://jira.codehaus.org/browse/ACT-1062
     */
    protected String databaseSchema;

    /**
     * Set to true in case the defined databaseTablePrefix is a schema-name, instead of an actual table name prefix.
     * This is relevant for checking if Flowable-tables exist, the databaseTablePrefix will not be used here - since the
     * schema is taken into account already, adding a prefix for the table-check will result in wrong table-names.
     */
    protected boolean tablePrefixIsSchema;
    /**
     * Set to true if the latest version of a definition should be retrieved, ignoring a possible parent deployment id
     * value
     */
    protected boolean alwaysLookupLatestDefinitionVersion;
    /**
     * Set to true if by default lookups should fallback to the default tenant (an empty string by default or a defined
     * tenant value)
     */
    protected boolean fallbackToDefaultTenant;

    // CONFIGURATORS ////////////////////////////////////////////////////////////

    /**
     * Default tenant provider that is executed when looking up definitions, in case the global or local fallback to
     * default tenant value is true
     */
    protected DefaultTenantProvider defaultTenantProvider = (tenantId, scope, scopeKey) -> NO_TENANT_ID;
    /**
     * Enables the MyBatis plugin that logs the execution time of sql statements.
     */
    protected boolean enableLogSqlExecutionTime;
    protected Properties databaseTypeMappings = getDefaultDatabaseTypeMappings();
    /**
     * Duration between the checks when acquiring a lock.
     */
    protected Duration lockPollRate = Duration.ofSeconds(10);
    /**
     * Duration to wait for the DB Schema lock before giving up.
     */
    protected Duration schemaLockWaitTime = Duration.ofMinutes(5);

    protected PropertyDataManager propertyDataManager;
    protected ByteArrayDataManager byteArrayDataManager;

    protected TableDataManager tableDataManager;
    protected PropertyEntityManager propertyEntityManager;
    protected ByteArrayEntityManager byteArrayEntityManager;
    protected List<EngineDeployer> customPreDeployers;
    protected List<EngineDeployer> customPostDeployers;
    protected List<EngineDeployer> deployers;
    protected boolean enableConfiguratorServiceLoader = true; // Enabled by default. In certain environments this should
                                                              // be set to false (eg osgi)
    protected List<EngineConfigurator> configurators; // The injected configurators

    protected List<EngineConfigurator> allConfigurators; // Including auto-discovered configurators

    protected EngineConfigurator idmEngineConfigurator;

    protected EngineConfigurator eventRegistryConfigurator;
    protected Map<Object, Object> beans;

    protected IdGenerator idGenerator;
    protected boolean usePrefixId;

    // Variables

    protected Clock clock;
    protected ObjectMapper objectMapper;

    /**
     * Define a max length for storing String variable types in the database. Mainly used for the Oracle NVARCHAR2 limit
     * of 2000 characters
     */
    protected int maxLengthStringVariableType = -1;

    public AbstractEngineConfiguration addAdditionalSchemaManager(SchemaManager schemaManager) {
        if (this.additionalSchemaManagers == null) {
            this.additionalSchemaManagers = new HashMap<>();
        }
        this.additionalSchemaManagers.put(schemaManager.getContext(), schemaManager);
        return this;
    }

    // DataSource
    // ///////////////////////////////////////////////////////////////

    public AbstractEngineConfiguration addAgendaOperationExecutionListener(AgendaOperationExecutionListener listener) {
        if (this.agendaOperationExecutionListeners == null) {
            this.agendaOperationExecutionListeners = new ArrayList<>();
        }
        this.agendaOperationExecutionListeners.add(listener);
        return this;
    }

    public AbstractEngineConfiguration addConfigurator(EngineConfigurator configurator) {
        if (configurators == null) {
            configurators = new ArrayList<>();
        }
        configurators.add(configurator);
        return this;
    }

    public AbstractEngineConfiguration addCustomPostCommandInterceptor(CommandInterceptor commandInterceptor) {
        if (this.customPostCommandInterceptors == null) {
            this.customPostCommandInterceptors = new ArrayList<>();
        }
        this.customPostCommandInterceptors.add(commandInterceptor);
        return this;
    }

    public AbstractEngineConfiguration addCustomPreCommandInterceptor(CommandInterceptor commandInterceptor) {
        if (this.customPreCommandInterceptors == null) {
            this.customPreCommandInterceptors = new ArrayList<>();
        }
        this.customPreCommandInterceptors.add(commandInterceptor);
        return this;
    }

    public AbstractEngineConfiguration addCustomSessionFactory(SessionFactory sessionFactory) {
        if (customSessionFactories == null) {
            customSessionFactories = new ArrayList<>();
        }
        customSessionFactories.add(sessionFactory);
        return this;
    }

    public void addEngineConfiguration(String key, String scopeType, AbstractEngineConfiguration engineConfiguration) {
        if (engineConfigurations == null) {
            engineConfigurations = new HashMap<>();
        }
        engineConfigurations.put(key, engineConfiguration);
        engineConfigurations.put(scopeType, engineConfiguration);
    }

    public void addEngineLifecycleListener(EngineLifecycleListener engineLifecycleListener) {
        if (this.engineLifecycleListeners == null) {
            this.engineLifecycleListeners = new ArrayList<>();
        }
        this.engineLifecycleListeners.add(engineLifecycleListener);
    }

    public void addEventRegistryEventConsumer(String key, EventRegistryEventConsumer eventRegistryEventConsumer) {
        if (eventRegistryEventConsumers == null) {
            eventRegistryEventConsumers = new HashMap<>();
        }
        eventRegistryEventConsumers.put(key, eventRegistryEventConsumer);
    }

    public void addServiceConfiguration(String key, AbstractServiceConfiguration serviceConfiguration) {
        if (serviceConfigurations == null) {
            serviceConfigurations = new HashMap<>();
        }
        serviceConfigurations.put(key, serviceConfiguration);
    }

    public void addSessionFactory(SessionFactory sessionFactory) {
        sessionFactories.put(sessionFactory.getSessionType(), sessionFactory);
    }

    protected void applyCustomMybatisCustomizations(Configuration configuration) {
        initCustomMybatisMappers(configuration);

        if (dependentEngineMybatisTypeAliasConfigs != null) {
            for (MybatisTypeAliasConfigurator typeAliasConfig : dependentEngineMybatisTypeAliasConfigs) {
                typeAliasConfig.configure(this, configuration.getTypeAliasRegistry());
            }
        }
        if (dependentEngineMybatisTypeHandlerConfigs != null) {
            for (MybatisTypeHandlerConfigurator typeHandlerConfig : dependentEngineMybatisTypeHandlerConfigs) {
                typeHandlerConfig.configure(this, configuration.getTypeHandlerRegistry());
            }
        }

        parseDependentEngineMybatisXMLMappers(configuration);
        parseCustomMybatisXMLMappers(configuration);
    }

    public void close() {
        if (forceCloseMybatisConnectionPool && dataSource instanceof PooledDataSource) {
            /*
             * When the datasource is created by a Flowable engine (i.e. it's an instance of PooledDataSource),
             * the connection pool needs to be closed when closing the engine.
             * Note that calling forceCloseAll() multiple times (as is the case when running with multiple engine) is ok.
             */
            ((PooledDataSource)dataSource).forceCloseAll();
        }
    }

    public void configuratorsAfterInit() {
        for (EngineConfigurator configurator : allConfigurators) {
            logger.info("Executing configure() of {} (priority:{})", configurator.getClass(),
                configurator.getPriority());
            configurator.configure(this);
        }
    }

    public void configuratorsBeforeInit() {
        for (EngineConfigurator configurator : allConfigurators) {
            logger.info("Executing beforeInit() of {} (priority:{})", configurator.getClass(),
                configurator.getPriority());
            configurator.beforeInit(this);
        }
    }

    public DbSqlSessionFactory createDbSqlSessionFactory() {
        return new DbSqlSessionFactory(usePrefixId);
    }

    protected abstract SchemaManager createEngineSchemaManager();

    public abstract CommandInterceptor createTransactionInterceptor();

    protected void defaultInitDbSqlSessionFactoryEntitySettings(List<Class<? extends Entity>> insertOrder,
        List<Class<? extends Entity>> deleteOrder) {
        if (insertOrder != null) {
            for (Class<? extends Entity> clazz : insertOrder) {
                dbSqlSessionFactory.getInsertionOrder().add(clazz);

                if (isBulkInsertEnabled) {
                    dbSqlSessionFactory.getBulkInserteableEntityClasses().add(clazz);
                }
            }
        }

        if (deleteOrder != null) {
            for (Class<? extends Entity> clazz : deleteOrder) {
                dbSqlSessionFactory.getDeletionOrder().add(clazz);
            }
        }
    }

    public List<CommandInterceptor> getAdditionalDefaultCommandInterceptors() {
        return null;
    }

    public List<EventDispatchAction> getAdditionalEventDispatchActions() {
        return additionalEventDispatchActions;
    }

    public Map<String, SchemaManager> getAdditionalSchemaManagers() {
        return additionalSchemaManagers;
    }

    // id generator
    // /////////////////////////////////////////////////////////////

    public Collection<AgendaOperationExecutionListener> getAgendaOperationExecutionListeners() {
        return agendaOperationExecutionListeners;
    }

    public AgendaOperationRunner getAgendaOperationRunner() {
        return agendaOperationRunner;
    }

    /**
     * @return All {@link EngineConfigurator} instances. Will only contain values after init of the engine. Use the
     *         {@link #getConfigurators()} or {@link #addConfigurator(EngineConfigurator)} methods otherwise.
     */
    public List<EngineConfigurator> getAllConfigurators() {
        return allConfigurators;
    }

    // Data managers ///////////////////////////////////////////////////////////

    public Map<Object, Object> getBeans() {
        return beans;
    }

    // Entity managers //////////////////////////////////////////////////////////

    public ByteArrayDataManager getByteArrayDataManager() {
        return byteArrayDataManager;
    }

    // services
    // /////////////////////////////////////////////////////////////////

    public ByteArrayEntityManager getByteArrayEntityManager() {
        return byteArrayEntityManager;
    }

    // myBatis SqlSessionFactory
    // ////////////////////////////////////////////////

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Clock getClock() {
        return clock;
    }

    public CommandContextFactory getCommandContextFactory() {
        return commandContextFactory;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public List<CommandInterceptor> getCommandInterceptors() {
        return commandInterceptors;
    }

    public CommandInterceptor getCommandInvoker() {
        return commandInvoker;
    }

    public SchemaManager getCommonSchemaManager() {
        return commonSchemaManager;
    }

    public List<EngineConfigurator> getConfigurators() {
        return configurators;
    }

    public List<Interceptor> getCustomMybatisInterceptors() {
        return customMybatisInterceptors;
    }

    public Set<Class<?>> getCustomMybatisMappers() {
        return customMybatisMappers;
    }

    public Set<String> getCustomMybatisXMLMappers() {
        return customMybatisXMLMappers;
    }

    public List<CommandInterceptor> getCustomPostCommandInterceptors() {
        return customPostCommandInterceptors;
    }

    public List<EngineDeployer> getCustomPostDeployers() {
        return customPostDeployers;
    }

    public List<CommandInterceptor> getCustomPreCommandInterceptors() {
        return customPreCommandInterceptors;
    }

    public List<EngineDeployer> getCustomPreDeployers() {
        return customPreDeployers;
    }

    public List<SessionFactory> getCustomSessionFactories() {
        return customSessionFactories;
    }

    public String getDatabaseCatalog() {
        return databaseCatalog;
    }

    public String getDatabaseSchema() {
        return databaseSchema;
    }

    public String getDatabaseSchemaUpdate() {
        return databaseSchemaUpdate;
    }

    public String getDatabaseTablePrefix() {
        return databaseTablePrefix;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getDatabaseWildcardEscapeCharacter() {
        return databaseWildcardEscapeCharacter;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getDataSourceJndiName() {
        return dataSourceJndiName;
    }

    public DbSqlSessionFactory getDbSqlSessionFactory() {
        return dbSqlSessionFactory;
    }

    public CommandConfig getDefaultCommandConfig() {
        return defaultCommandConfig;
    }

    public Collection<? extends CommandInterceptor> getDefaultCommandInterceptors() {
        if (defaultCommandInterceptors == null) {
            List<CommandInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new LogInterceptor());

            if (DATABASE_TYPE_COCKROACHDB.equals(databaseType)) {
                interceptors.add(new CrDbRetryInterceptor());
            }

            CommandInterceptor transactionInterceptor = createTransactionInterceptor();
            if (transactionInterceptor != null) {
                interceptors.add(transactionInterceptor);
            }

            if (commandContextFactory != null) {
                String engineCfgKey = getEngineCfgKey();
                CommandContextInterceptor commandContextInterceptor = new CommandContextInterceptor(
                    commandContextFactory, classLoader, useClassForNameClassLoading, clock, objectMapper);
                engineConfigurations.put(engineCfgKey, this);
                commandContextInterceptor.setEngineCfgKey(engineCfgKey);
                commandContextInterceptor.setEngineConfigurations(engineConfigurations);
                interceptors.add(commandContextInterceptor);
            }

            if (transactionContextFactory != null) {
                interceptors.add(new TransactionContextInterceptor(transactionContextFactory));
            }

            List<CommandInterceptor> additionalCommandInterceptors = getAdditionalDefaultCommandInterceptors();
            if (additionalCommandInterceptors != null) {
                interceptors.addAll(additionalCommandInterceptors);
            }

            defaultCommandInterceptors = interceptors;
        }
        return defaultCommandInterceptors;
    }

    public DefaultTenantProvider getDefaultTenantProvider() {
        return defaultTenantProvider;
    }

    public List<MybatisTypeAliasConfigurator> getDependentEngineMybatisTypeAliasConfigs() {
        return dependentEngineMybatisTypeAliasConfigs;
    }

    // getters and setters
    // //////////////////////////////////////////////////////

    public List<MybatisTypeHandlerConfigurator> getDependentEngineMybatisTypeHandlerConfigs() {
        return dependentEngineMybatisTypeHandlerConfigs;
    }

    public Set<String> getDependentEngineMyBatisXmlMappers() {
        return dependentEngineMyBatisXmlMappers;
    }

    public List<EngineDeployer> getDeployers() {
        return deployers;
    }

    public abstract String getEngineCfgKey();

    public Map<String, AbstractEngineConfiguration> getEngineConfigurations() {
        return engineConfigurations;
    }

    public List<EngineLifecycleListener> getEngineLifecycleListeners() {
        return engineLifecycleListeners;
    }

    public abstract String getEngineName();

    public abstract String getEngineScopeType();

    protected List<EngineConfigurator> getEngineSpecificEngineConfigurators() {
        // meant to be overridden if needed
        return Collections.emptyList();
    }

    public FlowableEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public List<FlowableEventListener> getEventListeners() {
        return eventListeners;
    }

    public EngineConfigurator getEventRegistryConfigurator() {
        return eventRegistryConfigurator;
    }

    public Map<String, EventRegistryEventConsumer> getEventRegistryEventConsumers() {
        return eventRegistryEventConsumers;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public EngineConfigurator getIdmEngineConfigurator() {
        return idmEngineConfigurator;
    }

    public int getJdbcDefaultTransactionIsolationLevel() {
        return jdbcDefaultTransactionIsolationLevel;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public int getJdbcMaxActiveConnections() {
        return jdbcMaxActiveConnections;
    }

    public int getJdbcMaxCheckoutTime() {
        return jdbcMaxCheckoutTime;
    }

    public int getJdbcMaxIdleConnections() {
        return jdbcMaxIdleConnections;
    }

    public int getJdbcMaxWaitTime() {
        return jdbcMaxWaitTime;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public int getJdbcPingConnectionNotUsedFor() {
        return jdbcPingConnectionNotUsedFor;
    }

    public String getJdbcPingQuery() {
        return jdbcPingQuery;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public LockManager getLockManager(String lockName) {
        return new LockManagerImpl(commandExecutor, lockName, getLockPollRate(), getEngineCfgKey());
    }

    public Duration getLockPollRate() {
        return lockPollRate;
    }

    public LoggingListener getLoggingListener() {
        return loggingListener;
    }

    public int getMaxLengthString() {
        if (maxLengthStringVariableType == -1) {
            if ("oracle".equalsIgnoreCase(databaseType)) {
                return DEFAULT_ORACLE_MAX_LENGTH_STRING;
            } else {
                return DEFAULT_GENERIC_MAX_LENGTH_STRING;
            }
        } else {
            return maxLengthStringVariableType;
        }
    }

    public int getMaxLengthStringVariableType() {
        return maxLengthStringVariableType;
    }

    public int getMaxNrOfStatementsInBulkInsert() {
        return maxNrOfStatementsInBulkInsert;
    }

    public String getMybatisMappingFile() {
        return mybatisMappingFile;
    }

    public abstract InputStream getMyBatisXmlConfigurationStream();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public PropertyDataManager getPropertyDataManager() {
        return propertyDataManager;
    }

    public PropertyEntityManager getPropertyEntityManager() {
        return propertyEntityManager;
    }

    protected InputStream getResourceAsStream(String resource) {
        ClassLoader classLoader = getClassLoader();
        if (classLoader != null) {
            return getClassLoader().getResourceAsStream(resource);
        } else {
            return this.getClass().getClassLoader().getResourceAsStream(resource);
        }
    }

    public CommandConfig getSchemaCommandConfig() {
        return schemaCommandConfig;
    }

    public Duration getSchemaLockWaitTime() {
        return schemaLockWaitTime;
    }

    public Command<Void> getSchemaManagementCmd() {
        return schemaManagementCmd;
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    public Map<String, AbstractServiceConfiguration> getServiceConfigurations() {
        return serviceConfigurations;
    }

    public Map<Class<?>, SessionFactory> getSessionFactories() {
        return sessionFactories;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public TableDataManager getTableDataManager() {
        return tableDataManager;
    }

    public TransactionContextFactory getTransactionContextFactory() {
        return transactionContextFactory;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public Map<String, List<FlowableEventListener>> getTypedEventListeners() {
        return typedEventListeners;
    }

    public String getXmlEncoding() {
        return xmlEncoding;
    }

    protected void initAdditionalEventDispatchActions() {
        if (this.additionalEventDispatchActions == null) {
            this.additionalEventDispatchActions = new ArrayList<>();
        }
    }

    public void initBeans() {
        if (beans == null) {
            beans = new HashMap<>();
        }
    }

    public void initClock() {
        if (clock == null) {
            clock = new DefaultClockImpl();
        }
    }

    public void initCommandContextFactory() {
        if (commandContextFactory == null) {
            commandContextFactory = new CommandContextFactory();
        }
    }

    public void initCommandExecutor() {
        if (commandExecutor == null) {
            CommandInterceptor first = initInterceptorChain(commandInterceptors);
            commandExecutor = new CommandExecutorImpl(getDefaultCommandConfig(), first);
        }
    }

    public void initCommandExecutors() {
        initDefaultCommandConfig();
        initSchemaCommandConfig();
        initCommandInvoker();
        initCommandInterceptors();
        initCommandExecutor();
    }

    public void initCommandInterceptors() {
        if (commandInterceptors == null) {
            commandInterceptors = new ArrayList<>();
            if (customPreCommandInterceptors != null) {
                commandInterceptors.addAll(customPreCommandInterceptors);
            }
            commandInterceptors.addAll(getDefaultCommandInterceptors());
            if (customPostCommandInterceptors != null) {
                commandInterceptors.addAll(customPostCommandInterceptors);
            }
            commandInterceptors.add(commandInvoker);
        }
    }

    public void initCommandInvoker() {
        if (commandInvoker == null) {
            commandInvoker = new DefaultCommandInvoker();
        }
    }

    public void initConfigurators() {

        allConfigurators = new ArrayList<>();
        allConfigurators.addAll(getEngineSpecificEngineConfigurators());

        // Configurators that are explicitly added to the config
        if (configurators != null) {
            allConfigurators.addAll(configurators);
        }

        // Auto discovery through ServiceLoader
        if (enableConfiguratorServiceLoader) {
            ClassLoader classLoader = getClassLoader();
            if (classLoader == null) {
                classLoader = ReflectUtil.getClassLoader();
            }

            ServiceLoader<EngineConfigurator> configuratorServiceLoader =
                ServiceLoader.load(EngineConfigurator.class, classLoader);
            int nrOfServiceLoadedConfigurators = 0;
            for (EngineConfigurator configurator : configuratorServiceLoader) {
                allConfigurators.add(configurator);
                nrOfServiceLoadedConfigurators++;
            }

            if (nrOfServiceLoadedConfigurators > 0) {
                logger.info("Found {} auto-discoverable Process Engine Configurator{}", nrOfServiceLoadedConfigurators,
                    nrOfServiceLoadedConfigurators > 1 ? "s" : "");
            }

            if (!allConfigurators.isEmpty()) {

                // Order them according to the priorities (useful for dependent
                // configurator)
                allConfigurators.sort(new Comparator<>() {

                    @Override
                    public int compare(EngineConfigurator configurator1, EngineConfigurator configurator2) {
                        int priority1 = configurator1.getPriority();
                        int priority2 = configurator2.getPriority();

                        if (priority1 < priority2) {
                            return -1;
                        } else if (priority1 > priority2) {
                            return 1;
                        }
                        return 0;
                    }
                });

                // Execute the configurators
                logger.info("Found {} Engine Configurators in total:", allConfigurators.size());
                for (EngineConfigurator configurator : allConfigurators) {
                    logger.info("{} (priority:{})", configurator.getClass(), configurator.getPriority());
                }

            }

        }
    }

    public void initCustomMybatisInterceptors(Configuration configuration) {
        if (customMybatisInterceptors != null) {
            for (Interceptor interceptor : customMybatisInterceptors) {
                configuration.addInterceptor(interceptor);
            }
        }
    }

    public void initCustomMybatisMappers(Configuration configuration) {
        if (getCustomMybatisMappers() != null) {
            for (Class<?> clazz : getCustomMybatisMappers()) {
                if (!configuration.hasMapper(clazz)) {
                    configuration.addMapper(clazz);
                }
            }
        }
    }

    public void initDatabaseType() {
        // y9 edit
        databaseType = Y9DbUtil.determineDatabaseType(dataSource, logger, databaseTypeMappings);

        // Special care for MSSQL, as it has a hard limit of 2000 params per statement (incl bulk statement).
        // Especially with executions, with 100 as default, this limit is passed.
        if (DATABASE_TYPE_MSSQL.equals(databaseType)) {
            maxNrOfStatementsInBulkInsert = DEFAULT_MAX_NR_OF_STATEMENTS_BULK_INSERT_SQL_SERVER;
        }
    }

    public void initDataManagers() {
        if (propertyDataManager == null) {
            propertyDataManager = new MybatisPropertyDataManager(idGenerator);
        }

        if (byteArrayDataManager == null) {
            byteArrayDataManager = new MybatisByteArrayDataManager(idGenerator);
        }
    }

    protected void initDataSource() {
        if (dataSource == null) {
            if (dataSourceJndiName != null) {
                try {
                    dataSource = (DataSource)new InitialContext().lookup(dataSourceJndiName);
                } catch (Exception e) {
                    throw new FlowableException(
                        "couldn't lookup datasource from " + dataSourceJndiName + ": " + e.getMessage(), e);
                }

            } else if (jdbcUrl != null) {
                if ((jdbcDriver == null) || (jdbcUsername == null)) {
                    throw new FlowableException(
                        "DataSource or JDBC properties have to be specified in a process engine configuration");
                }

                logger.debug("initializing datasource to db: {}", jdbcUrl);

                if (logger.isInfoEnabled()) {
                    logger.info("Configuring Datasource with following properties (omitted password for security)");
                    logger.info("datasource driver : {}", jdbcDriver);
                    logger.info("datasource url : {}", jdbcUrl);
                    logger.info("datasource user name : {}", jdbcUsername);
                }

                PooledDataSource pooledDataSource = new PooledDataSource(this.getClass().getClassLoader(), jdbcDriver,
                    jdbcUrl, jdbcUsername, jdbcPassword);

                if (jdbcMaxActiveConnections > 0) {
                    pooledDataSource.setPoolMaximumActiveConnections(jdbcMaxActiveConnections);
                }
                if (jdbcMaxIdleConnections > 0) {
                    pooledDataSource.setPoolMaximumIdleConnections(jdbcMaxIdleConnections);
                }
                if (jdbcMaxCheckoutTime > 0) {
                    pooledDataSource.setPoolMaximumCheckoutTime(jdbcMaxCheckoutTime);
                }
                if (jdbcMaxWaitTime > 0) {
                    pooledDataSource.setPoolTimeToWait(jdbcMaxWaitTime);
                }
                if (jdbcPingEnabled) {
                    pooledDataSource.setPoolPingEnabled(true);
                    if (jdbcPingQuery != null) {
                        pooledDataSource.setPoolPingQuery(jdbcPingQuery);
                    }
                    pooledDataSource.setPoolPingConnectionsNotUsedFor(jdbcPingConnectionNotUsedFor);
                }
                if (jdbcDefaultTransactionIsolationLevel > 0) {
                    pooledDataSource.setDefaultTransactionIsolationLevel(jdbcDefaultTransactionIsolationLevel);
                }
                dataSource = pooledDataSource;
            }
        }

        if (databaseType == null) {
            initDatabaseType();
        }
    }

    public void initDbSqlSessionFactory() {
        if (dbSqlSessionFactory == null) {
            dbSqlSessionFactory = createDbSqlSessionFactory();
        }
        dbSqlSessionFactory.setDatabaseType(databaseType);
        dbSqlSessionFactory.setSqlSessionFactory(sqlSessionFactory);
        dbSqlSessionFactory.setDbHistoryUsed(isDbHistoryUsed);
        dbSqlSessionFactory.setDatabaseTablePrefix(databaseTablePrefix);
        dbSqlSessionFactory.setTablePrefixIsSchema(tablePrefixIsSchema);
        dbSqlSessionFactory.setDatabaseCatalog(databaseCatalog);
        dbSqlSessionFactory.setDatabaseSchema(databaseSchema);
        dbSqlSessionFactory.setMaxNrOfStatementsInBulkInsert(maxNrOfStatementsInBulkInsert);

        initDbSqlSessionFactoryEntitySettings();

        addSessionFactory(dbSqlSessionFactory);
    }

    protected abstract void initDbSqlSessionFactoryEntitySettings();

    public void initDefaultCommandConfig() {
        if (defaultCommandConfig == null) {
            defaultCommandConfig = new CommandConfig();
        }
    }

    protected void initEngineConfigurations() {
        addEngineConfiguration(getEngineCfgKey(), getEngineScopeType(), this);
    }

    public void initEntityManagers() {
        if (propertyEntityManager == null) {
            propertyEntityManager = new PropertyEntityManagerImpl(this, propertyDataManager);
        }

        if (byteArrayEntityManager == null) {
            byteArrayEntityManager =
                new ByteArrayEntityManagerImpl(byteArrayDataManager, getEngineCfgKey(), this::getEventDispatcher);
        }

        if (tableDataManager == null) {
            tableDataManager = new TableDataManagerImpl(this);
        }
    }

    public void initEventDispatcher() {
        if (this.eventDispatcher == null) {
            this.eventDispatcher = new FlowableEventDispatcherImpl();
        }

        initAdditionalEventDispatchActions();

        this.eventDispatcher.setEnabled(enableEventDispatcher);

        initEventListeners();
        initTypedEventListeners();
    }

    protected void initEventListeners() {
        if (eventListeners != null) {
            for (FlowableEventListener listenerToAdd : eventListeners) {
                this.eventDispatcher.addEventListener(listenerToAdd);
            }
        }
    }

    public void initIdGenerator() {
        if (idGenerator == null) {
            idGenerator = new StrongUuidGenerator();
        }
    }

    public CommandInterceptor initInterceptorChain(List<CommandInterceptor> chain) {
        if (chain == null || chain.isEmpty()) {
            throw new FlowableException("invalid command interceptor chain configuration: " + chain);
        }
        for (int i = 0; i < chain.size() - 1; i++) {
            chain.get(i).setNext(chain.get(i + 1));
        }
        return chain.get(0);
    }

    public Configuration initMybatisConfiguration(Environment environment, Reader reader, Properties properties) {
        XMLConfigBuilder parser = new XMLConfigBuilder(reader, "", properties);
        Configuration configuration = parser.getConfiguration();

        if (databaseType != null) {
            configuration.setDatabaseId(databaseType);
        }

        configuration.setEnvironment(environment);

        initMybatisTypeHandlers(configuration);
        initCustomMybatisInterceptors(configuration);
        if (isEnableLogSqlExecutionTime()) {
            initMyBatisLogSqlExecutionTimePlugin(configuration);
        }

        configuration = parseMybatisConfiguration(parser);
        return configuration;
    }

    public void initMyBatisLogSqlExecutionTimePlugin(Configuration configuration) {
        configuration.addInterceptor(new LogSqlExecutionTimePlugin());
    }

    public void initMybatisTypeHandlers(Configuration configuration) {
        // When mapping into Map<String, Object> there is currently a problem with MyBatis.
        // It will return objects which are driver specific.
        // Therefore we are registering the mappings between Object.class and the specific jdbc type here.
        // see https://github.com/mybatis/mybatis-3/issues/2216 for more info
        TypeHandlerRegistry handlerRegistry = configuration.getTypeHandlerRegistry();

        handlerRegistry.register(Object.class, JdbcType.BOOLEAN, new BooleanTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.BIT, new BooleanTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.TINYINT, new ByteTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.SMALLINT, new ShortTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.INTEGER, new IntegerTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.FLOAT, new FloatTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.DOUBLE, new DoubleTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.CHAR, new StringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.CLOB, new ClobTypeHandler());

        // For SQL server, the performance difference between using varchar or nvarchar is big.
        // Using the correct jdbcType is thus very important for SQL server, but in many mappings the type was not set,
        // which led to defaulting to the regular 'varchar' jdbcType.
        // Up to the point where the following if was added, none of the MyBatis mappings would use 'nvarchar' as
        // jdbcType.
        // Together with this check, all mappings were reviewed and the correct type was added.
        if (databaseType.equals(DATABASE_TYPE_MSSQL)) {
            handlerRegistry.register(Object.class, JdbcType.VARCHAR, new StringTypeHandler());
            handlerRegistry.register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
            handlerRegistry.register(Object.class, JdbcType.NVARCHAR, new NStringTypeHandler()); // Notice the 'N'
                                                                                                 // prefix here
            handlerRegistry.register(String.class, JdbcType.NVARCHAR, new NStringTypeHandler()); // Notice the 'N'
                                                                                                 // prefix here
        } else {
            // However, for other databases, we want to keep the old behavior of always using 'varchar',
            // thus the same handler is used for both types.
            handlerRegistry.register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
            handlerRegistry.register(Object.class, JdbcType.VARCHAR, new StringTypeHandler());
            if (databaseType.equals(DATABASE_TYPE_DB2)) {
                handlerRegistry.register(String.class, JdbcType.NVARCHAR, new FlowableStringTypeHandler(true)); // Notice:
                                                                                                                // no
                                                                                                                // 'N'
                                                                                                                // prefix
                                                                                                                // here
                handlerRegistry.register(Object.class, JdbcType.NVARCHAR, new FlowableStringTypeHandler(true)); // Notice:
                                                                                                                // no
                                                                                                                // 'N'
                                                                                                                // prefix
                                                                                                                // here
            } else {
                handlerRegistry.register(String.class, JdbcType.NVARCHAR, new FlowableStringTypeHandler(false)); // Notice:
                                                                                                                 // no
                                                                                                                 // 'N'
                                                                                                                 // prefix
                                                                                                                 // here
                handlerRegistry.register(Object.class, JdbcType.NVARCHAR, new FlowableStringTypeHandler(false)); // Notice:
                                                                                                                 // no
                                                                                                                 // 'N'
                                                                                                                 // prefix
                                                                                                                 // here
            }
        }

        handlerRegistry.register(Object.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NCHAR, new NStringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NCLOB, new NClobTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.ARRAY, new ArrayTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.BIGINT, new LongTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.REAL, new BigDecimalTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.DECIMAL, new BigDecimalTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NUMERIC, new BigDecimalTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.BLOB, new BlobInputStreamTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.LONGVARBINARY, new BlobTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.DATE, new DateOnlyTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.TIME, new TimeOnlyTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.TIMESTAMP, new DateTypeHandler());

        handlerRegistry.register(Object.class, JdbcType.SQLXML, new SqlxmlTypeHandler());
    }

    public void initObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
    }

    public void initSchemaCommandConfig() {
        if (schemaCommandConfig == null) {
            schemaCommandConfig = new CommandConfig();
        }
    }

    public void initSchemaManagementCommand() {
        if (schemaManagementCmd == null) {
            if (usingRelationalDatabase && databaseSchemaUpdate != null) {
                this.schemaManagementCmd = new SchemaOperationsEngineBuild(getEngineScopeType());
            }
        }
    }
    // session factories ////////////////////////////////////////////////////////

    public void initSchemaManager() {
        if (this.commonSchemaManager == null) {
            this.commonSchemaManager = new CommonDbSchemaManager();
        }

        if (this.schemaManager == null) {
            this.schemaManager = createEngineSchemaManager();
        }

    }

    protected void initSchemaManagerDatabaseConfigurationSessionFactory() {
        if (!sessionFactories.containsKey(SchemaManagerDatabaseConfiguration.class)) {
            addSessionFactory(new SchemaManagerDatabaseConfigurationSessionFactory());
        }
    }

    protected void initService(Object service) {
        if (service instanceof CommonEngineServiceImpl) {
            ((CommonEngineServiceImpl)service).setCommandExecutor(commandExecutor);
        }
    }

    public void initSessionFactories() {
        if (sessionFactories == null) {
            sessionFactories = new HashMap<>();

            if (usingRelationalDatabase) {
                initDbSqlSessionFactory();
                initSchemaManagerDatabaseConfigurationSessionFactory();
            }

            addSessionFactory(new GenericManagerFactory(EntityCache.class, EntityCacheImpl::new));

            if (isLoggingSessionEnabled()) {
                if (!sessionFactories.containsKey(LoggingSession.class)) {
                    LoggingSessionFactory loggingSessionFactory = new LoggingSessionFactory();
                    loggingSessionFactory.setLoggingListener(loggingListener);
                    loggingSessionFactory.setObjectMapper(objectMapper);
                    sessionFactories.put(LoggingSession.class, loggingSessionFactory);
                }
            }

            commandContextFactory.setSessionFactories(sessionFactories);

        } else {
            if (usingRelationalDatabase) {
                initDbSqlSessionFactoryEntitySettings();
            }
        }

        if (customSessionFactories != null) {
            for (SessionFactory sessionFactory : customSessionFactories) {
                addSessionFactory(sessionFactory);
            }
        }
    }

    public void initSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            InputStream inputStream = null;
            try {
                inputStream = getMyBatisXmlConfigurationStream();

                Environment environment = new Environment("default", transactionFactory, dataSource);
                Reader reader = new InputStreamReader(inputStream);
                Properties properties = new Properties();
                properties.put("prefix", databaseTablePrefix);

                String wildcardEscapeClause = "";
                if ((databaseWildcardEscapeCharacter != null) && (databaseWildcardEscapeCharacter.length() != 0)) {
                    wildcardEscapeClause = " escape '" + databaseWildcardEscapeCharacter + "'";
                }
                properties.put("wildcardEscapeClause", wildcardEscapeClause);

                // set default properties
                properties.put("limitBefore", "");
                properties.put("limitAfter", "");
                properties.put("limitBetween", "");
                properties.put("limitBeforeNativeQuery", "");
                properties.put("limitAfterNativeQuery", "");
                properties.put("blobType", "BLOB");
                properties.put("boolValue", "TRUE");

                if (databaseType != null) {
                    properties.load(getResourceAsStream(pathToEngineDbProperties()));
                }

                Configuration configuration = initMybatisConfiguration(environment, reader, properties);
                sqlSessionFactory = new DefaultSqlSessionFactory(configuration);

            } catch (Exception e) {
                throw new FlowableException("Error while building ibatis SqlSessionFactory: " + e.getMessage(), e);
            } finally {
                IoUtil.closeSilently(inputStream);
            }
        } else {
            // This is needed when the SQL Session Factory is created by another engine.
            // When custom XML Mappers are registered with this engine they need to be loaded in the configuration as
            // well
            applyCustomMybatisCustomizations(sqlSessionFactory.getConfiguration());
        }
    }

    public void initTransactionContextFactory() {
        if (transactionContextFactory == null) {
            transactionContextFactory = new StandaloneMybatisTransactionContextFactory();
        }
    }

    public void initTransactionFactory() {
        if (transactionFactory == null) {
            if (transactionsExternallyManaged) {
                transactionFactory = new ManagedTransactionFactory();
                Properties properties = new Properties();
                properties.put("closeConnection", "false");
                this.transactionFactory.setProperties(properties);
            } else {
                transactionFactory = new JdbcTransactionFactory();
            }
        }
    }

    protected void initTypedEventListeners() {
        if (typedEventListeners != null) {
            for (Map.Entry<String, List<FlowableEventListener>> listenersToAdd : typedEventListeners.entrySet()) {
                // Extract types from the given string
                FlowableEngineEventType[] types = FlowableEngineEventType.getTypesFromString(listenersToAdd.getKey());

                for (FlowableEventListener listenerToAdd : listenersToAdd.getValue()) {
                    this.eventDispatcher.addEventListener(listenerToAdd, types);
                }
            }
        }
    }

    public boolean isAlwaysLookupLatestDefinitionVersion() {
        return alwaysLookupLatestDefinitionVersion;
    }

    public boolean isBulkInsertEnabled() {
        return isBulkInsertEnabled;
    }

    public boolean isDbHistoryUsed() {
        return isDbHistoryUsed;
    }

    public boolean isEnableConfiguratorServiceLoader() {
        return enableConfiguratorServiceLoader;
    }

    public boolean isEnableEventDispatcher() {
        return enableEventDispatcher;
    }

    public boolean isEnableLogSqlExecutionTime() {
        return enableLogSqlExecutionTime;
    }

    public boolean isFallbackToDefaultTenant() {
        return fallbackToDefaultTenant;
    }

    public boolean isForceCloseMybatisConnectionPool() {
        return forceCloseMybatisConnectionPool;
    }

    public boolean isJdbcPingEnabled() {
        return jdbcPingEnabled;
    }

    public boolean isLoggingSessionEnabled() {
        return loggingListener != null;
    }

    public boolean isTablePrefixIsSchema() {
        return tablePrefixIsSchema;
    }

    public boolean isTransactionsExternallyManaged() {
        return transactionsExternallyManaged;
    }

    public boolean isUseClassForNameClassLoading() {
        return useClassForNameClassLoading;
    }

    public boolean isUseLockForDatabaseSchemaUpdate() {
        return useLockForDatabaseSchemaUpdate;
    }

    public boolean isUsePrefixId() {
        return usePrefixId;
    }

    public boolean isUsingRelationalDatabase() {
        return usingRelationalDatabase;
    }

    public boolean isUsingSchemaMgmt() {
        return usingSchemaMgmt;
    }

    public void parseCustomMybatisXMLMappers(Configuration configuration) {
        if (getCustomMybatisXMLMappers() != null) {
            for (String resource : getCustomMybatisXMLMappers()) {
                parseMybatisXmlMapping(configuration, resource);
            }
        }
    }

    public void parseDependentEngineMybatisXMLMappers(Configuration configuration) {
        if (getDependentEngineMyBatisXmlMappers() != null) {
            for (String resource : getDependentEngineMyBatisXmlMappers()) {
                parseMybatisXmlMapping(configuration, resource);
            }
        }
    }

    public Configuration parseMybatisConfiguration(XMLConfigBuilder parser) {
        Configuration configuration = parser.parse();

        applyCustomMybatisCustomizations(configuration);
        return configuration;
    }

    protected void parseMybatisXmlMapping(Configuration configuration, String resource) {
        // see XMLConfigBuilder.mapperElement()
        XMLMapperBuilder mapperParser = new XMLMapperBuilder(getResourceAsStream(resource), configuration, resource,
            configuration.getSqlFragments());
        mapperParser.parse();
    }

    public String pathToEngineDbProperties() {
        return "org/flowable/common/db/properties/" + databaseType + ".properties";
    }

    public AbstractEngineConfiguration
        setAdditionalEventDispatchActions(List<EventDispatchAction> additionalEventDispatchActions) {
        this.additionalEventDispatchActions = additionalEventDispatchActions;
        return this;
    }

    public AbstractEngineConfiguration setAgendaOperationExecutionListeners(
        Collection<AgendaOperationExecutionListener> agendaOperationExecutionListeners) {
        this.agendaOperationExecutionListeners = agendaOperationExecutionListeners;
        return this;
    }

    public AbstractEngineConfiguration setAgendaOperationRunner(AgendaOperationRunner agendaOperationRunner) {
        this.agendaOperationRunner = agendaOperationRunner;
        return this;
    }

    public AbstractEngineConfiguration
        setAlwaysLookupLatestDefinitionVersion(boolean alwaysLookupLatestDefinitionVersion) {
        this.alwaysLookupLatestDefinitionVersion = alwaysLookupLatestDefinitionVersion;
        return this;
    }

    public AbstractEngineConfiguration setBeans(Map<Object, Object> beans) {
        this.beans = beans;
        return this;
    }

    public AbstractEngineConfiguration setBulkInsertEnabled(boolean isBulkInsertEnabled) {
        this.isBulkInsertEnabled = isBulkInsertEnabled;
        return this;
    }

    public AbstractEngineConfiguration setByteArrayDataManager(ByteArrayDataManager byteArrayDataManager) {
        this.byteArrayDataManager = byteArrayDataManager;
        return this;
    }

    public AbstractEngineConfiguration setByteArrayEntityManager(ByteArrayEntityManager byteArrayEntityManager) {
        this.byteArrayEntityManager = byteArrayEntityManager;
        return this;
    }

    public AbstractEngineConfiguration setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public AbstractEngineConfiguration setClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    public AbstractEngineConfiguration setCommandContextFactory(CommandContextFactory commandContextFactory) {
        this.commandContextFactory = commandContextFactory;
        return this;
    }

    public AbstractEngineConfiguration setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public AbstractEngineConfiguration setCommandInterceptors(List<CommandInterceptor> commandInterceptors) {
        this.commandInterceptors = commandInterceptors;
        return this;
    }

    public AbstractEngineConfiguration setCommandInvoker(CommandInterceptor commandInvoker) {
        this.commandInvoker = commandInvoker;
        return this;
    }

    public AbstractEngineConfiguration setCommonSchemaManager(SchemaManager commonSchemaManager) {
        this.commonSchemaManager = commonSchemaManager;
        return this;
    }

    public AbstractEngineConfiguration setConfigurators(List<EngineConfigurator> configurators) {
        this.configurators = configurators;
        return this;
    }

    public AbstractEngineConfiguration setCustomMybatisInterceptors(List<Interceptor> customMybatisInterceptors) {
        this.customMybatisInterceptors = customMybatisInterceptors;
        return this;
    }

    public AbstractEngineConfiguration setCustomMybatisMappers(Set<Class<?>> customMybatisMappers) {
        this.customMybatisMappers = customMybatisMappers;
        return this;
    }

    public AbstractEngineConfiguration setCustomMybatisXMLMappers(Set<String> customMybatisXMLMappers) {
        this.customMybatisXMLMappers = customMybatisXMLMappers;
        return this;
    }

    public AbstractEngineConfiguration
        setCustomPostCommandInterceptors(List<CommandInterceptor> customPostCommandInterceptors) {
        this.customPostCommandInterceptors = customPostCommandInterceptors;
        return this;
    }

    public AbstractEngineConfiguration setCustomPostDeployers(List<EngineDeployer> customPostDeployers) {
        this.customPostDeployers = customPostDeployers;
        return this;
    }

    public AbstractEngineConfiguration
        setCustomPreCommandInterceptors(List<CommandInterceptor> customPreCommandInterceptors) {
        this.customPreCommandInterceptors = customPreCommandInterceptors;
        return this;
    }

    public AbstractEngineConfiguration setCustomPreDeployers(List<EngineDeployer> customPreDeployers) {
        this.customPreDeployers = customPreDeployers;
        return this;
    }

    public AbstractEngineConfiguration setCustomSessionFactories(List<SessionFactory> customSessionFactories) {
        this.customSessionFactories = customSessionFactories;
        return this;
    }

    public AbstractEngineConfiguration setDatabaseCatalog(String databaseCatalog) {
        this.databaseCatalog = databaseCatalog;
        return this;
    }

    public AbstractEngineConfiguration setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
        return this;
    }

    public AbstractEngineConfiguration setDatabaseSchemaUpdate(String databaseSchemaUpdate) {
        this.databaseSchemaUpdate = databaseSchemaUpdate;
        return this;
    }

    public AbstractEngineConfiguration setDatabaseTablePrefix(String databaseTablePrefix) {
        this.databaseTablePrefix = databaseTablePrefix;
        return this;
    }

    public AbstractEngineConfiguration setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
        return this;
    }

    public AbstractEngineConfiguration setDatabaseWildcardEscapeCharacter(String databaseWildcardEscapeCharacter) {
        this.databaseWildcardEscapeCharacter = databaseWildcardEscapeCharacter;
        return this;
    }

    public AbstractEngineConfiguration setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public AbstractEngineConfiguration setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
        return this;
    }

    public AbstractEngineConfiguration setDbHistoryUsed(boolean isDbHistoryUsed) {
        this.isDbHistoryUsed = isDbHistoryUsed;
        return this;
    }

    public AbstractEngineConfiguration setDbSqlSessionFactory(DbSqlSessionFactory dbSqlSessionFactory) {
        this.dbSqlSessionFactory = dbSqlSessionFactory;
        return this;
    }

    public AbstractEngineConfiguration setDefaultCommandConfig(CommandConfig defaultCommandConfig) {
        this.defaultCommandConfig = defaultCommandConfig;
        return this;
    }

    public AbstractEngineConfiguration
        setDefaultCommandInterceptors(Collection<? extends CommandInterceptor> defaultCommandInterceptors) {
        this.defaultCommandInterceptors = defaultCommandInterceptors;
        return this;
    }

    public AbstractEngineConfiguration setDefaultTenantProvider(DefaultTenantProvider defaultTenantProvider) {
        this.defaultTenantProvider = defaultTenantProvider;
        return this;
    }

    public AbstractEngineConfiguration setDefaultTenantValue(String defaultTenantValue) {
        this.defaultTenantProvider = (tenantId, scope, scopeKey) -> defaultTenantValue;
        return this;
    }

    public AbstractEngineConfiguration setDependentEngineMybatisTypeAliasConfigs(
        List<MybatisTypeAliasConfigurator> dependentEngineMybatisTypeAliasConfigs) {
        this.dependentEngineMybatisTypeAliasConfigs = dependentEngineMybatisTypeAliasConfigs;
        return this;
    }

    public AbstractEngineConfiguration setDependentEngineMybatisTypeHandlerConfigs(
        List<MybatisTypeHandlerConfigurator> dependentEngineMybatisTypeHandlerConfigs) {
        this.dependentEngineMybatisTypeHandlerConfigs = dependentEngineMybatisTypeHandlerConfigs;
        return this;
    }

    public AbstractEngineConfiguration
        setDependentEngineMyBatisXmlMappers(Set<String> dependentEngineMyBatisXmlMappers) {
        this.dependentEngineMyBatisXmlMappers = dependentEngineMyBatisXmlMappers;
        return this;
    }

    public AbstractEngineConfiguration setDeployers(List<EngineDeployer> deployers) {
        this.deployers = deployers;
        return this;
    }

    public AbstractEngineConfiguration setEnableConfiguratorServiceLoader(boolean enableConfiguratorServiceLoader) {
        this.enableConfiguratorServiceLoader = enableConfiguratorServiceLoader;
        return this;
    }

    public AbstractEngineConfiguration setEnableEventDispatcher(boolean enableEventDispatcher) {
        this.enableEventDispatcher = enableEventDispatcher;
        return this;
    }

    public void setEnableLogSqlExecutionTime(boolean enableLogSqlExecutionTime) {
        this.enableLogSqlExecutionTime = enableLogSqlExecutionTime;
    }

    public AbstractEngineConfiguration
        setEngineConfigurations(Map<String, AbstractEngineConfiguration> engineConfigurations) {
        this.engineConfigurations = engineConfigurations;
        return this;
    }

    public AbstractEngineConfiguration
        setEngineLifecycleListeners(List<EngineLifecycleListener> engineLifecycleListeners) {
        this.engineLifecycleListeners = engineLifecycleListeners;
        return this;
    }

    public AbstractEngineConfiguration setEventDispatcher(FlowableEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        return this;
    }

    public AbstractEngineConfiguration setEventListeners(List<FlowableEventListener> eventListeners) {
        this.eventListeners = eventListeners;
        return this;
    }

    public AbstractEngineConfiguration setEventRegistryConfigurator(EngineConfigurator eventRegistryConfigurator) {
        this.eventRegistryConfigurator = eventRegistryConfigurator;
        return this;
    }

    public AbstractEngineConfiguration
        setEventRegistryEventConsumers(Map<String, EventRegistryEventConsumer> eventRegistryEventConsumers) {
        this.eventRegistryEventConsumers = eventRegistryEventConsumers;
        return this;
    }

    public AbstractEngineConfiguration setFallbackToDefaultTenant(boolean fallbackToDefaultTenant) {
        this.fallbackToDefaultTenant = fallbackToDefaultTenant;
        return this;
    }

    public AbstractEngineConfiguration setForceCloseMybatisConnectionPool(boolean forceCloseMybatisConnectionPool) {
        this.forceCloseMybatisConnectionPool = forceCloseMybatisConnectionPool;
        return this;
    }

    public AbstractEngineConfiguration setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    public AbstractEngineConfiguration setIdmEngineConfigurator(EngineConfigurator idmEngineConfigurator) {
        this.idmEngineConfigurator = idmEngineConfigurator;
        return this;
    }

    public AbstractEngineConfiguration
        setJdbcDefaultTransactionIsolationLevel(int jdbcDefaultTransactionIsolationLevel) {
        this.jdbcDefaultTransactionIsolationLevel = jdbcDefaultTransactionIsolationLevel;
        return this;
    }

    public AbstractEngineConfiguration setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
        return this;
    }

    public AbstractEngineConfiguration setJdbcMaxActiveConnections(int jdbcMaxActiveConnections) {
        this.jdbcMaxActiveConnections = jdbcMaxActiveConnections;
        return this;
    }

    public AbstractEngineConfiguration setJdbcMaxCheckoutTime(int jdbcMaxCheckoutTime) {
        this.jdbcMaxCheckoutTime = jdbcMaxCheckoutTime;
        return this;
    }

    public AbstractEngineConfiguration setJdbcMaxIdleConnections(int jdbcMaxIdleConnections) {
        this.jdbcMaxIdleConnections = jdbcMaxIdleConnections;
        return this;
    }

    public AbstractEngineConfiguration setJdbcMaxWaitTime(int jdbcMaxWaitTime) {
        this.jdbcMaxWaitTime = jdbcMaxWaitTime;
        return this;
    }

    public AbstractEngineConfiguration setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
        return this;
    }

    public AbstractEngineConfiguration setJdbcPingConnectionNotUsedFor(int jdbcPingConnectionNotUsedFor) {
        this.jdbcPingConnectionNotUsedFor = jdbcPingConnectionNotUsedFor;
        return this;
    }

    public AbstractEngineConfiguration setJdbcPingEnabled(boolean jdbcPingEnabled) {
        this.jdbcPingEnabled = jdbcPingEnabled;
        return this;
    }

    public AbstractEngineConfiguration setJdbcPingQuery(String jdbcPingQuery) {
        this.jdbcPingQuery = jdbcPingQuery;
        return this;
    }

    public AbstractEngineConfiguration setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public AbstractEngineConfiguration setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
        return this;
    }

    public AbstractEngineConfiguration setLockPollRate(Duration lockPollRate) {
        this.lockPollRate = lockPollRate;
        return this;
    }

    public void setLoggingListener(LoggingListener loggingListener) {
        this.loggingListener = loggingListener;
    }

    public AbstractEngineConfiguration setMaxLengthStringVariableType(int maxLengthStringVariableType) {
        this.maxLengthStringVariableType = maxLengthStringVariableType;
        return this;
    }

    public AbstractEngineConfiguration setMaxNrOfStatementsInBulkInsert(int maxNrOfStatementsInBulkInsert) {
        this.maxNrOfStatementsInBulkInsert = maxNrOfStatementsInBulkInsert;
        return this;
    }

    public void setMybatisMappingFile(String file) {
        this.mybatisMappingFile = file;
    }

    public AbstractEngineConfiguration setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public AbstractEngineConfiguration setPropertyDataManager(PropertyDataManager propertyDataManager) {
        this.propertyDataManager = propertyDataManager;
        return this;
    }

    public AbstractEngineConfiguration setPropertyEntityManager(PropertyEntityManager propertyEntityManager) {
        this.propertyEntityManager = propertyEntityManager;
        return this;
    }

    public AbstractEngineConfiguration setSchemaCommandConfig(CommandConfig schemaCommandConfig) {
        this.schemaCommandConfig = schemaCommandConfig;
        return this;
    }

    public void setSchemaLockWaitTime(Duration schemaLockWaitTime) {
        this.schemaLockWaitTime = schemaLockWaitTime;
    }

    public AbstractEngineConfiguration setSchemaManagementCmd(Command<Void> schemaManagementCmd) {
        this.schemaManagementCmd = schemaManagementCmd;
        return this;
    }

    public AbstractEngineConfiguration setSchemaManager(SchemaManager schemaManager) {
        this.schemaManager = schemaManager;
        return this;
    }

    public AbstractEngineConfiguration
        setServiceConfigurations(Map<String, AbstractServiceConfiguration> serviceConfigurations) {
        this.serviceConfigurations = serviceConfigurations;
        return this;
    }

    public AbstractEngineConfiguration setSessionFactories(Map<Class<?>, SessionFactory> sessionFactories) {
        this.sessionFactories = sessionFactories;
        return this;
    }

    public AbstractEngineConfiguration setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        return this;
    }

    public AbstractEngineConfiguration setTableDataManager(TableDataManager tableDataManager) {
        this.tableDataManager = tableDataManager;
        return this;
    }

    public AbstractEngineConfiguration setTablePrefixIsSchema(boolean tablePrefixIsSchema) {
        this.tablePrefixIsSchema = tablePrefixIsSchema;
        return this;
    }

    public AbstractEngineConfiguration
        setTransactionContextFactory(TransactionContextFactory transactionContextFactory) {
        this.transactionContextFactory = transactionContextFactory;
        return this;
    }

    public AbstractEngineConfiguration setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
        return this;
    }

    public AbstractEngineConfiguration setTransactionsExternallyManaged(boolean transactionsExternallyManaged) {
        this.transactionsExternallyManaged = transactionsExternallyManaged;
        return this;
    }

    public AbstractEngineConfiguration
        setTypedEventListeners(Map<String, List<FlowableEventListener>> typedEventListeners) {
        this.typedEventListeners = typedEventListeners;
        return this;
    }

    public AbstractEngineConfiguration setUseClassForNameClassLoading(boolean useClassForNameClassLoading) {
        this.useClassForNameClassLoading = useClassForNameClassLoading;
        return this;
    }

    public AbstractEngineConfiguration setUseLockForDatabaseSchemaUpdate(boolean useLockForDatabaseSchemaUpdate) {
        this.useLockForDatabaseSchemaUpdate = useLockForDatabaseSchemaUpdate;
        return this;
    }

    public AbstractEngineConfiguration setUsePrefixId(boolean usePrefixId) {
        this.usePrefixId = usePrefixId;
        return this;
    }

    public AbstractEngineConfiguration setUsingRelationalDatabase(boolean usingRelationalDatabase) {
        this.usingRelationalDatabase = usingRelationalDatabase;
        return this;
    }

    public AbstractEngineConfiguration setUsingSchemaMgmt(boolean usingSchema) {
        this.usingSchemaMgmt = usingSchema;
        return this;
    }

    public AbstractEngineConfiguration setXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
        return this;
    }
}
