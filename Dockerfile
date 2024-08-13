# build docker image from nvidia/cuda image with CUDA 11.0
FROM nvidia/cuda:11.0.3-cudnn8-runtime-ubuntu20.04

## SET UP ##

# Working Directory / Environment:
ADD . /qp_dsa_plug
WORKDIR /qp_dsa_plug

# https://github.com/gradle/gradle/issues/3117#issuecomment-336192694
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'


# https://stackoverflow.com/questions/51023312/docker-having-issues-installing-apt-utils
ARG DEBIAN_FRONTEND=noninteractive

# This tells girder_worker to enable gpu if possible
LABEL com.nvidia.volumes.needed=nvidia_driver

LABEL "name"="qp_dsa_plugin"
LABEL "description"="QuPath Digital Slide Archive (DSA) CLI Plugin"

# # INSTALLS
RUN apt-get update && \
    apt-get install -y binutils git python3-pip memcached

# QuPath Linux App Downloaded from https://github.com/qupath/qupath/releases/tag/v0.5.1
## LOCATION qp_dsa_plugin/qpbin

# Install HistomicsTK ( for HistomicsUI communication )
RUN pip install histomicstk --find-links https://girder.github.io/large_image_wheels

WORKDIR /qp_dsa_plug/cli

# Test Entrypoints:
RUN python3 -m slicer_cli_web.cli_list_entrypoint --list_cli

# define docker entrypoint
ENTRYPOINT ["/bin/bash", "docker-entrypoint.sh"]