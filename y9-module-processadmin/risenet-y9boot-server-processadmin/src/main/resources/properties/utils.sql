select 'alter table ' || table_name || ' move tablespace lhoa  ;'
from user_tables;

select 'alter index ' || index_name || ' rebuild tablespace lhoa  ;'
from user_indexes
where index_type !='LOB';

select 'alter table ' || t.TABLE_NAME || ' move lob(' || wm_concat(t.COLUMN_NAME) || ') store as (tablespace lhoa);'
from user_tab_columns t
where t.DATA_TYPE = 'CLOB'
  and t.TABLE_NAME in (select table_name from user_indexes where index_type = 'LOB')
group by t.TABLE_NAME;

alter
user lhoa default tablespace lhoa temporary tablespace temp;


revoke unlimited tablespace from approve;
alter
user approve quota 0 on users;
 alter
user approve quota unlimited on approve;

select 'alter table ' || table_name || ' allocate extent;'
from user_tables
where num_rows = 0;