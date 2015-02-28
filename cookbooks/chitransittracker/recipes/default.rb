#
# Cookbook Name:: chitransittracker
# Recipe:: default
#
# Copyright 2015, Chitransittracker
#
# All rights reserved - Do Not Redistribute
#
# your custom cookbook's database recipe
include_recipe 'postgresql::server'
include_recipe 'database::postgresql'

postgresql_connection_info = {
	:host     => '127.0.0.1',
	:port     => node['postgresql']['config']['port'],
	:username => 'postgres',
	:password => node['postgresql']['password']['postgres']
}

postgresql_database 'chitransittracker' do
	connection postgresql_connection_info
  	action :create
end

postgresql_database_user 'admin' do
  connection postgresql_connection_info
  password   'cta'
  action     :create
end

postgresql_database_user 'admin' do
  connection    postgresql_connection_info
  database_name 'chitransittracker'
  privileges    [:all]
  action        :grant
end