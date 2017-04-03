#!/bin/bash

# this script is autogenerated by 'ant startscripts'
# it starts a LAS2peer node providing the service 'i5.las2peer.services.versioningService.VersioningService' of this project
# pls execute it from the root folder of your deployment, e. g. ./bin/start_network.sh


java -cp "lib/*" i5.las2peer.tools.L2pNodeLauncher -p 9011 --bootstrap vermeer.informatik.rwth-aachen.de:9085 --node-id-seed 123456788 uploadStartupDirectory startService\(\'i5.las2peer.services.versioningService.VersioningService@0.1\'\)  uploadStartupDirectory\(\'etc/startup\'\) startService\(\'i5.las2peer.services.threadedCommentService.ThreadedCommentService@0.1\'\) startService\(\'i5.las2peer.services.commentManagementService.CommentManagementService@0.1\'\) interactive