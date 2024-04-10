#!/bin/bash

# Variables
bucket_name="lottery-s3-1"
file_name="marketplace-0.0.1-SNAPSHOT.jar"
local_file_location="/home/ec2-user/"

# Install dependencies. ex.: Java JDK 21
sudo yum update -y
sudo yum install java-21-amazon-corretto-devel -y

# Navigate to the directory where you want to download your jar file
cd $local_file_location

# Download the jar file from S3
aws s3 cp s3://$bucket_name/$file_name .

# Run the jar file
sudo nohup java -Dspring.profiles.active=prod -jar $file_name > output.log 2>&1 &