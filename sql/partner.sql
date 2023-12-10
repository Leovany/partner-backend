
-- ----------------------------
-- Create database
-- ----------------------------
CREATE DATABASE IF NOT EXISTS partner;

USE partner;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `username` varchar(256) DEFAULT NULL COMMENT '用户昵称',
                        `userAccount` varchar(256) DEFAULT NULL COMMENT '账号',
                        `userPasspword` varchar(512) NOT NULL COMMENT '密码',
                        `avatarUrl` varchar(1024) DEFAULT NULL COMMENT '用户头像',
                        `gender` tinyint(4) DEFAULT NULL COMMENT '性别',
                        `phone` varchar(128) DEFAULT NULL COMMENT '电话',
                        `email` varchar(512) DEFAULT NULL COMMENT '邮箱',
                        `userStatus` int(11) NOT NULL DEFAULT '0' COMMENT '状态 0=正常',
                        `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
                        `userRole` int(11) NOT NULL DEFAULT '0' COMMENT '用户角色，0=普通用户，1=管理员',
                        `planetCode` varchar(512) DEFAULT NULL COMMENT '星球编码',
                        `tags`       varchar(1024) NULL COMMENT '标签列表-json'
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1 COMMENT='用户表';