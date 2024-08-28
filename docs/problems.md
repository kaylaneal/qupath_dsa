# Things Broke and We Fixed It .. Maybe
Problems (and sometimes solutions!) and general debugging scenerios we've run into during development.

## Passing Command Line Arguments in QuPath Script?
In each program, arguments defined by the XML script and inputted through the DSA need to be passed from the Python file (acting as the bridge between DSA and QuPath) to the Groovy script. 

- In the Python file: when calling the Groovy script, arguments can be passed using the `-a` or `--args` flag
    - Arguments are expected to be Pathlike or strings
        - ROI arguments need to be stringified to work 

- In the Groovy file: the `args` keyword accesses all of the passed arguments in list like fashion
    - Ex) Given the command `./Qupath script Module/Module.groovy --image args.inputImage -a args.outputFile`, the Groovy file will access the outputFile path with `args[0]`
        - it is important to note that the command 'args' is from the XML script associated with the module, 'args' in the Groovy script refers to values passed with the `-a` flag
            - using `args.outputFile` in Groovy would be incorrect and should error out.

## Command Line file path is different from Error file path?
- **Example**:
    - Command Line file path arguments: [/mnt/girder_worker/97a68ce5cd3242e0b339b7a9c0467971/Tissue Thresholder-outputAnnotationFile.geojson, /mnt/girder_worker/97a68ce5cd3242e0b339b7a9c0467971/Tissue Thresholder-outputLabelImage.tiff] 
    - Error: FileNotFoundError: [Errno 2] No such file or directory: '/var/folders/0f/87hss8fd63q2chdb_15kmn200000gn/T/tmpq_xvofvy/Tissue Thresholder-outputAnnotationFile.geojson'

- **Problem + Next Steps**:
    - /mnt/girder_worker/... path designates running *inside* the docker container whereas /var/folders/... path is running *outside* of the docker container.
        - Did the job succeed? Did the files that were supposed to be made get made?
            - Tested locally inside of docker container -- Groovy script returned 'Killed' and files were NOT made 
            - Issue is with Groovy script - if the files don't get made, then there wouldn't be a file found
                - How are files being exported? Is it the correct steps? Is there file type restrictions? Is there a memory error?

- **Solution**:
    - There was likely a memory issue with the size of image the script was trying to create -- and it was an unnecessary image to create! That output expectation was removed entirely.


## Slicer CLI Failure to Load Docker Image
- **Example**:
Started to Load Docker images
FAILURE: Error with recently loading pre-existing image qupath-dsa:latest
Error getting qupath-dsa:latest cli data from image Attempt to docker run qupath-dsa:latest ['TestMod2', '--xml'] failed (image name: qupath-dsa:latest ) (image name: None )
Finished caching Docker image data

- **Problem + Next Steps**:
    - Run `docker run qupath-dsa:latest TestMod2 --xml` locally to see where error occurs.
    - Does *not* mean the XML file is where the problem is

## ROI Annotation Returned, Classifier Annotations Not
- **Example**:
    - Running TissueDetect with ROI specified -- the defined analysis ROI is returned but the results from the thresholder are not. 

- **Problem + Solution**:
    - Multiple Annotations from QuPath need to be specified as a Feature Collection otherwise only the first annotation is considered.
        - https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html