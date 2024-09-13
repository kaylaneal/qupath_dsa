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


## Helpful Links from Development:
- https://cmilab.nephrology.medicine.ufl.edu/wordpress/files/2023/10/creating-a-plugin.pdf
- https://www.slicer.org/w/index.php?title=Documentation/Nightly/Developers/SlicerExecutionModel#XML_Schema
- https://github.com/DigitalSlideArchive/HistomicsUI/tree/master
- https://qupath.github.io/javadoc/docs/index.html