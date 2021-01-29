-- Create Schema
DROP SCHEMA IF EXISTS `dtf_demo_account_1`;
CREATE SCHEMA `dtf_demo_account_1` ;

CREATE TABLE `dtf_demo_account_1`.`dtf_demo_account` (
  `id` int(11) NOT NULL COMMENT '账号ID',
  `balance` int(11) NOT NULL COMMENT '余额',
  `frozen` int(11) NOT NULL COMMENT '冻结金额',
  `incoming` int(11) NOT NULL COMMENT '应收金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Demo账户表';

INSERT INTO `dtf_demo_account_1`.`dtf_demo_account` (`id`, `balance`, `frozen`, `incoming`) VALUES (1, 10000, 0, 0);

CREATE TABLE `dtf_demo_account_1`.`dtf_demo_account_tmp` (
  `tx_id` varchar(30) NOT NULL COMMENT '主事务ID',
  `branch_id` bigint(20) NOT NULL COMMENT '子事务ID',
  `account_id` int(11) NOT NULL COMMENT '账户ID',
  `amount` int(11) NOT NULL COMMENT '变动金额',
  PRIMARY KEY (`tx_id`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create Schema
DROP SCHEMA IF EXISTS `dtf_demo_account_2`;
CREATE SCHEMA `dtf_demo_account_2`;

CREATE TABLE `dtf_demo_account_2`.`dtf_demo_account` (
  `id` int(11) NOT NULL COMMENT '账号ID',
  `balance` int(11) NOT NULL COMMENT '余额',
  `frozen` int(11) NOT NULL COMMENT '冻结金额',
  `incoming` int(11) NOT NULL COMMENT '应收金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Demo账户表';

INSERT INTO `dtf_demo_account_2`.`dtf_demo_account` (`id`, `balance`, `frozen`, `incoming`) VALUES (2, 10000, 0, 0);

CREATE TABLE `dtf_demo_account_2`.`dtf_demo_account_tmp` (
  `tx_id` bigint(20) NOT NULL COMMENT '主事务ID',
  `branch_id` bigint(20) NOT NULL COMMENT '子事务ID',
  `account_id` int(11) NOT NULL COMMENT '账户ID',
  `amount` int(11) NOT NULL COMMENT '变动金额',
  PRIMARY KEY (`tx_id`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create Schema
DROP SCHEMA IF EXISTS `dtf_demo_order`;
CREATE SCHEMA `dtf_demo_order` ;

-- Create Schema
DROP SCHEMA IF EXISTS `dtf_demo_inventory`;
CREATE SCHEMA `dtf_demo_inventory` ;

-- Create Schema
DROP SCHEMA IF EXISTS `dtf_demo_payment`;
CREATE SCHEMA `dtf_demo_payment` ;

-- Create Schema
DROP SCHEMA IF EXISTS `dtf_demo_point`;
CREATE SCHEMA `dtf_demo_point` ;

CREATE TABLE `dtf_demo_order`.`dtf_demo_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `qty` int(11) NOT NULL COMMENT '数量',
  `account_id` int(11) NOT NULL COMMENT '账户ID',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单状态：0处理中；1下单成功；2下单失败',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

CREATE TABLE `dtf_demo_order`.`dtf_demo_order_tmp` (
  `tx_id` bigint(20) NOT NULL COMMENT '事务ID',
  `branch_id` bigint(20) NOT NULL COMMENT '分支事务ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  PRIMARY KEY (`tx_id`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单临时表';

CREATE TABLE `dtf_demo_inventory`.`dtf_demo_inventory` (
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `qty` int(11) NOT NULL COMMENT '产品库存',
  `frozen` int(11) NOT NULL COMMENT '冻结库存',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='库存表';

INSERT INTO `dtf_demo_inventory`.`dtf_demo_inventory` (`product_id`, `qty`, `frozen`) VALUES ('1', '1000', '0');
INSERT INTO `dtf_demo_inventory`.`dtf_demo_inventory` (`product_id`, `qty`, `frozen`) VALUES ('2', '1000', '0');
INSERT INTO `dtf_demo_inventory`.`dtf_demo_inventory` (`product_id`, `qty`, `frozen`) VALUES ('3', '1000', '0');
INSERT INTO `dtf_demo_inventory`.`dtf_demo_inventory` (`product_id`, `qty`, `frozen`) VALUES ('4', '1000', '0');
INSERT INTO `dtf_demo_inventory`.`dtf_demo_inventory` (`product_id`, `qty`, `frozen`) VALUES ('5', '1000', '0');

CREATE TABLE `dtf_demo_inventory`.`dtf_demo_inventory_tmp` (
  `tx_id` bigint(20) NOT NULL COMMENT '事务ID',
  `branch_id` bigint(20) NOT NULL COMMENT '分支事务ID',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `qty` int(11) NOT NULL COMMENT '变动数量',
  PRIMARY KEY (`tx_id`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品库存对账表';

CREATE TABLE `dtf_demo_payment`.`dtf_demo_account` (
  `account_id` int(11) NOT NULL COMMENT '账户ID',
  `balance` int(11) NOT NULL COMMENT '账户余额',
  `frozen` int(11) NOT NULL DEFAULT '0' COMMENT '冻结金额',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户表';

CREATE TABLE `dtf_demo_payment`.`dtf_demo_account_tmp` (
  `tx_id` bigint(20) NOT NULL COMMENT '事务ID',
  `branch_id` bigint(20) NOT NULL COMMENT '分支事务ID',
  `account_id` int(11) NOT NULL COMMENT '账户ID',
  `amount` int(11) NOT NULL COMMENT '交易金额',
  PRIMARY KEY (`tx_id`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户临时表';

INSERT INTO `dtf_demo_payment`.`dtf_demo_account` (`account_id`, `balance`) VALUES ('1', '1000');
INSERT INTO `dtf_demo_payment`.`dtf_demo_account` (`account_id`, `balance`) VALUES ('2', '1000');
INSERT INTO `dtf_demo_payment`.`dtf_demo_account` (`account_id`, `balance`) VALUES ('3', '1000');
INSERT INTO `dtf_demo_payment`.`dtf_demo_account` (`account_id`, `balance`) VALUES ('4', '1000');
INSERT INTO `dtf_demo_payment`.`dtf_demo_account` (`account_id`, `balance`) VALUES ('5', '1000');

CREATE TABLE `dtf_demo_point`.`dtf_demo_point` (
  `account_id` int(11) NOT NULL COMMENT '账号ID',
  `point` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `frozen` int(11) NOT NULL DEFAULT '0' COMMENT '冻结积分',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分表';

CREATE TABLE `dtf_demo_point`.`dtf_demo_point_tmp` (
  `tx_id` bigint(20) NOT NULL COMMENT '事务ID',
  `branch_id` bigint(20) NOT NULL COMMENT '分支事务ID',
  `account_id` int(11) NOT NULL COMMENT '账户ID',
  `point` int(11) NOT NULL COMMENT '积分',
  PRIMARY KEY (`tx_id`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分临时表';

INSERT INTO `dtf_demo_point`.`dtf_demo_point` (`account_id`) VALUES ('1');
INSERT INTO `dtf_demo_point`.`dtf_demo_point` (`account_id`) VALUES ('2');
INSERT INTO `dtf_demo_point`.`dtf_demo_point` (`account_id`) VALUES ('3');
INSERT INTO `dtf_demo_point`.`dtf_demo_point` (`account_id`) VALUES ('4');
INSERT INTO `dtf_demo_point`.`dtf_demo_point` (`account_id`) VALUES ('5');
