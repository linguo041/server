SET MODE MySQL;

create schema duoshouji;
use duoshouji;

DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
  `brand_id` decimal(13,0) NOT NULL,
  `brand_name` varchar(45) DEFAULT NULL,
  `is_tag` bit(1) DEFAULT 0,
  `is_channel` bit(1) DEFAULT 0
);

DROP TABLE IF EXISTS `catalog_tag_map`;
CREATE TABLE `catalog_tag_map` (
  `tag_id` decimal(13,0) NOT NULL,
  `category_id` decimal(13,0) NOT NULL,
  `brand_id` decimal(13,0) NOT NULL
); 

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `category_id` decimal(13,0) NOT NULL,
  `category_name` varchar(45) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `is_tag` bit(1) DEFAULT 0,
  `is_channel` bit(1) DEFAULT 0
);

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `user_id` decimal(13,0) NOT NULL,
  `note_id` decimal(13,0) NOT NULL,
  `content` varchar(45) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  `rating` int(11) NOT NULL,
  `longitude` decimal(12,6) DEFAULT NULL,
  `latitude` decimal(12,6) DEFAULT NULL
);

DROP TABLE IF EXISTS `district`;
CREATE TABLE `district` (
  `district_id` decimal(12,0) NOT NULL,
  `district_name` varchar(255) NOT NULL
);

DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `user_id` decimal(11,0) DEFAULT NULL,
  `fan_user_id` decimal(11,0) DEFAULT NULL,
  `created_time` bigint(20) DEFAULT NULL,
  `is_activated` bit(1) DEFAULT 1
);

DROP TABLE IF EXISTS `invitation`;
CREATE TABLE `invitation` (
  `inviter_id` decimal(13,0) DEFAULT NULL,
  `invited_mobile` decimal(13,0) DEFAULT NULL
);

DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
  `user_id` decimal(11,0) NOT NULL,
  `note_id` decimal(13,0) NOT NULL,
  KEY `likes_user_id_idx` (`user_id`),
  KEY `likes_note_id_idx` (`note_id`)
);

DROP TABLE IF EXISTS `note`;
CREATE TABLE `note` (
  `note_id` decimal(13,0) NOT NULL,
  `title` varchar(50) NOT NULL,
  `content` varchar(500) DEFAULT NULL,
  `user_id` decimal(11,0) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `country_id` int(11) DEFAULT NULL,
  `is_suggest` bit(1) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `last_update_time` bigint(20) NOT NULL,
  `main_image_width` int(11) DEFAULT NULL,
  `main_image_height` int(11) DEFAULT NULL,
  `category_id` decimal(13,0) DEFAULT NULL,
  `brand_id` decimal(13,0) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `longitude` decimal(12,6) DEFAULT NULL,
  `latitude` decimal(12,6) DEFAULT NULL,
  `price` decimal(18,2) DEFAULT NULL,
  `district_id` decimal(12,0) DEFAULT NULL,
  `main_image_url` varchar(255) DEFAULT NULL,
  `main_image_marks` varchar(1024) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL
);

DROP TABLE IF EXISTS `note_extend`;
CREATE TABLE `note_extend` (
  `note_id` decimal(13,0) NOT NULL,
  `like_number` int(11) DEFAULT '0',
  `view_number` int(11) DEFAULT '0',
  `comment_number` int(11) DEFAULT '0',
  `order_number` int(11) DEFAULT '0',
  `order_rebate_amount` decimal(10,2) DEFAULT '0.00',
  `favourate_number` int(11) DEFAULT '0',
  `rating_sum` int(11) DEFAULT '0'
);

DROP TABLE IF EXISTS `note_image`;
CREATE TABLE `note_image` (
  `note_id` decimal(13,0) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `image_width` int(11) NOT NULL,
  `image_height` int(11) NOT NULL,
  `image_marks` varchar(1024) NOT NULL
);

DROP TABLE IF EXISTS `note_keyword`;
CREATE TABLE `note_keyword` (
  `note_id` decimal(13,0) DEFAULT NULL,
  `keyword` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL
);

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(50) NOT NULL,
  `item_order` int(2) NOT NULL,
  `item_id` varchar(50) NOT NULL,
  `item_title` varchar(255) NOT NULL,
  `item_price` decimal(14,2) NOT NULL,
  `pic_url` varchar(255),
  `source` varchar(10) NOT NULL DEFAULT 'Taobao'
);

DROP TABLE IF EXISTS `rating`;
CREATE TABLE `rating` (
  `id` int(11) NOT NULL,
  `category_rating_id` int(11) NOT NULL,
  `note_id` decimal(13,0) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `value` int(11) unsigned NOT NULL,
  `is_author` bit(1) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL
);

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `tag_id` decimal(13,0) NOT NULL,
  `tag_name` varchar(45) NOT NULL,
  `is_channel` bit(1) DEFAULT 0
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(60) DEFAULT NULL,
  `mobile_country_code` int(11) DEFAULT NULL,
  `user_id` decimal(11,0) NOT NULL,
  `birthday` datetime DEFAULT NULL,
  `gender` varchar(16) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `avatar_width` int(11) DEFAULT NULL,
  `avatar_height` int(11) DEFAULT NULL,
  `country_id` int(11) DEFAULT NULL,
  `province_id` int(11) DEFAULT NULL,
  `city_id` int(11) DEFAULT NULL,
  `area_id` int(11) DEFAULT NULL,
  `register_time` int(11) DEFAULT NULL,
  `register_ip` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `password_salt` varchar(10) DEFAULT NULL,
  `last_login_time` int(11) DEFAULT NULL,
  `last_login_ip` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `token` varchar(256) DEFAULT NULL
);

DROP TABLE IF EXISTS `user_extend`;
CREATE TABLE `user_extend` (
  `user_id` decimal(11,0) NOT NULL,
  `balance` decimal(10,2) unsigned DEFAULT '00000000.00',
  `note_number` int(11) DEFAULT '0',
  `order_number` int(11) DEFAULT '0',
  `follow_number` int(11) DEFAULT '0',
  `followed_number` int(11) DEFAULT '0'
);

DROP TABLE IF EXISTS `user_wechat_login`;
CREATE TABLE `user_wechat_login` (
  `mobile` bigint(20) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `create_time` bigint(20) NOT NULL,
  `token` varchar(200) NOT NULL,
  `ip` varchar(11) DEFAULT NULL,
  `expired` bit(1) DEFAULT 0
);

drop view if exists duoshouji.v_square_notes;
create view duoshouji.v_square_notes as
select c.note_id, c.content, c.address, c.longitude, c.latitude, c.category_id, c.title, coalesce(c.rating, 0) owner_rating, coalesce(a2.rating_sum, 0) comment_rating, c.main_image_url, c.main_image_width, c.main_image_height, c.main_image_marks, c.create_time
  , coalesce(a2.comment_number, 0) comment_number, coalesce(a2.like_number, 0) like_number, coalesce(a2.order_number, 0) order_number,
  u.user_id, u.user_name, u.avatar_url, u.avatar_width, u.avatar_height, u.gender
from duoshouji.note c left join
  duoshouji.note_extend a2 on c.note_id = a2.note_id left join
  duoshouji.user u on c.user_id = u.user_id
;

DROP VIEW IF EXISTS `v_user`;
CREATE VIEW `v_user` AS select `c`.`user_id` AS `user_id`,`c`.`user_name` AS `user_name`,`c`.`password` AS `password`,`c`.`avatar_url` AS `avatar_url`,`c`.`avatar_width` AS `avatar_width`,`c`.`avatar_height` AS `avatar_height`,`c`.`gender` AS `gender`,`a`.`balance` AS `balance`,`a`.`note_number` AS `note_number`,`a`.`order_number` AS `order_number`,`a`.`follow_number` AS `follow_number`,`a`.`followed_number` AS `followed_number` from (`user` `c` join `user_extend` `a`) where (`c`.`user_id` = `a`.`user_id`) ;
