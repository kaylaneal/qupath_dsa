# QuPath DSA Support Module

## INSTALL
To build docker image: `docker build -t qp-dsa .`
- to test locally run `docker run -it --entrypoint /bin/bash qp-dsa` to enter docker container. 


## USAGE
Command line scripts are in [cli](/cli/) folder

- TestMod
    - Proof of concept/testing
    - Used as a sandbox
    - *Should be unincluded in final*

- TissueDetect
    - Create tissue mask from pixel classifier

Scripts may have file input requirements:
- testing locally: have files saved somewhere accessible from docker container
    - Pixel Classifier used in TissueDetect is saved in [classifiers](/classifiers/) folder 
- on DSA: necessary files should be saved on the DSA directly
    - currently: uploaded the classifiers folder under the Admin user account, selectable from HistomicsUI window