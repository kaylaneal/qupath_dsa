# QuPath CLI Plugin Module for DSA via HistomicsUI

## Set Up
The **QuPath-DSA** Plugin Dockerfile is located in the [base directory](../Dockerfile)
- QuPath for Linux is expected to be downloaded in the project directory *BEFORE* building Docker image.
    - QuPath Downloading Instructions: [QuPath GitHub](https://github.com/qupath/qupath/releases/tag/v0.5.1)
        - *Working on ideas to include in Dockerfile itself so this step is unnecessary*
    - Locally saved to `qpbin` folder within base directory

## Uploading CLI Module to DSA
- Starting at the DSA Homepage (site/dsa) 
    - Select Collections Tab on left-hand side (site/dsa#collections)
    - Select Tasks Folder (site/dsa#collection/taskFolderID)
    - Select SubFolder "Slicer CLI Web Tasks" (site/dsa#collection/taskFolderID/folder/slicercliwebtaskFolderID)
        - On this page the first item should be a dsardchive/histomicstk folder. 
        - To add a module, click on the white upload buttom with "CLI" 
            - Third button from the left on the right-hand side of the site
            - In the pop-up, enter the Docker image name (image:tag) of the CLI Module
            - Click "Import Image"

## Writing CLI Programs
- Each CLI Program should be in it's own folder within the [cli](../cli/) folder
    - The CLI folder contains: a module folder for each program, `docker-entrypoint.sh`, and `slicer_cli_list.json`
        - Each module folder includes: `Module.py` and `Module.xml` files
            - QuPath uses [Groovy](https://groovy-lang.org/) for scripting. Groovy is not supported in Slicer CLI. The program script is thus written in Groovy.
            - The Python file is the bridge between Groovy and Slicer.
            - The XML file should be to DSA specifications. The [DSA's HistomicsTK](https://github.com/DigitalSlideArchive/HistomicsTK) XML files were used as examples/templates.
        - The programs listed in `slicer_cli_list.json` will be the ones avaliable for use on the DSA, any modules not listed won't appear.

- XML Format
    - To make parameters optional, use `<longflag>` in the variable definition. 
    - To make parameters required, use `<index>` in the variable definition. 
    - The `<name>` flag is the argument name that can be used in the python file.

- Python Script
    - Required to cooporate with Slicer CLI.
    - Should take arguments defined in the corresponding XML file and use them to run the Groovy script that utilizes QuPath.
    - Pre/Post-Processing to ensure input/output matches DSA expectations.

- Groovy Script:
    - Runs algorithm that utilizes QuPath.
    - Should be able to handle input arguments.
    - Should generate output files.
