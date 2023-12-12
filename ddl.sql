-- Create syntax for TABLE 'm2m_course'
CREATE TABLE `m2m_course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '课程名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程\n@R;@M=relationsamples;@A=many2many;@T=Course;';

-- Create syntax for TABLE 'm2m_student'
CREATE TABLE `m2m_student` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '学生名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生\n@R;@M=relationsamples;@A=many2many;@T=Student;';

-- Create syntax for TABLE 'm2m_student_course_rel'
CREATE TABLE `m2m_student_course_rel` (
  `course_id` bigint(20) NOT NULL COMMENT '@Ref=m2m_course;',
  `student_id` bigint(20) NOT NULL COMMENT '@Ref=m2m_student;',
  KEY `FKpkxug8kv2dg01jwumitfu74gn` (`student_id`),
  KEY `FKfpbkburlhwlvlttbnp8gbf5tx` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='@Rel=ManyToMany;';

-- Create syntax for TABLE 'o2m_member'
CREATE TABLE `o2m_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '成员名称',
  `team_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '@Rel=ManyToOne;@Ref=o2m_team;',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='成员\n@P=o2m_team;@M=relationsamples;@A=one2many;@T=Member;';

-- Create syntax for TABLE 'o2m_team'
CREATE TABLE `o2m_team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '团队名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='团队\n@R;@M=relationsamples;@A=one2many;@T=Team;';

-- Create syntax for TABLE 'account'
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '账户名称',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '账户余额',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户\n@R;@M=samples;';

-- Create syntax for TABLE 'transfer'
CREATE TABLE `transfer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(100) NOT NULL DEFAULT '0' COMMENT '关联账户',
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  `biz_type` int(11) NOT NULL DEFAULT '0' COMMENT '业务类型',
  `biz_id` varchar(20) NOT NULL DEFAULT '0' COMMENT '业务编码',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '转账金额',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='转账记录\n@R;@M=samples;';

-- Create syntax for TABLE 'order'
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '订单金额',
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '订单标题',
  `owner` varchar(100) NOT NULL DEFAULT '' COMMENT '下单人',
  `finished` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否完成',
  `closed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否关闭',
  `update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT '0',
  `db_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单\n@R;@M=samples;';

-- Create syntax for TABLE 'order_item'
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '订单项名称',
  `price` int(11) NOT NULL DEFAULT '0' COMMENT '单价',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '@Rel=ManyToOne;',
  `db_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单项\n@P=order;@M=samples;';

-- Create syntax for TABLE 'bill'
CREATE TABLE `bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL COMMENT '账单名称',
  `owner` varchar(100) NOT NULL COMMENT '支付人',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '账单金额',
  `payed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否支付',
  `closed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否关闭',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账单\n@R;@M=samples;';

-- Create syntax for TABLE '__event'
CREATE TABLE `__event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `svc_name` varchar(255) NOT NULL DEFAULT '',
  `data` text,
  `data_type` varchar(255) NOT NULL DEFAULT '',
  `event_state` int(11) NOT NULL DEFAULT '0',
  `event_type` varchar(255) NOT NULL DEFAULT '',
  `expire_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_try_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `next_try_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `tried_times` int(11) NOT NULL DEFAULT '0',
  `try_times` int(11) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `db_created_at`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  KEY `idx_event_type` (`event_type`,`svc_name`),
  KEY `idx_create_at` (`create_at`),
  KEY `idx_expire_at` (`expire_at`),
  KEY `idx_next_try_time` (`next_try_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集成事件\n@I;'
partition by range(to_days(db_created_at))
(partition p202201 values less than (to_days('2022-02-01')) ENGINE=InnoDB);

-- Create syntax for TABLE '__saga'
CREATE TABLE `__saga` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biz_type` int(11) NOT NULL DEFAULT '0',
  `svc_name` varchar(255) NOT NULL DEFAULT '',
  `context_data` text,
  `context_data_type` varchar(255) NOT NULL DEFAULT '',
  `saga_state` int(11) NOT NULL DEFAULT '0',
  `expire_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_try_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `next_try_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `tried_times` int(11) NOT NULL DEFAULT '0',
  `try_times` int(11) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `db_created_at`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  KEY `idx_biz_type` (`biz_type`,`svc_name`),
  KEY `idx_create_at` (`create_at`),
  KEY `idx_expire_at` (`expire_at`),
  KEY `idx_next_try_time` (`next_try_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SAGA事务\n@I;'
partition by range(to_days(db_created_at))
(partition p202201 values less than (to_days('2022-02-01')) ENGINE=InnoDB);

-- Create syntax for TABLE '__saga_process'
CREATE TABLE `__saga_process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `saga_id` bigint(20) NOT NULL DEFAULT '0',
  `process_code` int(11) NOT NULL DEFAULT '0',
  `process_name` varchar(255) NOT NULL DEFAULT '',
  `context_data` text,
  `process_state` int(11) NOT NULL DEFAULT '0',
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tried_times` int(11) NOT NULL DEFAULT '0',
  `try_times` int(11) NOT NULL DEFAULT '0',
  `last_try_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exception` varchar(255) NOT NULL DEFAULT '',
  `db_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `db_created_at`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  KEY `idx_saga_id` (`saga_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SAGA事务-子环节\n@I;'
partition by range(to_days(db_created_at))
(partition p202201 values less than (to_days('2022-02-01')) ENGINE=InnoDB);

CREATE TABLE `__archived_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `svc_name` varchar(255) NOT NULL DEFAULT '',
  `data` text,
  `data_type` varchar(255) NOT NULL DEFAULT '',
  `event_state` int(11) NOT NULL DEFAULT '0',
  `event_type` varchar(255) NOT NULL DEFAULT '',
  `expire_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_try_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `next_try_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `tried_times` int(11) NOT NULL DEFAULT '0',
  `try_times` int(11) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `db_created_at`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  KEY `idx_event_type` (`event_type`,`svc_name`),
  KEY `idx_create_at` (`create_at`),
  KEY `idx_expire_at` (`expire_at`),
  KEY `idx_next_try_time` (`next_try_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集成事件存档\n@I;'
partition by range(to_days(db_created_at))
(partition p202201 values less than (to_days('2022-02-01')) ENGINE=InnoDB);

CREATE TABLE `__archived_saga` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biz_type` int(11) NOT NULL DEFAULT '0',
  `svc_name` varchar(255) NOT NULL DEFAULT '',
  `context_data` text,
  `context_data_type` varchar(255) NOT NULL,
  `saga_state` int(11) NOT NULL DEFAULT '0',
  `expire_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_try_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `next_try_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `tried_times` int(11) NOT NULL DEFAULT '0',
  `try_times` int(11) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT '0',
  `db_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `db_created_at`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  KEY `idx_biz_type` (`biz_type`,`svc_name`),
  KEY `idx_create_at` (`create_at`),
  KEY `idx_expire_at` (`expire_at`),
  KEY `idx_next_try_time` (`next_try_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SAGA事务存档\n@I;'
partition by range(to_days(db_created_at))
(partition p202201 values less than (to_days('2022-02-01')) ENGINE=InnoDB);

CREATE TABLE `__archived_saga_process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `saga_id` bigint(20) NOT NULL DEFAULT '0',
  `process_code` int(11) NOT NULL DEFAULT '0',
  `process_name` varchar(255) NOT NULL DEFAULT '',
  `context_data` text,
  `process_state` int(11) NOT NULL DEFAULT '0',
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tried_times` int(11) NOT NULL DEFAULT '0',
  `try_times` int(11) NOT NULL DEFAULT '0',
  `last_try_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exception` varchar(255) NOT NULL DEFAULT '',
  `db_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `db_created_at`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  KEY `idx_saga_id` (`saga_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SAGA事务存档-子环节\n@I;'
partition by range(to_days(db_created_at))
(partition p202201 values less than (to_days('2022-02-01')) ENGINE=InnoDB);

CREATE TABLE `__locker` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `pwd` varchar(100) NOT NULL DEFAULT '',
  `lock_at` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `unlock_at` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `db_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `db_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_db_created_at` (`db_created_at`),
  KEY `idx_db_updated_at` (`db_updated_at`),
  UNIQUE `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='锁\n@I;';